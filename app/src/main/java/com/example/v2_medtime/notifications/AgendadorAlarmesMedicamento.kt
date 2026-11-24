package com.example.v2_medtime.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Base64
import com.example.v2_medtime.data.local.NotificacaoStorage
import com.example.v2_medtime.data.local.TokenManager
import com.example.v2_medtime.data.model.AlarmeMedicamento
import com.example.v2_medtime.data.remote.RetrofitClient
import com.example.v2_medtime.data.remote.texto
import com.example.v2_medtime.data.repository.ApiNotificacaoRepository
import com.example.v2_medtime.data.repository.NotificacaoRepository
import com.example.v2_medtime.data.repository.objetos
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object AgendadorAlarmesMedicamento {

    suspend fun sincronizarAlarmes(
        context: Context,
        repository: NotificacaoRepository = ApiNotificacaoRepository(context)
    ) {
        val appContext = context.applicationContext
        RetrofitClient.inicializar(appContext)

        val sessao = TokenManager(appContext).obterSessao()
        if (sessao == null || sessao.idUsuario <= 0L) {
            cancelarTodosAlarmes(appContext)
            return
        }

        val role = runCatching {
            val parte = sessao.token.split('.')[1]
            val bytes = Base64.decode(parte, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            JsonParser.parseString(String(bytes, Charsets.UTF_8))
                .asJsonObject.get("role")?.takeUnless { it.isJsonNull }?.asInt
        }.getOrNull()

        if (role == 1) {
            cancelarTodosAlarmes(appContext)
            return
        }

        val alarmes = buscarDosesFuturas(sessao.idUsuario) ?: return
        sincronizarAlarmes(appContext, alarmes)
    }

    fun sincronizarAlarmes(context: Context, alarmes: List<AlarmeMedicamento>) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val storage = NotificacaoStorage(context)
        val agoraMillis = System.currentTimeMillis()

        val idsAnteriores: Set<String> = storage.obterIdsAlarmesAgendados()
        val novosIds = mutableSetOf<String>()

        val podeExato = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                alarmManager.canScheduleExactAlarms()

        for (alarme in alarmes) {
            val gatilhoMillis = runCatching {
                Instant.parse(alarme.horarioProgramadoIso).toEpochMilli()
            }.getOrNull() ?: continue

            if (gatilhoMillis <= agoraMillis) continue

            val pendingReceiver = criarPendingIntentReceiver(context, alarme)
            if (podeExato) {
                try {
                    val pendingTela = PendingIntent.getActivity(
                        context,
                        alarme.notificacaoId,
                        NotificacaoHelper.criarIntentTelaAlarme(context, alarme),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    val infoDespertador = AlarmManager.AlarmClockInfo(gatilhoMillis, pendingTela)
                    alarmManager.setAlarmClock(infoDespertador, pendingReceiver)
                } catch (_: SecurityException) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        gatilhoMillis,
                        pendingReceiver
                    )
                }
            }
            novosIds.add(alarme.notificacaoId.toString())
        }

        for (idAntigo in idsAnteriores - novosIds) {
            idAntigo.toIntOrNull()?.let { idInt ->
                cancelarAlarmePorId(context, alarmManager, idInt)
            }
        }

        storage.salvarIdsAlarmesAgendados(novosIds)
    }

    fun cancelarTodosAlarmes(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val storage = NotificacaoStorage(context)
        for (id in storage.obterIdsAlarmesAgendados()) {
            id.toIntOrNull()?.let { idInt ->
                cancelarAlarmePorId(context, alarmManager, idInt)
            }
        }
        storage.salvarIdsAlarmesAgendados(emptySet())
    }

    private suspend fun buscarDosesFuturas(idUsuario: Long): List<AlarmeMedicamento>? {
        val fuso = ZoneId.systemDefault()
        val hoje = LocalDate.now(fuso)
        val amanha = hoje.plusDays(1)
        val agora = Instant.now()
        val formatadorHora = DateTimeFormatter.ofPattern("HH:mm")

        val receitasPorId: Map<Long, JsonObject> = runCatching {
            val respReceitas = RetrofitClient.api.listarReceitas(idUsuario)
            if (respReceitas.code() == 404) {
                emptyMap()
            } else {
                respReceitas.objetos("receita_medica", "Não foi possível carregar as receitas.")
                    .mapNotNull { receita ->
                        val id = receita.texto("id_receita_medica")?.toLongOrNull() ?: return@mapNotNull null
                        id to receita
                    }
                    .toMap()
            }
        }.getOrDefault(emptyMap())

        val dosesAgenda = mutableListOf<JsonObject>()
        var obteveRespostaValida = false

        for (data in listOf(hoje, amanha)) {
            val resp = runCatching {
                RetrofitClient.api.consultarAgenda(idUsuario, data.toString(), fuso.id)
            }.getOrNull() ?: continue

            if (resp.code() == 404) {
                obteveRespostaValida = true
                continue
            }

            runCatching {
                dosesAgenda.addAll(resp.objetos("agenda", "Não foi possível carregar a agenda."))
                obteveRespostaValida = true
            }
        }

        if (!obteveRespostaValida) return null

        return dosesAgenda.mapNotNull { dose ->
            val status = dose.texto("status").orEmpty()
            if (status == "TOMADO") return@mapNotNull null

            val idReceita = dose.texto("id_receita_medica")?.toLongOrNull() ?: return@mapNotNull null
            val horarioIso = dose.texto("horario_programado") ?: return@mapNotNull null
            val instante = runCatching { Instant.parse(horarioIso) }.getOrNull() ?: return@mapNotNull null
            if (!instante.isAfter(agora)) return@mapNotNull null

            val receita = receitasPorId[idReceita]
            val nome = dose.texto("nome_medicamento")
                ?: receita?.texto("nome_medicamento")
                ?: "Medicamento"
            val dosagem = listOfNotNull(
                dose.texto("dosagem")?.toBigDecimalOrNull()?.stripTrailingZeros()?.toPlainString(),
                dose.texto("tipo_dosagem")
            ).joinToString(" ")
            val imagemUrl = receita?.texto("imagem")?.takeIf { it.isNotBlank() }
            val notificacaoId = ("$idReceita|$horarioIso".hashCode() and 0x7FFFFFFF).coerceAtLeast(1000)

            AlarmeMedicamento(
                notificacaoId = notificacaoId,
                idReceita = idReceita,
                nomeMedicamento = nome,
                dosagemTexto = dosagem,
                horarioFormatado = instante.atZone(fuso).format(formatadorHora),
                horarioProgramadoIso = horarioIso,
                imagemUrl = imagemUrl
            )
        }.distinctBy { "${it.idReceita}_${it.horarioProgramadoIso}" }
    }

    private fun cancelarAlarmePorId(context: Context, alarmManager: AlarmManager, id: Int) {
        val intent = Intent(LembreteMedicamentoReceiver.ACTION_DISPARAR_ALARME).apply {
            component = ComponentName(context, LembreteMedicamentoReceiver::class.java)
        }
        val pending = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pending != null) {
            alarmManager.cancel(pending)
            pending.cancel()
        }
    }

    private fun criarPendingIntentReceiver(
        context: Context,
        alarme: AlarmeMedicamento
    ): PendingIntent {
        val intent = Intent(LembreteMedicamentoReceiver.ACTION_DISPARAR_ALARME).apply {
            component = ComponentName(context, LembreteMedicamentoReceiver::class.java)
            putExtra(NotificacaoHelper.EXTRA_NOTIFICACAO_ID, alarme.notificacaoId)
            putExtra(NotificacaoHelper.EXTRA_ID_RECEITA, alarme.idReceita)
            putExtra(NotificacaoHelper.EXTRA_NOME_MEDICAMENTO, alarme.nomeMedicamento)
            putExtra(NotificacaoHelper.EXTRA_DOSAGEM_TEXTO, alarme.dosagemTexto)
            putExtra(NotificacaoHelper.EXTRA_HORARIO_FORMATADO, alarme.horarioFormatado)
            putExtra(NotificacaoHelper.EXTRA_HORARIO_PROGRAMADO, alarme.horarioProgramadoIso)
            putExtra(NotificacaoHelper.EXTRA_IMAGEM_URL, alarme.imagemUrl)
        }
        return PendingIntent.getBroadcast(
            context,
            alarme.notificacaoId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
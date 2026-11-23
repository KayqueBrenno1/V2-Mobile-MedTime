package com.example.v2_medtime.data.repository

import android.content.Context
import com.example.v2_medtime.data.local.NotificacaoStorage
import com.example.v2_medtime.data.local.TokenManager
import com.example.v2_medtime.data.model.AlertaNotificacaoItem
import com.example.v2_medtime.data.model.DoseAgendadaNotificacao
import com.example.v2_medtime.data.model.TipoAlertaNotificacao
import com.example.v2_medtime.data.remote.RetrofitClient
import com.example.v2_medtime.data.remote.confirmouSucesso
import com.example.v2_medtime.data.remote.texto
import com.google.gson.JsonParser
import android.util.Base64
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

interface NotificacaoRepository {
    suspend fun obterProximasDosesParaAlarme(idUsuario: Long): Result<List<DoseAgendadaNotificacao>>
    suspend fun verificarAlertasSegundoPlano(): Result<List<AlertaNotificacaoItem>>
    fun marcarAlertaComoEnviado(chaveUnica: String)
    fun limparDadosLocais()
}

class ApiNotificacaoRepository(
    context: Context
) : NotificacaoRepository {

    private val appContext = context.applicationContext
    private val api get() = RetrofitClient.api
    private val storage = NotificacaoStorage(appContext)
    private val tokenManager = TokenManager(appContext)

    override suspend fun obterProximasDosesParaAlarme(
        idUsuario: Long
    ): Result<List<DoseAgendadaNotificacao>> = requisitar {
        if (idUsuario <= 0L) return@requisitar emptyList()

        val fuso = ZoneId.systemDefault()
        val hoje = LocalDate.now(fuso)
        val amanha = hoje.plusDays(1)
        val agora = Instant.now()
        val formatadorHora = DateTimeFormatter.ofPattern("HH:mm")

        val imagensPorReceita = runCatching {
            val respReceitas = api.listarReceitas(idUsuario)
            if (respReceitas.code() == 404) {
                emptyMap()
            } else {
                respReceitas.objetos("receita_medica", "Erro ao carregar receitas.")
                    .mapNotNull { item ->
                        val receita = item.getAsJsonObject("receita_medica")
                        val medicamento = item.getAsJsonObject("medicamento")
                        val idRec = (item.texto("id_receita_medica")
                            ?: receita?.texto("id_receita_medica")
                            ?: receita?.texto("id"))?.toLongOrNull() ?: return@mapNotNull null
                        val img = (item.texto("imagem") ?: medicamento?.texto("imagem"))
                            ?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                        idRec to img
                    }.toMap()
            }
        }.getOrDefault(emptyMap())

        val doses = mutableListOf<DoseAgendadaNotificacao>()

        listOf(hoje, amanha).forEach { data ->
            val resposta = api.consultarAgenda(idUsuario, data.toString(), fuso.id)
            if (resposta.code() == 404) return@forEach

            val agenda = resposta.objetos("agenda", "Não foi possível carregar a agenda.")
            agenda.forEach { dose ->
                val status = dose.texto("status").orEmpty()
                if (status == "TOMADO") return@forEach

                val idReceita = dose.texto("id_receita_medica")?.toLongOrNull() ?: return@forEach
                val horarioIso = dose.texto("horario_programado") ?: return@forEach
                val instante = runCatching { Instant.parse(horarioIso) }.getOrNull() ?: return@forEach

                if (!instante.isAfter(agora)) return@forEach

                val dosagemTexto = listOfNotNull(
                    dose.texto("dosagem"),
                    dose.texto("tipo_dosagem")
                ).joinToString(" ").trim()

                val chaveNotificacao = "${idReceita}_$horarioIso"
                doses.add(
                    DoseAgendadaNotificacao(
                        notificacaoId = chaveNotificacao.hashCode(),
                        idReceita = idReceita,
                        nomeMedicamento = dose.texto("nome_medicamento").orEmpty(),
                        dosagemTexto = dosagemTexto,
                        horarioFormatado = instante.atZone(fuso).format(formatadorHora),
                        horarioProgramadoIso = horarioIso,
                        horarioProgramado = instante,
                        imagemUrl = imagensPorReceita[idReceita]
                    )
                )
            }
        }

        doses.distinctBy { it.notificacaoId }.sortedBy { it.horarioProgramado }
    }

    override suspend fun verificarAlertasSegundoPlano(): Result<List<AlertaNotificacaoItem>> = requisitar {
        val sessao = tokenManager.obterSessao() ?: return@requisitar emptyList()
        val isCuidador = obterIdPerfil(sessao.token) == 1
        val alertas = mutableListOf<AlertaNotificacaoItem>()

        // 1. Novos vínculos (para paciente e cuidador)
        alertas.addAll(verificarNovosVinculos(sessao.idUsuario, isCuidador))

        // 2. Doses atrasadas e estoque baixo (para paciente)
        if (!isCuidador) {
            alertas.addAll(verificarDosesAtrasadas(sessao.idUsuario))
            alertas.addAll(verificarEstoqueBaixo(sessao.idUsuario))
        }

        alertas
    }

    override fun marcarAlertaComoEnviado(chaveUnica: String) {
        storage.marcarNotificado(chaveUnica)
    }

    override fun limparDadosLocais() {
        storage.limparTudo()
    }

    private suspend fun verificarDosesAtrasadas(idUsuario: Long): List<AlertaNotificacaoItem> {
        val fuso = ZoneId.systemDefault()
        val hoje = LocalDate.now(fuso)
        val agora = Instant.now()
        val formatadorHora = DateTimeFormatter.ofPattern("HH:mm")

        val resposta = api.consultarAgenda(idUsuario, hoje.toString(), fuso.id)
        if (resposta.code() == 404) return emptyList()

        val agenda = resposta.objetos("agenda", "Não foi possível verificar doses atrasadas.")
        val resultado = mutableListOf<AlertaNotificacaoItem>()

        agenda.forEach { dose ->
            if (dose.texto("status") != "ATRASADO") return@forEach

            val idReceita = dose.texto("id_receita_medica")?.toLongOrNull() ?: return@forEach
            val horarioIso = dose.texto("horario_programado") ?: return@forEach
            val instante = runCatching { Instant.parse(horarioIso) }.getOrNull() ?: return@forEach

            // Avisa quando passar de 15 minutos do horário programado
            if (Duration.between(instante, agora).toMinutes() < 15) return@forEach

            val chave = "atraso_${idUsuario}_${idReceita}_$horarioIso"
            if (storage.jaNotificado(chave)) return@forEach

            val nome = dose.texto("nome_medicamento").orEmpty()
            val horaFmt = instante.atZone(fuso).format(formatadorHora)

            resultado.add(
                AlertaNotificacaoItem(
                    chaveUnica = chave,
                    notificacaoId = chave.hashCode(),
                    tipo = TipoAlertaNotificacao.DOSE_ATRASADA,
                    titulo = "Dose atrasada: $nome",
                    mensagem = "Você tinha uma dose programada às $horaFmt. Toque para confirmar.",
                    prioridadeAlta = true
                )
            )
        }

        return resultado
    }

    private suspend fun verificarEstoqueBaixo(idUsuario: Long): List<AlertaNotificacaoItem> {
        val resposta = api.consultarEstoque(idUsuario)
        if (resposta.code() == 404) return emptyList()

        val estoques = resposta.objetos("estoques", "Não foi possível verificar o estoque.")
        val resultado = mutableListOf<AlertaNotificacaoItem>()

        estoques.forEach { item ->
            val idMedicamento = item.texto("id_medicamento")?.toLongOrNull() ?: return@forEach
            val nome = item.texto("nome_medicamento").orEmpty()
            val unidade = item.texto("tipo_dosagem").orEmpty().lowercase()
            val quantidade = item.texto("quantidade_em_estoque")?.toBigDecimalOrNull()?.toInt() ?: return@forEach

            if (quantidade > 5) return@forEach

            val nivel = if (quantidade <= 0) "zerado" else "baixo_$quantidade"
            val chave = "estoque_${idUsuario}_${idMedicamento}_$nivel"
            if (storage.jaNotificado(chave)) return@forEach

            val titulo = if (quantidade <= 0) {
                "Estoque esgotado: $nome"
            } else {
                "Estoque baixo: $nome"
            }
            val mensagem = if (quantidade <= 0) {
                "Seu estoque de $nome acabou. Lembre-se de repor."
            } else {
                "Restam apenas $quantidade $unidade(s) de $nome em estoque."
            }

            resultado.add(
                AlertaNotificacaoItem(
                    chaveUnica = chave,
                    notificacaoId = chave.hashCode(),
                    tipo = TipoAlertaNotificacao.ESTOQUE_BAIXO,
                    titulo = titulo,
                    mensagem = mensagem,
                    prioridadeAlta = false
                )
            )
        }

        return resultado
    }

    private suspend fun verificarNovosVinculos(
        idUsuario: Long,
        isCuidador: Boolean
    ): List<AlertaNotificacaoItem> {
        val resposta = if (isCuidador) {
            api.listarPacientesVinculados()
        } else {
            api.buscarMeuCuidador()
        }

        val atuais = when {
            resposta.code() == 404 -> emptyMap()
            resposta.confirmouSucesso() -> {
                val lista = resposta.body()?.getAsJsonObject("response")
                    ?.getAsJsonArray("vinculo_paciente")
                lista?.mapNotNull { elemento ->
                    val obj = elemento.asJsonObject
                    val idVinculado = if (isCuidador) {
                        obj.texto("id_paciente")
                    } else {
                        obj.texto("id_cuidador")
                    } ?: return@mapNotNull null
                    val nome = obj.texto("nome").orEmpty()
                    idVinculado to nome
                }?.toMap().orEmpty()
            }
            else -> return emptyList()
        }

        val conhecidos = storage.obterVinculosConhecidos(idUsuario)
        storage.salvarVinculosConhecidos(idUsuario, atuais.keys)

        // Na primeira sincronização apenas registra o estado atual sem disparar alertas antigos
        if (conhecidos == null) return emptyList()

        val novosIds = atuais.keys - conhecidos
        return novosIds.map { novoId ->
            val nome = atuais[novoId].orEmpty().ifBlank {
                if (isCuidador) "um paciente" else "um cuidador"
            }
            val chave = "vinculo_${idUsuario}_$novoId"
            val mensagem = if (isCuidador) {
                "O paciente $nome agora está vinculado à sua conta."
            } else {
                "O cuidador $nome agora está vinculado à sua conta."
            }
            AlertaNotificacaoItem(
                chaveUnica = chave,
                notificacaoId = chave.hashCode(),
                tipo = TipoAlertaNotificacao.NOVO_VINCULO,
                titulo = "Novo vínculo realizado",
                mensagem = mensagem,
                prioridadeAlta = false
            )
        }
    }

    private fun obterIdPerfil(jwt: String): Int? = runCatching {
        val conteudo = Base64.decode(jwt.split('.')[1], Base64.URL_SAFE)
        JsonParser.parseString(conteudo.toString(Charsets.UTF_8))
            .asJsonObject.get("role")?.asInt
    }.getOrNull()
}
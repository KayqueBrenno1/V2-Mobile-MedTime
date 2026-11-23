package com.example.v2_medtime.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.v2_medtime.MainActivity
import com.example.v2_medtime.R
import com.example.v2_medtime.data.model.AlarmeMedicamento

object NotificacaoHelper {

    const val CANAL_ALARMES_ID = "medtime_alarmes_medicamentos_v2"
    const val CANAL_LEMBRETES = "medtime_lembretes_v1"
    const val CANAL_ESTOQUE = "medtime_estoque_v1"
    const val CANAL_VINCULOS = "medtime_vinculos_v1"

    const val EXTRA_ABRIR_ALARME = "extra_abrir_alarme"
    const val EXTRA_NOTIFICACAO_ID = "extra_notificacao_id"
    const val EXTRA_ID_RECEITA = "extra_id_receita"
    const val EXTRA_NOME_MEDICAMENTO = "extra_nome_medicamento"
    const val EXTRA_DOSAGEM_TEXTO = "extra_dosagem_texto"
    const val EXTRA_HORARIO_FORMATADO = "extra_horario_formatado"
    const val EXTRA_HORARIO_PROGRAMADO = "extra_horario_programado"
    const val EXTRA_IMAGEM_URL = "extra_imagem_url"

    fun criarCanais(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return

        val somAlarme = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val audioAlarme = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val canalAlarmes = NotificationChannel(
            CANAL_ALARMES_ID,
            "Alarmes de Medicamentos",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alarmes sonoros e em tela cheia no horário de tomar medicamentos"
            enableVibration(true)
            vibrationPattern = longArrayOf(0L, 600L, 400L, 600L)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(somAlarme, audioAlarme)
            setBypassDnd(true)
        }

        val canalLembretes = NotificationChannel(
            CANAL_LEMBRETES,
            "Lembretes e Doses Atrasadas",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Avisos de doses atrasadas de medicamentos"
        }

        val canalEstoque = NotificationChannel(
            CANAL_ESTOQUE,
            "Alertas de Estoque",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Avisos quando o estoque de medicamentos estiver baixo"
        }

        val canalVinculos = NotificationChannel(
            CANAL_VINCULOS,
            "Vínculos de Cuidado",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notificações de novos vínculos entre paciente e cuidador"
        }

        manager.createNotificationChannel(canalAlarmes)
        manager.createNotificationChannel(canalLembretes)
        manager.createNotificationChannel(canalEstoque)
        manager.createNotificationChannel(canalVinculos)
    }

    fun criarIntentTelaAlarme(context: Context, alarme: AlarmeMedicamento): Intent {
        return Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_ABRIR_ALARME, true)
            putExtra(EXTRA_NOTIFICACAO_ID, alarme.notificacaoId)
            putExtra(EXTRA_ID_RECEITA, alarme.idReceita)
            putExtra(EXTRA_NOME_MEDICAMENTO, alarme.nomeMedicamento)
            putExtra(EXTRA_DOSAGEM_TEXTO, alarme.dosagemTexto)
            putExtra(EXTRA_HORARIO_FORMATADO, alarme.horarioFormatado)
            putExtra(EXTRA_HORARIO_PROGRAMADO, alarme.horarioProgramadoIso)
            putExtra(EXTRA_IMAGEM_URL, alarme.imagemUrl)
        }
    }

    @SuppressLint("MissingPermission")
    fun exibirNotificacaoAlarme(context: Context, alarme: AlarmeMedicamento) {
        if (!podeNotificar(context)) return
        criarCanais(context)

        val intentTela = criarIntentTelaAlarme(context, alarme)
        val pendingTela = PendingIntent.getActivity(
            context,
            alarme.notificacaoId,
            intentTela,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val intentConfirmar = Intent(LembreteMedicamentoReceiver.ACTION_CONFIRMAR_DOSE).apply {
            component = ComponentName(context, LembreteMedicamentoReceiver::class.java)
            putExtra(EXTRA_NOTIFICACAO_ID, alarme.notificacaoId)
            putExtra(EXTRA_ID_RECEITA, alarme.idReceita)
            putExtra(EXTRA_HORARIO_PROGRAMADO, alarme.horarioProgramadoIso)
        }
        val pendingConfirmar = PendingIntent.getBroadcast(
            context,
            alarme.notificacaoId + 50_000,
            intentConfirmar,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val descricao = buildString {
            if (alarme.dosagemTexto.isNotBlank()) append("${alarme.dosagemTexto} • ")
            append("Horário: ${alarme.horarioFormatado}")
        }

        val notificacao = NotificationCompat.Builder(context, CANAL_ALARMES_ID)
            .setSmallIcon(R.drawable.relogio_medtime)
            .setContentTitle("Hora do medicamento: ${alarme.nomeMedicamento}")
            .setContentText(descricao)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(true)
            .setContentIntent(pendingTela)
            .setFullScreenIntent(pendingTela, true)
            .addAction(0, "Confirmar", pendingConfirmar)
            .build()

        NotificationManagerCompat.from(context).notify(alarme.notificacaoId, notificacao)
    }

    @SuppressLint("MissingPermission")
    fun exibirAlertaSimples(
        context: Context,
        canalId: String = CANAL_LEMBRETES,
        notificacaoId: Int,
        titulo: String,
        mensagem: String,
        prioridadeAlta: Boolean = false
    ) {
        if (!podeNotificar(context)) return
        criarCanais(context)

        val intentApp = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingApp = PendingIntent.getActivity(
            context,
            notificacaoId,
            intentApp,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prioridade = if (prioridadeAlta) {
            NotificationCompat.PRIORITY_HIGH
        } else {
            NotificationCompat.PRIORITY_DEFAULT
        }

        val notificacao = NotificationCompat.Builder(context, canalId)
            .setSmallIcon(R.drawable.relogio_medtime)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensagem))
            .setPriority(prioridade)
            .setAutoCancel(true)
            .setContentIntent(pendingApp)
            .build()

        NotificationManagerCompat.from(context).notify(notificacaoId, notificacao)
    }

    fun cancelar(context: Context, notificacaoId: Int) {
        if (notificacaoId > 0) {
            NotificationManagerCompat.from(context).cancel(notificacaoId)
        }
    }

    fun cancelarNotificacao(context: Context, notificacaoId: Int) {
        cancelar(context, notificacaoId)
    }

    private fun podeNotificar(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
    }
}
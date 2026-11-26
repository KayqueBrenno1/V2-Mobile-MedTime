package com.example.v2_medtime.notifications

import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.v2_medtime.R
import com.example.v2_medtime.data.model.AlarmeMedicamento

class AlarmeMedicamentoService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_PARAR_ALARME) {
            pararTudoEFinalizar()
            return START_NOT_STICKY
        }

        val idReceita = intent?.getLongExtra(NotificacaoHelper.EXTRA_ID_RECEITA, 0L) ?: 0L
        val nome = intent?.getStringExtra(NotificacaoHelper.EXTRA_NOME_MEDICAMENTO).orEmpty()
        if (idReceita <= 0L || nome.isBlank()) {
            stopSelf()
            return START_NOT_STICKY
        }

        val alarme = AlarmeMedicamento(
            notificacaoId = intent?.getIntExtra(NotificacaoHelper.EXTRA_NOTIFICACAO_ID, 1001) ?: 1001,
            idReceita = idReceita,
            nomeMedicamento = nome,
            dosagemTexto = intent?.getStringExtra(NotificacaoHelper.EXTRA_DOSAGEM_TEXTO).orEmpty(),
            horarioFormatado = intent?.getStringExtra(NotificacaoHelper.EXTRA_HORARIO_FORMATADO).orEmpty(),
            horarioProgramadoIso = intent?.getStringExtra(NotificacaoHelper.EXTRA_HORARIO_PROGRAMADO).orEmpty(),
            imagemUrl = intent?.getStringExtra(NotificacaoHelper.EXTRA_IMAGEM_URL)
        )

        NotificacaoHelper.criarCanais(this)
        acordarTela()

        val intentTela = NotificacaoHelper.criarIntentTelaAlarme(this, alarme)
        val pendingTela = PendingIntent.getActivity(
            this,
            alarme.notificacaoId,
            intentTela,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val intentConfirmar = Intent(LembreteMedicamentoReceiver.ACTION_CONFIRMAR_DOSE).apply {
            component = ComponentName(this@AlarmeMedicamentoService, LembreteMedicamentoReceiver::class.java)
            putExtra(NotificacaoHelper.EXTRA_NOTIFICACAO_ID, alarme.notificacaoId)
            putExtra(NotificacaoHelper.EXTRA_ID_RECEITA, alarme.idReceita)
            putExtra(NotificacaoHelper.EXTRA_HORARIO_PROGRAMADO, alarme.horarioProgramadoIso)
        }
        val pendingConfirmar = PendingIntent.getBroadcast(
            this,
            alarme.notificacaoId + 50_000,
            intentConfirmar,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val descricao = buildString {
            if (alarme.dosagemTexto.isNotBlank()) append("${alarme.dosagemTexto} • ")
            append("Horário: ${alarme.horarioFormatado}")
        }

        val notificacao = NotificationCompat.Builder(this, NotificacaoHelper.CANAL_ALARMES_ID)
            .setSmallIcon(R.drawable.relogio_medtime)
            .setContentTitle("Hora do medicamento: ${alarme.nomeMedicamento}")
            .setContentText(descricao)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(pendingTela)
            .setFullScreenIntent(pendingTela, true)
            .addAction(0, "Confirmar", pendingConfirmar)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                alarme.notificacaoId,
                notificacao,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(alarme.notificacaoId, notificacao)
        }

        AlarmeAudioPlayer.iniciar(this)

        runCatching {
            startActivity(intentTela)
        }

        return START_NOT_STICKY
    }

    private fun acordarTela() {
        runCatching {
            val pm = getSystemService(PowerManager::class.java) ?: return
            @Suppress("DEPRECATION")
            wakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE,
                "v2_medtime:AlarmeServiceWakeLock"
            ).apply {
                acquire(15_000L)
            }
        }
    }

    private fun pararTudoEFinalizar() {
        AlarmeAudioPlayer.parar()
        runCatching {
            if (wakeLock?.isHeld == true) wakeLock?.release()
        }
        wakeLock = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        AlarmeAudioPlayer.parar()
        runCatching {
            if (wakeLock?.isHeld == true) wakeLock?.release()
        }
        wakeLock = null
        super.onDestroy()
    }

    companion object {
        const val ACTION_INICIAR_ALARME = "com.example.v2_medtime.ACTION_INICIAR_ALARME"
        const val ACTION_PARAR_ALARME = "com.example.v2_medtime.ACTION_PARAR_ALARME"

        fun iniciar(context: Context, intentOrigem: Intent) {
            val serviceIntent = Intent(context, AlarmeMedicamentoService::class.java).apply {
                action = ACTION_INICIAR_ALARME
                putExtras(intentOrigem)
            }
            ContextCompat.startForegroundService(context, serviceIntent)
        }

        fun parar(context: Context) {
            AlarmeAudioPlayer.parar()
            runCatching {
                context.stopService(Intent(context, AlarmeMedicamentoService::class.java))
            }
        }
    }
}
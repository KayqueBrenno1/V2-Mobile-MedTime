package com.example.v2_medtime.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.example.v2_medtime.data.model.AlarmeMedicamento
import com.example.v2_medtime.data.remote.RetrofitClient
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LembreteMedicamentoReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        RetrofitClient.inicializar(context.applicationContext)

        if (intent.action == ACTION_CONFIRMAR_DOSE) {
            AlarmeAudioPlayer.parar()
            val notificacaoId = intent.getIntExtra(NotificacaoHelper.EXTRA_NOTIFICACAO_ID, 0)
            val idReceita = intent.getLongExtra(NotificacaoHelper.EXTRA_ID_RECEITA, 0L)
            val horarioIso = intent.getStringExtra(NotificacaoHelper.EXTRA_HORARIO_PROGRAMADO).orEmpty()

            NotificacaoHelper.cancelarNotificacao(context, notificacaoId)

            if (idReceita > 0L && horarioIso.isNotBlank()) {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val dados = JsonObject().apply {
                            addProperty("horario_programado", horarioIso)
                        }
                        RetrofitClient.api.confirmarIngestao(idReceita, dados)
                        VerificacaoPeriodicaWorker.executarAgora(context)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
            return
        }

        val idReceita = intent.getLongExtra(NotificacaoHelper.EXTRA_ID_RECEITA, 0L)
        val nome = intent.getStringExtra(NotificacaoHelper.EXTRA_NOME_MEDICAMENTO).orEmpty()
        if (idReceita <= 0L || nome.isBlank()) return

        val alarme = AlarmeMedicamento(
            notificacaoId = intent.getIntExtra(NotificacaoHelper.EXTRA_NOTIFICACAO_ID, 1001),
            idReceita = idReceita,
            nomeMedicamento = nome,
            dosagemTexto = intent.getStringExtra(NotificacaoHelper.EXTRA_DOSAGEM_TEXTO).orEmpty(),
            horarioFormatado = intent.getStringExtra(NotificacaoHelper.EXTRA_HORARIO_FORMATADO).orEmpty(),
            horarioProgramadoIso = intent.getStringExtra(NotificacaoHelper.EXTRA_HORARIO_PROGRAMADO).orEmpty(),
            imagemUrl = intent.getStringExtra(NotificacaoHelper.EXTRA_IMAGEM_URL)
        )

        acordarTela(context)
        AlarmeAudioPlayer.iniciar(context)
        NotificacaoHelper.exibirNotificacaoAlarme(context, alarme)

        runCatching {
            val intentAbrir = NotificacaoHelper.criarIntentTelaAlarme(context, alarme)
            context.startActivity(intentAbrir)
        }
    }

    private fun acordarTela(context: Context) {
        runCatching {
            val pm = context.getSystemService(PowerManager::class.java) ?: return
            @Suppress("DEPRECATION")
            val wakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE,
                "v2_medtime:AlarmeWakeLock"
            )
            wakeLock.acquire(10_000L)
        }
    }

    companion object {
        const val ACTION_DISPARAR_ALARME = "com.example.v2_medtime.ACTION_DISPARAR_ALARME"
        const val ACTION_CONFIRMAR_DOSE = "com.example.v2_medtime.ACTION_CONFIRMAR_DOSE"
    }
}
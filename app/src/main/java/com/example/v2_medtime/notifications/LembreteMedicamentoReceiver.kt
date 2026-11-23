package com.example.v2_medtime.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.v2_medtime.data.remote.RetrofitClient
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LembreteMedicamentoReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        RetrofitClient.inicializar(context.applicationContext)

        if (intent.action == ACTION_CONFIRMAR_DOSE) {
            AlarmeMedicamentoService.parar(context)
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
                        AgendadorAlarmesMedicamento.sincronizarAlarmes(context)
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

        AlarmeMedicamentoService.iniciar(context.applicationContext, intent)
    }

    companion object {
        const val ACTION_DISPARAR_ALARME = "com.example.v2_medtime.ACTION_DISPARAR_ALARME"
        const val ACTION_CONFIRMAR_DOSE = "com.example.v2_medtime.ACTION_CONFIRMAR_DOSE"
    }
}
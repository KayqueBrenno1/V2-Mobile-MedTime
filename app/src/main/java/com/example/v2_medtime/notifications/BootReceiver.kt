package com.example.v2_medtime.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.v2_medtime.data.remote.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val acao = intent.action ?: return
        if (acao != Intent.ACTION_BOOT_COMPLETED &&
            acao != "android.intent.action.QUICKBOOT_POWERON"
        ) {
            return
        }

        val appContext = context.applicationContext
        RetrofitClient.inicializar(appContext)
        NotificacaoHelper.criarCanais(appContext)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AgendadorAlarmesMedicamento.sincronizarAlarmes(appContext)
                VerificacaoPeriodicaWorker.agendarPeriodico(appContext)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
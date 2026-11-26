package com.example.v2_medtime

import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.v2_medtime.data.model.AlarmeMedicamento
import com.example.v2_medtime.data.remote.RetrofitClient
import com.example.v2_medtime.navigation.AppNavigation
import com.example.v2_medtime.notifications.NotificacaoHelper
import com.example.v2_medtime.screens.components.PermissoesAlarmeHandler
import com.example.v2_medtime.ui.theme.V2MedTimeTheme

class MainActivity : ComponentActivity() {

    private var alarmePendente by mutableStateOf<AlarmeMedicamento?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setShowWhenLocked(true)
        setTurnScreenOn(true)
        getSystemService(KeyguardManager::class.java)?.requestDismissKeyguard(this, null)

        enableEdgeToEdge()

        RetrofitClient.inicializar(applicationContext)
        NotificacaoHelper.criarCanais(applicationContext)
        alarmePendente = NotificacaoHelper.extrairAlarmeDaIntent(intent)

        setContent {
            V2MedTimeTheme {
                AppNavigation(
                    modifier = Modifier.fillMaxSize(),
                    alarmeInicial = alarmePendente,
                    onAlarmeConsumido = { alarmePendente = null }
                )

                PermissoesAlarmeHandler()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        NotificacaoHelper.extrairAlarmeDaIntent(intent)?.let { alarmePendente = it }
    }
}
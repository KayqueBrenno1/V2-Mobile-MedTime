package com.example.v2_medtime

import android.Manifest
import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.v2_medtime.data.model.AlarmeMedicamento
import com.example.v2_medtime.data.remote.RetrofitClient
import com.example.v2_medtime.navigation.AppNavigation
import com.example.v2_medtime.notifications.NotificacaoHelper
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
        alarmePendente = extrairAlarmeDaIntent(intent)

        setContent {
            val launcherPermissao = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { }

            var mostrarDialogoSobreposicao by remember {
                mutableStateOf(!Settings.canDrawOverlays(this@MainActivity))
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    launcherPermissao.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    val nm = getSystemService(NotificationManager::class.java)
                    if (nm != null && !nm.canUseFullScreenIntent()) {
                        runCatching {
                            startActivity(
                                Intent(
                                    Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                                    Uri.parse("package:$packageName")
                                )
                            )
                        }
                    }
                }
            }

            V2MedTimeTheme {
                AppNavigation(
                    modifier = Modifier.fillMaxSize(),
                    alarmeInicial = alarmePendente,
                    onAlarmeConsumido = { alarmePendente = null }
                )

                if (mostrarDialogoSobreposicao && !Settings.canDrawOverlays(this@MainActivity)) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoSobreposicao = false },
                        title = { Text("Permitir alarme na tela") },
                        text = {
                            Text("Para que a tela de alarme abra automaticamente mesmo quando você estiver fora do MedTime, ative \"Exibir sobre outros apps\".")
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                mostrarDialogoSobreposicao = false
                                startActivity(
                                    Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:$packageName")
                                    )
                                )
                            }) {
                                Text("Ativar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { mostrarDialogoSobreposicao = false }) {
                                Text("Agora não")
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        extrairAlarmeDaIntent(intent)?.let { alarmePendente = it }
    }

    private fun extrairAlarmeDaIntent(intent: Intent?): AlarmeMedicamento? {
        if (intent?.getBooleanExtra(NotificacaoHelper.EXTRA_ABRIR_ALARME, false) != true) {
            return null
        }

        val idReceita = intent.getLongExtra(NotificacaoHelper.EXTRA_ID_RECEITA, 0L)
        val nome = intent.getStringExtra(NotificacaoHelper.EXTRA_NOME_MEDICAMENTO).orEmpty()
        if (idReceita <= 0L || nome.isBlank()) return null

        return AlarmeMedicamento(
            notificacaoId = intent.getIntExtra(NotificacaoHelper.EXTRA_NOTIFICACAO_ID, 0),
            idReceita = idReceita,
            nomeMedicamento = nome,
            dosagemTexto = intent.getStringExtra(NotificacaoHelper.EXTRA_DOSAGEM_TEXTO).orEmpty(),
            horarioFormatado = intent.getStringExtra(NotificacaoHelper.EXTRA_HORARIO_FORMATADO).orEmpty(),
            horarioProgramadoIso = intent.getStringExtra(NotificacaoHelper.EXTRA_HORARIO_PROGRAMADO).orEmpty(),
            imagemUrl = intent.getStringExtra(NotificacaoHelper.EXTRA_IMAGEM_URL)
        )
    }
}
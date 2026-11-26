package com.example.v2_medtime.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.model.AlarmeMedicamento
import com.example.v2_medtime.data.remote.RetrofitClient
import com.example.v2_medtime.data.repository.exigirSucesso
import com.example.v2_medtime.data.repository.requisitar
import com.example.v2_medtime.notifications.AlarmeAudioPlayer
import com.example.v2_medtime.notifications.AlarmeMedicamentoService
import com.example.v2_medtime.notifications.NotificacaoHelper
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class AlarmeViewModel(application: Application) : AndroidViewModel(application) {

    init {
        RetrofitClient.inicializar(application.applicationContext)
    }

    var alarmeAtual by mutableStateOf<AlarmeMedicamento?>(null)
        private set

    var carregando by mutableStateOf(false)
        private set

    val isLoading: Boolean
        get() = carregando

    var erroAlarme by mutableStateOf<String?>(null)
        private set

    val erro: String?
        get() = erroAlarme

    fun definirAlarme(alarme: AlarmeMedicamento) {
        alarmeAtual = alarme
        erroAlarme = null
    }

    fun confirmarTomei(onSucesso: (Int) -> Unit) {
        val alarme = alarmeAtual ?: return
        if (carregando) return

        AlarmeMedicamentoService.parar(getApplication())

        viewModelScope.launch {
            carregando = true
            erroAlarme = null
            requisitar {
                val dados = JsonObject().apply {
                    addProperty("horario_programado", alarme.horarioProgramadoIso)
                }
                RetrofitClient.api
                    .confirmarIngestao(alarme.idReceita, dados)
                    .exigirSucesso("Não foi possível confirmar a dose.")
            }.onSuccess {
                val notificacaoId = alarme.notificacaoId
                NotificacaoHelper.cancelar(getApplication(), notificacaoId)
                alarmeAtual = null
                onSucesso(notificacaoId)
            }.onFailure { falha ->
                erroAlarme = falha.message ?: "Não foi possível confirmar a dose."
            }
            carregando = false
        }
    }

    fun confirmarDose(onSucesso: () -> Unit) {
        confirmarTomei { onSucesso() }
    }

    fun dispensarAlarme() {
        AlarmeMedicamentoService.parar(getApplication())
        alarmeAtual?.let {
            NotificacaoHelper.cancelar(getApplication(), it.notificacaoId)
        }
        alarmeAtual = null
    }

    fun limparAlarme() {
        dispensarAlarme()
    }

    fun limparErro() {
        erroAlarme = null
    }
}
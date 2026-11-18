package com.example.v2_medtime.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ConfirmarEmailViewModel : ViewModel() {

    var email by mutableStateOf("")
        private set

    var codigo by mutableStateOf("")
        private set

    var carregando by mutableStateOf(false)
        private set

    var mensagemErro by mutableStateOf<String?>(null)
        private set

    var emailConfirmado by mutableStateOf(false)
        private set

    fun definirEmail(novoEmail: String) {
        email = novoEmail
        codigo = ""
        mensagemErro = null
        emailConfirmado = false
    }

    fun atualizarCodigo(novoCodigo: String) {
        codigo = novoCodigo.filter { it.isDigit() }.take(6)
        mensagemErro = null
    }

    fun validarCodigo(): Boolean {
        if (codigo.length != 6) {
            mensagemErro = "Digite o código de 6 dígitos."
            return false
        }

        mensagemErro = null
        return true
    }

    fun iniciarCarregamento() {
        carregando = true
        mensagemErro = null
    }

    fun finalizarCarregamento() {
        carregando = false
    }

    fun registrarErro(mensagem: String) {
        mensagemErro = mensagem
        carregando = false
    }

    fun registrarConfirmacao() {
        emailConfirmado = true
        mensagemErro = null
        carregando = false
    }
}

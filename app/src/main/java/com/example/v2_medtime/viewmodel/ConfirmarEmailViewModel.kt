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
        carregando = false
        ultimoCodigoVerificado = null
    }

    fun atualizarCodigo(novoCodigo: String) {
        val codigoFormatado = novoCodigo
            .filter { it.isDigit() }
            .take(6)

        if (codigoFormatado != codigo) {
            codigo = codigoFormatado
            mensagemErro = null
            ultimoCodigoVerificado = null
        }
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
        carregando = false
        mensagemErro = mensagem
    }

    fun registrarConfirmacao() {
        emailConfirmado = true
        mensagemErro = null
        carregando = false
    }

    private var ultimoCodigoVerificado: String? = null

    fun podeVerificarCodigo(): Boolean {
        if (codigo.length != 6 || carregando) {
            return false
        }

        if (codigo == ultimoCodigoVerificado) {
            return false
        }

        ultimoCodigoVerificado = codigo

        return true
    }
}
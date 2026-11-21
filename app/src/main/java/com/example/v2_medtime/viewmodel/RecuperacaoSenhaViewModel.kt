package com.example.v2_medtime.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.remote.confirmouSucesso
import com.example.v2_medtime.data.remote.mensagemDeErro
import com.example.v2_medtime.data.repository.LoginRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.Response

class RecuperacaoSenhaViewModel : ViewModel() {

    private val repositorio = LoginRepository()

    var carregando by mutableStateOf(false)
        private set

    fun enviarCodigo(email: String, onSucesso: () -> Unit, onErro: (String) -> Unit) {
        executar(onSucesso, onErro, "Não foi possível enviar o código.") {
            repositorio.gerarCodigoRedefinicaoSenha(email)
        }
    }

    fun redefinirSenha(
        email: String,
        codigo: String,
        novaSenha: String,
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        executar(onSucesso, onErro, "Não foi possível redefinir a senha.") {
            repositorio.redefinirSenha(email, codigo, novaSenha)
        }
    }

    private fun executar(
        onSucesso: () -> Unit,
        onErro: (String) -> Unit,
        mensagemPadrao: String,
        solicitar: suspend () -> Response<JsonObject>
    ) {
        if (carregando) return

        viewModelScope.launch {
            carregando = true
            try {
                val resposta = solicitar()
                if (resposta.confirmouSucesso()) {
                    onSucesso()
                } else {
                    onErro(resposta.mensagemDeErro(mensagemPadrao))
                }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (_: Exception) {
                onErro(mensagemPadrao)
            } finally {
                carregando = false
            }
        }
    }
}

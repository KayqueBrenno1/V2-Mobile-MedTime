package com.example.v2_medtime.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.local.TokenManager
import com.example.v2_medtime.data.model.PerfilUsuario
import com.example.v2_medtime.data.remote.confirmouSucesso
import com.example.v2_medtime.data.remote.mensagemDeErro
import com.example.v2_medtime.data.remote.texto
import com.example.v2_medtime.data.repository.PerfilRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = PerfilRepository()
    private val gerenciadorToken = TokenManager(application)

    var perfil by mutableStateOf(PerfilUsuario())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var erroPerfil by mutableStateOf<String?>(null)
        private set

    fun carregarPerfil(usuario: JsonObject) {
        val dataApi = usuario.texto("data_nascimento").orEmpty().take(10)
        val data = dataApi.split("-")
        perfil = PerfilUsuario(
            id = usuario.texto("id")?.toLongOrNull() ?: 0L,
            nomeCompleto = usuario.texto("nome").orEmpty(),
            email = usuario.texto("email").orEmpty(),
            dataNascimento = if (data.size == 3) data.reversed().joinToString("/") else dataApi,
            cpf = usuario.texto("cpf").orEmpty(),
            fotoUrlRemota = usuario.texto("foto")
        )
        erroPerfil = null
    }

    fun salvarAlteracoesPerfil(
        nomeCompleto: String,
        email: String,
        dataNascimento: String,
        cpf: String,
        fotoUriLocal: Uri?,
        onSucesso: () -> Unit
    ) {
        if (isLoading) return

        val dataApi = runCatching {
            val formatoTela = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).apply { isLenient = false }
            val formatoApi = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
            require(dataNascimento.matches(Regex("[0-9]{2}/[0-9]{2}/[0-9]{4}")))
            formatoApi.format(requireNotNull(formatoTela.parse(dataNascimento)))
        }.getOrNull()

        if (perfil.id <= 0L || dataApi == null) {
            erroPerfil = if (dataApi == null) "Informe a data de nascimento no formato DD/MM/AAAA."
                else "Entre novamente para editar o perfil."
            return
        }

        if (cpf.isBlank() && perfil.cpf.isNotBlank()) {
            erroPerfil = "O servidor ainda não permite remover um CPF cadastrado."
            return
        }

        viewModelScope.launch {
            isLoading = true
            erroPerfil = null

            try {
                val atualizado = perfil.copy(
                    nomeCompleto = nomeCompleto.trim(),
                    email = email.trim(),
                    dataNascimento = dataNascimento,
                    cpf = cpf,
                    fotoUriLocal = fotoUriLocal
                )
                val resposta = repositorio.atualizarPerfil(atualizado, dataApi)
                if (resposta.confirmouSucesso()) {
                    perfil = atualizado
                    onSucesso()
                } else {
                    erroPerfil = resposta.mensagemDeErro("Não foi possível salvar o perfil.")
                }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (_: Exception) {
                erroPerfil = "Não foi possível salvar o perfil."
            } finally {
                isLoading = false
            }
        }
    }

    fun limparErroPerfil() {
        erroPerfil = null
    }

    fun sairDaConta(onLogoutConcluido: () -> Unit) {
        if (gerenciadorToken.removerToken()) {
            perfil = PerfilUsuario()
            erroPerfil = null
            onLogoutConcluido()
        } else {
            erroPerfil = "Não foi possível encerrar a sessão."
        }
    }
}

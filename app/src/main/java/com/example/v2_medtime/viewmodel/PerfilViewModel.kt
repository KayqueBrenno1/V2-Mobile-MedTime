package com.example.v2_medtime.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.model.PerfilUsuario
import com.example.v2_medtime.data.repository.FakePerfilRepository
import com.example.v2_medtime.data.repository.PerfilRepository
import kotlinx.coroutines.launch
import java.io.File

class PerfilViewModel(
    private val repository: PerfilRepository = FakePerfilRepository()
) : ViewModel() {

    var perfil by mutableStateOf(PerfilUsuario())
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        carregarPerfil()
    }

    fun carregarPerfil() {
        viewModelScope.launch {
            isLoading = true
            repository.obterPerfil().onSuccess { dados ->
                perfil = dados
            }
            isLoading = false
        }
    }

    fun salvarAlteracoesPerfil(
        nomeCompleto: String,
        email: String,
        dataNascimento: String,
        novaFotoFile: File?,
        novaFotoUri: Uri?,
        onSucesso: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            val atualizado = perfil.copy(
                nomeCompleto = nomeCompleto,
                email = email,
                dataNascimento = dataNascimento
            )
            repository.atualizarPerfil(atualizado, novaFotoFile, novaFotoUri)
                .onSuccess { perfilSalvo ->
                    perfil = perfilSalvo
                    onSucesso()
                }
            isLoading = false
        }
    }

    fun sairDaConta(onLogoutConcluido: () -> Unit) {
        viewModelScope.launch {
            repository.encerrarSessao().onSuccess {
                onLogoutConcluido()
            }
        }
    }
}
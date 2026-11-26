package com.example.v2_medtime.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.model.HistoricoItem
import com.example.v2_medtime.data.repository.ApiHistoricoRepository
import kotlinx.coroutines.cancelChildren
import com.example.v2_medtime.data.repository.HistoricoRepository
import kotlinx.coroutines.launch

class HistoricoViewModel(
    private val repository: HistoricoRepository = ApiHistoricoRepository()
) : ViewModel() {

    val listaHistorico = mutableStateListOf<HistoricoItem>()
    var isLoading by mutableStateOf(false)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    fun atualizarHistorico() {
        if (isLoading || isRefreshing) return
        viewModelScope.launch {
            isRefreshing = true
            try {
                repository.listarHistorico().onSuccess {
                    listaHistorico.clear()
                    listaHistorico.addAll(it)
                }
            } finally {
                isRefreshing = false
            }
        }
    }

    fun iniciarSessao(idUsuario: Long) {
        viewModelScope.coroutineContext.cancelChildren()
        (repository as? ApiHistoricoRepository)?.idUsuario = idUsuario
        listaHistorico.clear()
        isLoading = false

        if (idUsuario > 0L) carregarHistorico()
    }

    fun carregarHistorico() {
        if (isLoading) return
        viewModelScope.launch {
            isLoading = true
            repository.listarHistorico().onSuccess {
                listaHistorico.clear()
                listaHistorico.addAll(it)
            }
            isLoading = false
        }
    }
}
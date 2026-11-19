package com.example.v2_medtime.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.model.HistoricoItem
import com.example.v2_medtime.data.repository.FakeHistoricoRepository
import com.example.v2_medtime.data.repository.HistoricoRepository
import kotlinx.coroutines.launch

class HistoricoViewModel(
    private val repository: HistoricoRepository = FakeHistoricoRepository()
) : ViewModel() {

    val listaHistorico = mutableStateListOf<HistoricoItem>()
    var isLoading by mutableStateOf(false)
        private set

    init {
        carregarHistorico()
    }

    fun carregarHistorico() {
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
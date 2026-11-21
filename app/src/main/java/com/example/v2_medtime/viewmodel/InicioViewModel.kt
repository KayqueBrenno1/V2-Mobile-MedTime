package com.example.v2_medtime.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.model.DiaCalendarioItem
import com.example.v2_medtime.data.model.EstoqueResumoItem
import com.example.v2_medtime.data.model.ProximoMedicamentoItem
import com.example.v2_medtime.data.model.StatusDoseHoje
import com.example.v2_medtime.data.repository.ApiInicioRepository
import kotlinx.coroutines.cancelChildren
import com.example.v2_medtime.data.repository.InicioRepository
import kotlinx.coroutines.launch

class InicioViewModel(
    private val repository: InicioRepository = ApiInicioRepository()
) : ViewModel() {

    var nomeUsuario by mutableStateOf("")
        private set

    val diasCalendario = mutableStateListOf<DiaCalendarioItem>()
    var diaSelecionadoId by mutableStateOf("")
        private set

    val proximosMedicamentos = mutableStateListOf<ProximoMedicamentoItem>()
    val resumoEstoque = mutableStateListOf<EstoqueResumoItem>()

    // Controla qual card está expandido para exibir o botão verde "TOMAR"
    var cardExpandidoId by mutableStateOf<Long?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var erroInicio by mutableStateOf<String?>(null)
        private set

    fun iniciarSessao(idUsuario: Long, nome: String) {
        viewModelScope.coroutineContext.cancelChildren()
        (repository as? ApiInicioRepository)?.idUsuario = idUsuario
        nomeUsuario = nome.substringBefore(' ')
        diasCalendario.clear()
        proximosMedicamentos.clear()
        resumoEstoque.clear()
        cardExpandidoId = null
        diaSelecionadoId = ""
        isLoading = false
        erroInicio = null

        if (idUsuario > 0L) carregarDadosIniciais()
    }

    fun limparErro() {
        erroInicio = null
    }

    fun carregarDadosIniciais() {
        if (isLoading) return
        viewModelScope.launch {
            isLoading = true
            repository.obterDiasCalendario().onSuccess { dias ->
                diasCalendario.clear()
                diasCalendario.addAll(dias)
                val hoje = dias.firstOrNull { it.isHoje } ?: dias.firstOrNull()
                if (hoje != null) {
                    diaSelecionadoId = hoje.id
                }
            }
            carregarDosesDoDia(diaSelecionadoId)
            repository.obterResumoEstoque().onSuccess { estoques ->
                resumoEstoque.clear()
                resumoEstoque.addAll(estoques)
            }
            isLoading = false
        }
    }

    fun selecionarDia(diaId: String) {
        if (isLoading) return
        diaSelecionadoId = diaId
        viewModelScope.launch {
            isLoading = true
            try {
                carregarDosesDoDia(diaId)
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun carregarDosesDoDia(diaId: String) {
        repository.obterProximosMedicamentos(diaId).onSuccess { doses ->
            proximosMedicamentos.clear()
            proximosMedicamentos.addAll(doses)
            cardExpandidoId = doses.firstOrNull { it.status != StatusDoseHoje.TOMADO }?.id
        }.onFailure {
            proximosMedicamentos.clear()
            erroInicio = it.message ?: "Não foi possível carregar a agenda."
        }
    }

    fun alternarExpansaoCard(doseId: Long) {
        cardExpandidoId = if (cardExpandidoId == doseId) null else doseId
    }

    fun marcarComoTomado(doseId: Long, onSucesso: () -> Unit = {}) {
        viewModelScope.launch {
            repository.registrarDoseTomada(doseId).onSuccess {
                val index = proximosMedicamentos.indexOfFirst { it.id == doseId }
                if (index != -1) {
                    proximosMedicamentos[index] = proximosMedicamentos[index].copy(
                        status = StatusDoseHoje.TOMADO
                    )
                }
                if (cardExpandidoId == doseId) {
                    cardExpandidoId = null
                }
                onSucesso()
            }.onFailure {
                erroInicio = it.message ?: "Não foi possível registrar a dose."
            }
        }
    }
}
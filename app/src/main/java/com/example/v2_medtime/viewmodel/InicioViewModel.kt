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
import com.example.v2_medtime.data.repository.FakeInicioRepository
import com.example.v2_medtime.data.repository.InicioRepository
import kotlinx.coroutines.launch

class InicioViewModel(
    private val repository: InicioRepository = FakeInicioRepository()
) : ViewModel() {

    var nomeUsuario by mutableStateOf("Kayque")
        private set

    val diasCalendario = mutableStateListOf<DiaCalendarioItem>()
    var diaSelecionadoId by mutableStateOf("2026-11-12")
        private set

    val proximosMedicamentos = mutableStateListOf<ProximoMedicamentoItem>()
    val resumoEstoque = mutableStateListOf<EstoqueResumoItem>()

    // Controla qual card está expandido para exibir o botão verde "TOMAR"
    var cardExpandidoId by mutableStateOf<Long?>(101L)
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        carregarDadosIniciais()
    }

    fun carregarDadosIniciais() {
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
        diaSelecionadoId = diaId
        viewModelScope.launch {
            carregarDosesDoDia(diaId)
        }
    }

    private suspend fun carregarDosesDoDia(diaId: String) {
        repository.obterProximosMedicamentos(diaId).onSuccess { doses ->
            proximosMedicamentos.clear()
            proximosMedicamentos.addAll(doses)
            cardExpandidoId = doses.firstOrNull { it.status != StatusDoseHoje.TOMADO }?.id
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
            }
        }
    }
}
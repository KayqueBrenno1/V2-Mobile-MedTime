package com.example.v2_medtime.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.model.DiaCalendarioItem
import com.example.v2_medtime.data.model.EstoqueResumoItem
import com.example.v2_medtime.data.model.ProximoMedicamentoItem
import com.example.v2_medtime.data.model.StatusDoseHoje
import com.example.v2_medtime.data.repository.ApiInicioRepository
import com.example.v2_medtime.data.repository.InicioRepository
import com.example.v2_medtime.notifications.VerificacaoPeriodicaWorker
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class InicioViewModel(
    application: Application,
    private val repository: InicioRepository = ApiInicioRepository(application.cacheDir)
) : AndroidViewModel(application) {

    constructor(application: Application) : this(application, ApiInicioRepository(application.cacheDir))

    var nomeUsuario by mutableStateOf("")
        private set

    val diasCalendario = mutableStateListOf<DiaCalendarioItem>()
    var diaSelecionadoId by mutableStateOf("")
        private set

    var deslocamentoSemana by mutableIntStateOf(0)
        private set

    val proximosMedicamentos = mutableStateListOf<ProximoMedicamentoItem>()
    val resumoEstoque = mutableStateListOf<EstoqueResumoItem>()

    var cardExpandidoId by mutableStateOf<Long?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var erroInicio by mutableStateOf<String?>(null)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    fun atualizarDados() {
        if (isLoading || isRefreshing) return
        viewModelScope.launch {
            isRefreshing = true
            try {
                if (diasCalendario.isEmpty()) {
                    atualizarDiasDaSemana(0, selecionarHoje = true)
                }
                if (diaSelecionadoId.isNotBlank()) {
                    carregarDosesDoDia(diaSelecionadoId)
                }
                repository.obterResumoEstoque().onSuccess { estoques ->
                    resumoEstoque.clear()
                    resumoEstoque.addAll(estoques)
                }
            } finally {
                isRefreshing = false
            }
        }
    }

    val tituloDataSelecionada: String
        get() {
            val data = runCatching { LocalDate.parse(diaSelecionadoId) }.getOrNull() ?: LocalDate.now()
            val hoje = LocalDate.now()
            val idioma = Locale.forLanguageTag("pt-BR")
            val mes = data.format(DateTimeFormatter.ofPattern("MMM", idioma))
                .trimEnd('.')
                .replaceFirstChar { it.uppercase() }
            val diaMes = "${data.dayOfMonth} de $mes"
            return when (data) {
                hoje -> "Hoje, $diaMes"
                hoje.plusDays(1) -> "Amanhã, $diaMes"
                hoje.minusDays(1) -> "Ontem, $diaMes"
                else -> {
                    val diaSemana = data.dayOfWeek.getDisplayName(TextStyle.SHORT, idioma)
                        .trimEnd('.')
                        .replaceFirstChar { it.uppercase() }
                    "$diaSemana, $diaMes"
                }
            }
        }

    fun iniciarSessao(idUsuario: Long, nome: String) {
        viewModelScope.coroutineContext.cancelChildren()
        (repository as? ApiInicioRepository)?.idUsuario = idUsuario
        nomeUsuario = nome.substringBefore(' ')
        diasCalendario.clear()
        proximosMedicamentos.clear()
        resumoEstoque.clear()
        cardExpandidoId = null
        diaSelecionadoId = ""
        deslocamentoSemana = 0
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
            try {
                if (diasCalendario.isEmpty()) {
                    atualizarDiasDaSemana(0, selecionarHoje = true)
                }
                if (diaSelecionadoId.isNotBlank()) {
                    carregarDosesDoDia(diaSelecionadoId)
                }
                repository.obterResumoEstoque().onSuccess { estoques ->
                    resumoEstoque.clear()
                    resumoEstoque.addAll(estoques)
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun navegarSemana(deltaSemanas: Int) {
        if (isLoading) return
        val novaSemana = deslocamentoSemana + deltaSemanas
        atualizarDiasDaSemana(novaSemana, selecionarHoje = novaSemana == 0)
        selecionarDia(diaSelecionadoId)
    }

    private fun atualizarDiasDaSemana(semanaOffset: Int, selecionarHoje: Boolean) {
        deslocamentoSemana = semanaOffset
        val hoje = LocalDate.now()
        val segundaDaSemana = hoje
            .plusWeeks(semanaOffset.toLong())
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val idioma = Locale.forLanguageTag("pt-BR")

        val dias = (0L..6L).map { offset ->
            val dia = segundaDaSemana.plusDays(offset)
            DiaCalendarioItem(
                id = dia.toString(),
                diaSemana = dia.dayOfWeek.getDisplayName(TextStyle.SHORT, idioma)
                    .trimEnd('.')
                    .replaceFirstChar { it.uppercase() },
                diaNumero = dia.dayOfMonth.toString().padStart(2, '0'),
                isHoje = dia == hoje
            )
        }
        diasCalendario.clear()
        diasCalendario.addAll(dias)
        diaSelecionadoId = if (selecionarHoje) {
            dias.firstOrNull { it.isHoje }?.id ?: dias.first().id
        } else {
            dias.first().id
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
                repository.obterResumoEstoque().onSuccess { estoques ->
                    resumoEstoque.clear()
                    resumoEstoque.addAll(estoques)
                }
                VerificacaoPeriodicaWorker.executarAgora(getApplication())
                onSucesso()
            }.onFailure {
                erroInicio = it.message ?: "Não foi possível registrar a dose."
            }
        }
    }
}
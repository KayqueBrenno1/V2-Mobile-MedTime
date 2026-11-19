package com.example.v2_medtime.data.model

data class DiaCalendarioItem(
    val id: String,
    val diaSemana: String,
    val diaNumero: String,
    val isHoje: Boolean = false
)

enum class StatusDoseHoje(val label: String) {
    ATRASADO("Atrasado"),
    PENDENTE("Pendente"),
    TOMADO("Tomado")
}

data class ProximoMedicamentoItem(
    val id: Long,
    val medicamentoId: Long,
    val nome: String,
    val concentracao: String,
    val unidadeMedida: String,
    val dosagemTexto: String,
    val horario: String,
    val status: StatusDoseHoje
)

data class EstoqueResumoItem(
    val medicamentoId: Long,
    val nome: String,
    val atual: Int,
    val total: Int,
    val unidade: String
)
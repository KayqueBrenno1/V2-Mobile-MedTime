package com.example.v2_medtime.data.model

enum class StatusHistorico(val label: String) {
    TOMADO("Tomado"),
    NAO_TOMADO("Pendente")
}

data class HistoricoItem(
    val id: Long,
    val medicamentoId: Long,
    val nomeMedicamento: String,
    val concentracao: String,
    val unidadeMedida: String,
    val dataLabel: String,
    val horario: String,
    val status: StatusHistorico,
    val colorIndex: Int = 0
)
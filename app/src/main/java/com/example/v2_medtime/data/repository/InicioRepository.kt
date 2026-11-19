package com.example.v2_medtime.data.repository

import com.example.v2_medtime.data.model.DiaCalendarioItem
import com.example.v2_medtime.data.model.EstoqueResumoItem
import com.example.v2_medtime.data.model.ProximoMedicamentoItem
import com.example.v2_medtime.data.model.StatusDoseHoje

interface InicioRepository {
    suspend fun obterDiasCalendario(): Result<List<DiaCalendarioItem>>
    suspend fun obterProximosMedicamentos(diaId: String): Result<List<ProximoMedicamentoItem>>
    suspend fun obterResumoEstoque(): Result<List<EstoqueResumoItem>>
    suspend fun registrarDoseTomada(doseId: Long): Result<Unit>
}

class FakeInicioRepository : InicioRepository {

    private val diasMock = listOf(
        DiaCalendarioItem("2026-11-10", "Seg", "10", isHoje = false),
        DiaCalendarioItem("2026-11-11", "Ter", "11", isHoje = false),
        DiaCalendarioItem("2026-11-12", "Hoje", "12", isHoje = true),
        DiaCalendarioItem("2026-11-13", "Qui", "13", isHoje = false),
        DiaCalendarioItem("2026-11-14", "Sex", "14", isHoje = false)
    )

    private val dosesPorDia = mutableMapOf(
        "2026-11-12" to mutableListOf(
            ProximoMedicamentoItem(
                id = 101L,
                medicamentoId = 1L,
                nome = "Losartana",
                concentracao = "50",
                unidadeMedida = "mg",
                dosagemTexto = "1 comprimido",
                horario = "14:00",
                status = StatusDoseHoje.ATRASADO
            ),
            ProximoMedicamentoItem(
                id = 102L,
                medicamentoId = 1L,
                nome = "Losartana",
                concentracao = "50",
                unidadeMedida = "mg",
                dosagemTexto = "1 comprimido",
                horario = "17:00",
                status = StatusDoseHoje.PENDENTE
            )
        )
    )

    private val estoquesMock = mutableListOf(
        EstoqueResumoItem(medicamentoId = 1L, nome = "Losartana", atual = 30, total = 30, unidade = "CP"),
        EstoqueResumoItem(medicamentoId = 2L, nome = "Metformina", atual = 15, total = 30, unidade = "")
    )

    override suspend fun obterDiasCalendario(): Result<List<DiaCalendarioItem>> {
        return Result.success(diasMock)
    }

    override suspend fun obterProximosMedicamentos(diaId: String): Result<List<ProximoMedicamentoItem>> {
        val lista = dosesPorDia[diaId] ?: listOf(
            ProximoMedicamentoItem(
                id = 201L,
                medicamentoId = 2L,
                nome = "Metformina",
                concentracao = "850",
                unidadeMedida = "mg",
                dosagemTexto = "1 comprimido",
                horario = "08:00",
                status = StatusDoseHoje.PENDENTE
            )
        )
        return Result.success(lista.toList())
    }

    override suspend fun obterResumoEstoque(): Result<List<EstoqueResumoItem>> {
        return Result.success(estoquesMock.toList())
    }

    override suspend fun registrarDoseTomada(doseId: Long): Result<Unit> {
        dosesPorDia.values.forEach { lista ->
            val idx = lista.indexOfFirst { it.id == doseId }
            if (idx != -1) {
                lista[idx] = lista[idx].copy(status = StatusDoseHoje.TOMADO)
            }
        }
        return Result.success(Unit)
    }
}
package com.example.v2_medtime.data.repository

import com.example.v2_medtime.data.model.HistoricoItem
import com.example.v2_medtime.data.model.StatusHistorico

interface HistoricoRepository {
    suspend fun listarHistorico(): Result<List<HistoricoItem>>
}

class FakeHistoricoRepository : HistoricoRepository {
    private val registros = listOf(
        HistoricoItem(1L, 1L, "Losartana", "50", "mg", "Hoje", "08:00", StatusHistorico.TOMADO, 0),
        HistoricoItem(2L, 2L, "Metformina", "850", "mg", "Hoje", "08:00", StatusHistorico.TOMADO, 1),
        HistoricoItem(3L, 3L, "Omeprazol", "20", "mg", "Ontem", "20:00", StatusHistorico.NAO_TOMADO, 2),
        HistoricoItem(4L, 1L, "Losartana", "50", "mg", "Ontem", "16:00", StatusHistorico.TOMADO, 0),
        HistoricoItem(5L, 2L, "Metformina", "850", "mg", "Ontem", "08:00", StatusHistorico.TOMADO, 1)
    )

    override suspend fun listarHistorico(): Result<List<HistoricoItem>> = Result.success(registros)
}
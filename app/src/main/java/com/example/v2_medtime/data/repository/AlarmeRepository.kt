package com.example.v2_medtime.data.repository

import com.example.v2_medtime.data.remote.RetrofitClient
import com.google.gson.JsonObject

interface AlarmeRepository {
    suspend fun confirmarIngestao(idReceita: Long, horarioProgramadoIso: String): Result<Unit>
}

class ApiAlarmeRepository : AlarmeRepository {

    private val api get() = RetrofitClient.api

    override suspend fun confirmarIngestao(
        idReceita: Long,
        horarioProgramadoIso: String
    ): Result<Unit> = requisitar {
        require(idReceita > 0L && horarioProgramadoIso.isNotBlank()) {
            "Dados da dose inválidos para confirmação."
        }
        val dados = JsonObject().apply {
            addProperty("horario_programado", horarioProgramadoIso)
        }
        api.confirmarIngestao(idReceita, dados)
            .exigirSucesso("Não foi possível confirmar a ingestão do medicamento.")
    }
}
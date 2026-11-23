package com.example.v2_medtime.data.repository

import com.example.v2_medtime.data.remote.RetrofitClient
import com.google.gson.JsonObject
import retrofit2.Response

class VinculoRepository {

    private val api get() = RetrofitClient.api

    suspend fun gerarCodigo(): Response<JsonObject> = api.gerarCodigoVinculo()

    suspend fun validarCodigo(codigo: String): Response<JsonObject> {
        val dados = JsonObject().apply {
            addProperty("codigo", codigo)
        }

        return api.validarCodigoVinculo(dados)
    }

    suspend fun listarPacientes(): Response<JsonObject> = api.listarPacientesVinculados()

    suspend fun buscarMeuCuidador(): Response<JsonObject> = api.buscarMeuCuidador()

    suspend fun encerrarVinculo(idPaciente: Long): Result<Unit> = requisitar {
        require(idPaciente > 0L) { "Paciente inválido para encerrar o vínculo." }
        api.encerrarVinculoPaciente(idPaciente)
            .exigirSucesso("Não foi possível encerrar o vínculo.")
    }
}
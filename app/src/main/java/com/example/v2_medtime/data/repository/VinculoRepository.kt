package com.example.v2_medtime.data.repository

import com.example.v2_medtime.data.remote.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
        val resposta = api.encerrarVinculoPaciente(idPaciente)
        if (!resposta.isSuccessful) {
            val erroBruto = resposta.errorBody()?.string().orEmpty()
            val mensagem = runCatching {
                JsonParser.parseString(erroBruto)
                    .asJsonObject
                    .get("message")
                    ?.takeUnless { it.isJsonNull }
                    ?.asString
                    ?.takeIf { it.isNotBlank() }
            }.getOrNull() ?: "Não foi possível encerrar o vínculo."
            error(mensagem)
        }
    }
}
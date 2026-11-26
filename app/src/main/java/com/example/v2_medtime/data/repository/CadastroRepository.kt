package com.example.v2_medtime.data.repository

import com.example.v2_medtime.data.remote.RetrofitClient
import com.google.gson.JsonObject
import retrofit2.Response

class CadastroRepository {

    suspend fun gerarCodigoVerificacaoEmail(
        email: String
    ): Response<JsonObject> {

        val dados = JsonObject().apply {
            addProperty("email", email)
        }

        return RetrofitClient.api.gerarCodigoVerificacaoEmail(dados)
    }

    suspend fun cadastrarUsuario(
        dados: JsonObject
    ): Response<JsonObject> {
        return RetrofitClient.api.cadastrarUsuario(dados)
    }
}
package com.example.v2_medtime.data.repository

import com.example.v2_medtime.data.remote.RetrofitClient
import com.google.gson.JsonObject
import retrofit2.Response

class LoginRepository {
    suspend fun autenticarLogin(
        email: String,
        senha: String
    ): Response<JsonObject> {

        val dados = JsonObject().apply {
            addProperty("email", email)
            addProperty("senha", senha)
        }

        return RetrofitClient.api.autenticarLogin(dados)
    }


    suspend fun autenticarGoogle(token: String): Response<JsonObject> {
        val dados = JsonObject().apply {
            addProperty("token", token)
        }

        return RetrofitClient.api.autenticarGoogle(dados)
    }

    suspend fun cadastrarUsuarioGoogle(
        token: String,
        cpf: String,
        telefone: String,
        dataNascimento: String,
        idRole: Int
    ): Response<JsonObject> {

        val dados = JsonObject().apply {
            addProperty("token", token)
            addProperty("cpf", cpf.ifBlank { null })
            addProperty("telefone", telefone.ifBlank { null })
            addProperty("data_nascimento", dataNascimento)

            add("usuario_role", JsonObject().apply {
                addProperty("id_role", idRole)
            })
        }

        return RetrofitClient.api.cadastrarUsuarioGoogle(dados)
    }
}
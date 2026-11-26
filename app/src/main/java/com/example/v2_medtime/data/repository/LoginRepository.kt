package com.example.v2_medtime.data.repository

import com.example.v2_medtime.data.remote.RetrofitClient
import com.google.gson.JsonObject
import retrofit2.Response
import java.time.LocalDate
import java.time.ZoneId

class LoginRepository {

    suspend fun validarSessao(idUsuario: Long, cuidador: Boolean): Response<JsonObject> =
        if (cuidador) {
            RetrofitClient.api.listarPacientesVinculados()
        } else {
            RetrofitClient.api.consultarAgenda(
                idUsuario = idUsuario,
                data = LocalDate.now().toString(),
                fuso = ZoneId.systemDefault().id
            )
        }
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

    suspend fun gerarCodigoRedefinicaoSenha(email: String): Response<JsonObject> {
        val dados = JsonObject().apply {
            addProperty("email", email)
        }

        return RetrofitClient.api.gerarCodigoRedefinicaoSenha(dados)
    }

    suspend fun redefinirSenha(
        email: String,
        codigo: String,
        novaSenha: String
    ): Response<JsonObject> {
        val dados = JsonObject().apply {
            addProperty("email", email)
            addProperty("codigo", codigo)
            addProperty("nova_senha", novaSenha)
        }

        return RetrofitClient.api.redefinirSenha(dados)
    }
}
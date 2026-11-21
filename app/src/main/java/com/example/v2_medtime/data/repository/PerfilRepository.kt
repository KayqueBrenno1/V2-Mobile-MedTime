package com.example.v2_medtime.data.repository

import com.example.v2_medtime.data.model.PerfilUsuario
import com.example.v2_medtime.data.remote.RetrofitClient
import com.google.gson.JsonObject
import retrofit2.Response

class PerfilRepository {

    suspend fun atualizarPerfil(perfil: PerfilUsuario, dataNascimentoApi: String): Response<JsonObject> {
        val dados = JsonObject().apply {
            add("usuario", JsonObject().apply {
                addProperty("nome", perfil.nomeCompleto.trim())
                addProperty("email", perfil.email.trim())
                addProperty("data_nascimento", dataNascimentoApi)
                if (perfil.cpf.isNotBlank()) {
                    addProperty("cpf", perfil.cpf.filter { it.isDigit() })
                }
                perfil.fotoUrlRemota?.let { addProperty("foto", it) }
            })
        }

        return RetrofitClient.api.atualizarUsuario(perfil.id, dados)
    }
}

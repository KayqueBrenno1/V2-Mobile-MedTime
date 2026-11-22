package com.example.v2_medtime.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.example.v2_medtime.data.model.PerfilUsuario
import com.example.v2_medtime.data.remote.RetrofitClient
import com.google.gson.JsonObject
import java.io.File
import retrofit2.Response

class PerfilRepository(
    contentResolver: ContentResolver,
    cacheDir: File
) {

    private val cloudinaryRepository = CloudinaryRepository(contentResolver, cacheDir)

    suspend fun enviarFoto(uri: Uri): String =
        cloudinaryRepository.uploadImage(uri).getOrThrow()

    suspend fun atualizarPerfil(
        perfil: PerfilUsuario,
        dataNascimentoApi: String
    ): Response<JsonObject> {
        val fotoUrl = perfil.fotoUrlRemota

        val dados = JsonObject().apply {
            add("usuario", JsonObject().apply {
                addProperty("nome", perfil.nomeCompleto.trim())
                addProperty("email", perfil.email.trim())
                addProperty("data_nascimento", dataNascimentoApi)
                if (perfil.cpf.isNotBlank()) {
                    addProperty("cpf", perfil.cpf.filter { it.isDigit() })
                }
                fotoUrl?.let { addProperty("foto", it) }
            })
        }

        return RetrofitClient.api.atualizarUsuario(perfil.id, dados)
    }
}
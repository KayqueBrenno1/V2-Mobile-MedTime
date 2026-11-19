package com.example.v2_medtime.data.remote

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MedTimeApi {

    @POST("v1/healthSystem/medtime/codigo_verificacao_email/gerar")
    suspend fun gerarCodigoVerificacaoEmail(
        @Body dados: JsonObject
    ): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("v1/healthSystem/medtime/usuario")
    suspend fun cadastrarUsuario(
        @Body dados: JsonObject
    ): Response<JsonObject>
}
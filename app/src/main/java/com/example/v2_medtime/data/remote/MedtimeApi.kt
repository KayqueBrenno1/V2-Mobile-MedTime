package com.example.v2_medtime.data.remote

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

    @Headers("Content-Type: application/json")
    @POST("v1/healthSystem/medtime/usuario/login")
    suspend fun autenticarLogin(
        @Body dados: JsonObject
    ): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("v1/healthSystem/medtime/usuario/google/login")
    suspend fun autenticarGoogle(
        @Body dados: JsonObject
    ): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("v1/healthSystem/medtime/usuario/google/cadastro")
    suspend fun cadastrarUsuarioGoogle(
        @Body dados: JsonObject
    ): Response<JsonObject>

    @POST("v1/healthSystem/medtime/codigo_verificacao_email/redefinir_senha/gerar")
    suspend fun gerarCodigoRedefinicaoSenha(@Body dados: JsonObject): Response<JsonObject>

    @POST("v1/healthSystem/medtime/codigo_verificacao_email/redefinir_senha")
    suspend fun redefinirSenha(@Body dados: JsonObject): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @PUT("v1/healthSystem/medtime/usuario/{id}")
    suspend fun atualizarUsuario(
        @Path("id") idUsuario: Long,
        @Body dados: JsonObject
    ): Response<JsonObject>

    @POST("v1/healthSystem/medtime/codigo_vinculo/gerar")
    suspend fun gerarCodigoVinculo(): Response<JsonObject>

    @POST("v1/healthSystem/medtime/codigo_vinculo/validar")
    suspend fun validarCodigoVinculo(@Body dados: JsonObject): Response<JsonObject>

    @GET("v1/healthSystem/medtime/vinculo_paciente/meus_pacientes")
    suspend fun listarPacientesVinculados(): Response<JsonObject>

    @GET("v1/healthSystem/medtime/tipo_dosagem")
    suspend fun listarTiposDosagem(): Response<JsonObject>

    @GET("v1/healthSystem/medtime/unidade_medida")
    suspend fun listarUnidadesMedida(): Response<JsonObject>

    @GET("v1/healthSystem/medtime/tipo_intervalo")
    suspend fun listarTiposIntervalo(): Response<JsonObject>

    @GET("v1/healthSystem/medtime/receita_medica/usuario/{idUsuario}")
    suspend fun listarReceitas(@Path("idUsuario") idUsuario: Long): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("v1/healthSystem/medtime/cadastro_medicamento/{idUsuario}")
    suspend fun cadastrarMedicamento(
        @Path("idUsuario") idUsuario: Long,
        @Body dados: JsonObject
    ): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @PATCH("v1/healthSystem/medtime/editar_medicamento/{idReceita}/{idUsuario}")
    suspend fun editarMedicamento(
        @Path("idReceita") idReceita: Long,
        @Path("idUsuario") idUsuario: Long,
        @Body dados: JsonObject
    ): Response<JsonObject>

    @DELETE("v1/healthSystem/medtime/excluir_medicamento/{idReceita}/{idUsuario}")
    suspend fun excluirMedicamento(
        @Path("idReceita") idReceita: Long,
        @Path("idUsuario") idUsuario: Long
    ): Response<JsonObject>

    @Multipart
    @POST("v1/healthSystem/medtime/ocr/{idUsuario}")
    suspend fun escanearReceita(
        @Path("idUsuario") idUsuario: Long,
        @Part receita: MultipartBody.Part
    ): Response<JsonObject>

    @GET("v1/healthSystem/medtime/agenda_medicamento/{idUsuario}")
    suspend fun consultarAgenda(
        @Path("idUsuario") idUsuario: Long,
        @Query("data") data: String,
        @Query("fuso") fuso: String
    ): Response<JsonObject>

    @GET("v1/healthSystem/medtime/estoque/usuario/{idUsuario}")
    suspend fun consultarEstoque(@Path("idUsuario") idUsuario: Long): Response<JsonObject>

    @GET("v1/healthSystem/medtime/historico_medicamento/usuario/{idUsuario}")
    suspend fun consultarHistorico(@Path("idUsuario") idUsuario: Long): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @PATCH("v1/healthSystem/medtime/historico_medicamento/confirmar_ingestao/{idReceita}")
    suspend fun confirmarIngestao(
        @Path("idReceita") idReceita: Long,
        @Body dados: JsonObject
    ): Response<JsonObject>
}

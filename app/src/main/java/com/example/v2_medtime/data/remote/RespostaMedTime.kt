package com.example.v2_medtime.data.remote

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Response

internal fun Response<JsonObject>.confirmouSucesso(): Boolean =
    isSuccessful && body()?.get("status")?.asBoolean == true

internal fun Response<JsonObject>.mensagemDeErro(mensagemPadrao: String): String {
    val corpo = errorBody()?.string()
    return runCatching {
        val dados = corpo?.let { JsonParser.parseString(it).asJsonObject } ?: body()
        dados?.get("message")?.asString
    }.getOrNull() ?: mensagemPadrao
}

internal fun JsonObject.texto(campo: String): String? =
    get(campo)?.takeIf { it.isJsonPrimitive }?.asString

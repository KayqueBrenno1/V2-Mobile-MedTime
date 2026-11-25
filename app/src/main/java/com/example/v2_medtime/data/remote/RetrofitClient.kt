package com.example.v2_medtime.data.remote

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://medtime-backend.onrender.com"
    private lateinit var applicationContext: Context

    fun inicializar(context: Context) {
        applicationContext = context.applicationContext
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(75, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(AuthInterceptor(applicationContext))
            .build()
    }

    private val ocrClient: OkHttpClient by lazy {
        client.newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .callTimeout(200, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
    }

    val ocrApi: MedTimeApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(ocrClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MedTimeApi::class.java)
    }

    suspend fun acordarServidor(tentativasMaximas: Int = 3): Boolean {
        repeat(tentativasMaximas) { tentativa ->
            val respondeu = runCatching {
                withContext(Dispatchers.IO) {
                    val req = Request.Builder()
                        .url(BASE_URL)
                        .get()
                        .build()
                    client.newCall(req).execute().use { response ->
                        response.code in 200..499
                    }
                }
            }.getOrDefault(false)

            if (respondeu) return true
            if (tentativa < tentativasMaximas - 1) delay(2_000L)
        }
        return false
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: MedTimeApi by lazy {
        retrofit.create(MedTimeApi::class.java)
    }
}
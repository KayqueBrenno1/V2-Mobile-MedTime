package com.example.v2_medtime.data.remote

import android.content.Context
import com.example.v2_medtime.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

    private val tokenManager = TokenManager(context.applicationContext)

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = tokenManager.obterToken()

        val request = chain.request()
            .newBuilder()
            .apply {
                if (!token.isNullOrBlank()) {
                    header("Authorization", "Bearer $token")
                }
            }
            .build()

        val response = chain.proceed(request)
        if (response.code == 401 && !token.isNullOrBlank()) {
            tokenManager.removerToken()
            AuthSessionEvents.notifySessionEnded()
        }
        return response
    }
}

package com.example.v2_medtime.data.remote

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.v2_medtime.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleAuthManager(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    suspend fun autenticarGoogle(): String {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(
                context.getString(R.string.google_web_client_id)
            )
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val resultado = credentialManager.getCredential(
            context = context,
            request = request
        )

        val credencial = resultado.credential

        if (
            credencial is CustomCredential &&
            credencial.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCredential = GoogleIdTokenCredential.createFrom(
                credencial.data
            )

            return googleCredential.idToken
        }

        throw IllegalStateException(
            "Não foi possível obter o token da conta Google."
        )
    }
}

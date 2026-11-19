package com.example.v2_medtime.data.local

import android.content.Context
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties

class TokenManager(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(
        "medtime_sessao",
        Context.MODE_PRIVATE
    )

    private val alias = "medtime_jwt_key"

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private fun obterChave(): SecretKey {
        val chaveExistente = keyStore.getKey(alias, null) as? SecretKey

        if (chaveExistente != null)
            return chaveExistente

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        val especificacao = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(especificacao)

        return keyGenerator.generateKey()
    }

    fun salvarToken(token: String): Boolean {
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")

            cipher.init(
                Cipher.ENCRYPT_MODE,
                obterChave()
            )

            val tokenCriptografado = cipher.doFinal(
                token.toByteArray(Charsets.UTF_8)
            )

            val iv = Base64.encodeToString(
                cipher.iv,
                Base64.NO_WRAP
            )

            val conteudo = Base64.encodeToString(
                tokenCriptografado,
                Base64.NO_WRAP
            )

            preferences.edit()
                .putString("jwt", conteudo)
                .putString("jwt_iv", iv)
                .commit()

        } catch (error: Exception) {
            false
        }
    }

    fun obterToken(): String? {
        return try {
            val conteudo = preferences.getString("jwt", null)
                ?: return null

            val iv = preferences.getString("jwt_iv", null)
                ?: return null

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")

            cipher.init(
                Cipher.DECRYPT_MODE,
                obterChave(),
                GCMParameterSpec(
                    128,
                    Base64.decode(iv, Base64.NO_WRAP)
                )
            )

            val tokenDescriptografado = cipher.doFinal(
                Base64.decode(conteudo, Base64.NO_WRAP)
            )

            String(tokenDescriptografado, Charsets.UTF_8)

        } catch (error: Exception) {
            null
        }
    }

    fun removerToken(): Boolean {
        return preferences.edit()
            .remove("jwt")
            .remove("jwt_iv")
            .commit()
    }
}
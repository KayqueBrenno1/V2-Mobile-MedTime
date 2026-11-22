package com.example.v2_medtime.data.local

import android.content.Context
import android.util.Base64
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties

data class SessaoLocal(
    val token: String,
    val idUsuario: Long,
    val nome: String,
    val email: String
)

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
        if (chaveExistente != null) return chaveExistente

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
            val (conteudo, iv) = criptografar(token)
            preferences.edit()
                .putString("jwt", conteudo)
                .putString("jwt_iv", iv)
                .commit()
        } catch (_: Exception) {
            false
        }
    }

    fun salvarSessao(token: String, usuario: JsonObject): Boolean {
        return try {
            val dadosSessao = JsonObject().apply {
                addProperty("id", obterIdUsuario(usuario, token))
                addProperty("nome", usuario.get("nome")?.takeUnless { it.isJsonNull }?.asString.orEmpty())
                addProperty("email", usuario.get("email")?.takeUnless { it.isJsonNull }?.asString.orEmpty())
            }
            val (tokenCifrado, tokenIv) = criptografar(token)
            val (usuarioCifrado, usuarioIv) = criptografar(dadosSessao.toString())

            preferences.edit()
                .putString("jwt", tokenCifrado)
                .putString("jwt_iv", tokenIv)
                .putString("usuario", usuarioCifrado)
                .putString("usuario_iv", usuarioIv)
                .commit()
        } catch (_: Exception) {
            false
        }
    }

    fun obterToken(): String? {
        return try {
            val conteudo = preferences.getString("jwt", null) ?: return null
            val iv = preferences.getString("jwt_iv", null) ?: return null
            descriptografar(conteudo, iv)
        } catch (_: Exception) {
            null
        }
    }

    fun obterSessao(): SessaoLocal? {
        val token = obterToken() ?: return null
        return try {
            val conteudo = preferences.getString("usuario", null) ?: return null
            val iv = preferences.getString("usuario_iv", null) ?: return null
            val usuario = JsonParser.parseString(descriptografar(conteudo, iv)).asJsonObject
            val id = usuario.get("id")?.takeUnless { it.isJsonNull }?.asLong ?: 0L
            if (id <= 0L) return null
            SessaoLocal(
                token = token,
                idUsuario = id,
                nome = usuario.get("nome")?.takeUnless { it.isJsonNull }?.asString.orEmpty(),
                email = usuario.get("email")?.takeUnless { it.isJsonNull }?.asString.orEmpty()
            )
        } catch (_: Exception) {
            null
        }
    }

    fun removerToken(): Boolean {
        return preferences.edit()
            .remove("jwt")
            .remove("jwt_iv")
            .remove("usuario")
            .remove("usuario_iv")
            .commit()
    }

    private fun criptografar(valor: String): Pair<String, String> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, obterChave())
        val cifrado = cipher.doFinal(valor.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(cifrado, Base64.NO_WRAP) to
            Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
    }

    private fun descriptografar(conteudo: String, iv: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            obterChave(),
            GCMParameterSpec(128, Base64.decode(iv, Base64.NO_WRAP))
        )
        return String(
            cipher.doFinal(Base64.decode(conteudo, Base64.NO_WRAP)),
            Charsets.UTF_8
        )
    }

    private fun obterIdUsuario(usuario: JsonObject, token: String): Long {
        val idResposta = sequenceOf("id", "id_usuario")
            .mapNotNull { chave ->
                runCatching {
                    usuario.get(chave)?.takeUnless { it.isJsonNull }?.asLong
                }.getOrNull()
            }
            .firstOrNull { it > 0L }
        if (idResposta != null) return idResposta

        return runCatching {
            val parte = token.split('.')[1]
            val bytes = Base64.decode(parte, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val claims = JsonParser.parseString(String(bytes, Charsets.UTF_8)).asJsonObject
            sequenceOf("id", "id_usuario", "sub")
                .mapNotNull { chave ->
                    runCatching { claims.get(chave)?.takeUnless { it.isJsonNull }?.asLong }.getOrNull()
                }
                .firstOrNull { it > 0L } ?: 0L
        }.getOrDefault(0L)
    }
}

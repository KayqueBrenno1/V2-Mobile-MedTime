package com.example.v2_medtime.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.v2_medtime.data.remote.CloudinaryApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class CloudinaryRepository(
    private val contentResolver: ContentResolver,
    private val cacheDir: File
) {

    private val api = CloudinaryApi.create()

    private val cloudName = "dl5pjsesc"
    private val uploadPreset = "medtime_mobile"

    suspend fun uploadImage(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        var arquivoTemporario: File? = null

        try {
            val mimeType = contentResolver.getType(uri)
            val extensao = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(mimeType)
                ?.takeIf { it.isNotBlank() }
                ?: "img"
            val arquivo = File.createTempFile("medtime_upload_", ".$extensao", cacheDir)
            arquivoTemporario = arquivo

            val input = contentResolver.openInputStream(uri)
                ?: throw IOException("Não foi possível abrir a imagem selecionada.")
            input.use { entrada ->
                arquivo.outputStream().use { saida -> entrada.copyTo(saida) }
            }

            Result.success(uploadImage(arquivo).getOrThrow())
        } catch (cancelamento: CancellationException) {
            throw cancelamento
        } catch (erro: Exception) {
            Result.failure(erro)
        } finally {
            arquivoTemporario?.delete()
        }
    }

    suspend fun uploadImage(file: File): Result<String> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val presetBody = uploadPreset.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = api.uploadImage(cloudName, presetBody, imagePart)

            if (response.isSuccessful) {
                val imageUrl = response.body()?.secureUrl
                if (!imageUrl.isNullOrBlank()) {
                    Result.success(imageUrl)
                } else {
                    Result.failure(Exception("O Cloudinary não retornou a URL da imagem."))
                }
            } else {
                Result.failure(Exception("Falha no upload: HTTP ${response.code()}"))
            }
        } catch (cancelamento: CancellationException) {
            throw cancelamento
        } catch (erro: Exception) {
            Result.failure(erro)
        }
    }
}
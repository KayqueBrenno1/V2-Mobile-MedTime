package com.example.v2_medtime.data.remote

import com.example.v2_medtime.data.model.CloudinaryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CloudinaryApi {

    @Multipart
    @POST("v1_1/{cloudName}/image/upload")
    suspend fun uploadImage(
        @Path("cloudName") cloudName: String,
        @Part("upload_preset") uploadPreset: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<CloudinaryResponse>

    companion object {
        fun create(): CloudinaryApi {
            return Retrofit.Builder()
                .baseUrl("https://api.cloudinary.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CloudinaryApi::class.java)
        }
    }
}
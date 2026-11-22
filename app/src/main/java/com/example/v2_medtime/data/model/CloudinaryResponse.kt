package com.example.v2_medtime.data.model

import com.google.gson.annotations.SerializedName

data class CloudinaryResponse(
    @SerializedName("public_id")
    val publicId: String,

    @SerializedName("secure_url")
    val secureUrl: String,

    val format: String
)
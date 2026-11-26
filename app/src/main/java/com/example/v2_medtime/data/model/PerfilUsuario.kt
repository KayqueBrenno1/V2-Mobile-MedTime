package com.example.v2_medtime.data.model

import android.net.Uri

data class PerfilUsuario(
    val id: Long = 0L,
    val nomeCompleto: String = "",
    val email: String = "",
    val dataNascimento: String = "",
    val cpf: String = "",
    val fotoUriLocal: Uri? = null,
    val fotoUrlRemota: String? = null
)
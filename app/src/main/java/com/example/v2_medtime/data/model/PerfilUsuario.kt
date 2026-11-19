package com.example.v2_medtime.data.model

import android.net.Uri

data class PerfilUsuario(
    val id: Long = 1L,
    val nomeCompleto: String = "Ana Souza",
    val email: String = "ana.souza@medtime.com",
    val dataNascimento: String = "12/05/1998",
    val cpf: String = "123.456.789-00",
    val fotoUriLocal: Uri? = null,
    val fotoUrlRemota: String? = null
)
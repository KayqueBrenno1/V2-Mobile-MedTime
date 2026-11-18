package com.example.v2_medtime.screens.auth

import androidx.compose.runtime.Composable

@Composable
fun VerificarCodigoSenhaScreen(
    email: String,
    onVerificar: (String) -> Unit,
    onReenviarCodigo: () -> Unit,
    onVoltar: () -> Unit
) {
    ConfirmarEmailScreen(
        email = email,
        onVerificar = onVerificar,
        onReenviarCodigo = onReenviarCodigo,
        onVoltar = onVoltar,
        titulo = "Verifique seu código",
        mostrarIndicador = false
    )
}

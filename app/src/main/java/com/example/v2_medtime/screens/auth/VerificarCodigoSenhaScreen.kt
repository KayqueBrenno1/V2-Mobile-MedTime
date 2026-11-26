package com.example.v2_medtime.screens.auth

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.v2_medtime.viewmodel.ConfirmarEmailViewModel

@Composable
fun VerificarCodigoSenhaScreen(
    email: String,
    onVerificar: (String) -> Unit,
    onReenviarCodigo: () -> Unit,
    onVoltar: () -> Unit,
    confirmarEmailViewModel: ConfirmarEmailViewModel = viewModel()
) {
    ConfirmarEmailScreen(
        email = email,
        onVerificar = onVerificar,
        onReenviarCodigo = onReenviarCodigo,
        onVoltar = onVoltar,
        titulo = "Verifique seu código",
        mostrarIndicador = false,
        confirmarEmailViewModel = confirmarEmailViewModel
    )
}

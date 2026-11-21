package com.example.v2_medtime.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.R
import com.example.v2_medtime.screens.components.BackgroundPadrao

private val AzulSenha = Color(0xFF086FA7)
private val FundoSenha = Color(0xFFF3F8FD)
private val TextoSenha = Color(0xFF101A2A)

@Composable
fun AlterarSenhaScreen(
    navController: NavController,
    onAlterarSenha: (senhaAtual: String, novaSenha: String) -> Unit = { _, _ -> }
) {
    var senhaAtual by remember { mutableStateOf("") }
    var novaSenha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var tentouEnviar by remember { mutableStateOf(false) }

    val erroSenhaAtual = "Informe a senha atual.".takeIf { senhaAtual.isBlank() }
    val erroNovaSenha = when {
        novaSenha.isBlank() -> "Informe a nova senha."
        novaSenha == senhaAtual -> "Escolha uma senha diferente da atual."
        else -> null
    }
    val erroConfirmacao = when {
        confirmarSenha.isBlank() -> "Confirme a nova senha."
        novaSenha.isNotBlank() && confirmarSenha != novaSenha -> "As senhas não coincidem."
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoSenha)
    ) {
        BackgroundPadrao()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(35.dp))

            Text(
                text = "Alterar senha",
                modifier = Modifier.semantics { heading() },
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Box(
                modifier = Modifier
                    .size(112.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(Color(0xFFD8EAFE), RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = Color(0xFF168FD2),
                    modifier = Modifier.size(55.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(24.dp))

            CampoSenhaMedTime(
                titulo = "Senha atual",
                valor = senhaAtual,
                onValorAlterado = { senhaAtual = it },
                acaoTeclado = ImeAction.Next,
                erro = erroSenhaAtual.takeIf { tentouEnviar }
            )

            Spacer(modifier = Modifier.height(12.dp))

            CampoSenhaMedTime(
                titulo = "Nova senha",
                valor = novaSenha,
                onValorAlterado = { novaSenha = it },
                acaoTeclado = ImeAction.Next,
                erro = erroNovaSenha.takeIf { tentouEnviar }
            )

            Spacer(modifier = Modifier.height(12.dp))

            CampoSenhaMedTime(
                titulo = "Confirmar nova senha",
                valor = confirmarSenha,
                onValorAlterado = { confirmarSenha = it },
                acaoTeclado = ImeAction.Done,
                erro = erroConfirmacao.takeIf { tentouEnviar }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    tentouEnviar = true
                    if (erroSenhaAtual == null && erroNovaSenha == null && erroConfirmacao == null) {
                        onAlterarSenha(senhaAtual, novaSenha)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulSenha
                )
            ) {
                Text(
                    text = "Alterar senha",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, colorResource(R.color.azul_borda))
            ) {
                Text(
                    text = "Voltar",
                    fontSize = 19.sp,
                    color = colorResource(R.color.azul_entrar)
                )
            }

            Spacer(modifier = Modifier.height(35.dp))
        }
    }
}

@Composable
private fun CampoSenhaMedTime(
    titulo: String,
    valor: String,
    onValorAlterado: (String) -> Unit,
    acaoTeclado: ImeAction,
    erro: String?
) {
    var senhaVisivel by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = onValorAlterado,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(titulo) },
        singleLine = true,
        isError = erro != null,
        supportingText = erro?.let { mensagem -> { Text(mensagem) } },
        visualTransformation = if (senhaVisivel) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = acaoTeclado
        ),
        trailingIcon = {
            IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                Icon(
                    imageVector = if (senhaVisivel) {
                        Icons.Outlined.VisibilityOff
                    } else {
                        Icons.Outlined.Visibility
                    },
                    contentDescription = if (senhaVisivel) {
                        "Ocultar ${titulo.lowercase()}"
                    } else {
                        "Mostrar ${titulo.lowercase()}"
                    },
                    tint = TextoSenha
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorContainerColor = Color.White,
            focusedBorderColor = AzulSenha,
            unfocusedBorderColor = Color(0xFF78879B),
            focusedTextColor = TextoSenha,
            unfocusedTextColor = TextoSenha,
            focusedLabelColor = AzulSenha,
            unfocusedLabelColor = TextoSenha
        )
    )
}

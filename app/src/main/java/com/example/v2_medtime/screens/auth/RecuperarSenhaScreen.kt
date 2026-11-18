package com.example.v2_medtime.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.v2_medtime.R
import com.example.v2_medtime.screens.components.BackgroundPadrao

@Composable
fun RecuperarSenhaScreen(
    onVoltar: () -> Unit,
    onEnviarCodigo: (String) -> Unit
) {
    var email by rememberSaveable {
        mutableStateOf("")
    }

    val azulTexto = colorResource(R.color.azul_texto)
    val azulBotao = colorResource(R.color.azul_botao)
    val azulBorda = colorResource(R.color.azul_borda)

    val emailValido = android.util.Patterns.EMAIL_ADDRESS
        .matcher(email)
        .matches()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.azul_fundo))
    ) {
        BackgroundPadrao()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(85.dp))

                Text(
                    text = "Esqueceu \nsua senha?",
                    lineHeight = 35.sp,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = azulTexto
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Informe o e-mail cadastrado na sua conta. " +
                            "Enviaremos um código de verificação " +
                            "para você redefinir sua senha.",
                    fontSize = 15.sp,
                    lineHeight = 27.sp,
                    color = azulTexto
                )

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "E-mail",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = azulTexto
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { valor ->
                        email = valor
                            .filterNot { it.isWhitespace() }
                            .take(255)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 65.dp),
                    placeholder = {
                        Text(
                            text = "exemplo@email.com",
                            color = Color.Black.copy(alpha = 0.4f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            tint = azulTexto
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = azulBotao,
                        unfocusedBorderColor = azulBorda,
                        focusedTextColor = azulTexto,
                        unfocusedTextColor = azulTexto,
                        focusedPlaceholderColor = Color.Black.copy(alpha = 0.4f),
                        unfocusedPlaceholderColor = Color.Black.copy(alpha = 0.4f)
                    )
                )

                Spacer(modifier = Modifier.height(90.dp))

                Button(
                    onClick = {
                        onEnviarCodigo(email.trim())
                    },
                    enabled = emailValido,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = azulBotao,
                        disabledContainerColor = azulBotao,
                        contentColor = Color.White,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Enviar código",
                        fontSize = 19.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onVoltar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        azulBorda
                    )
                ) {
                    Text(
                        text = "Voltar",
                        fontSize = 19.sp,
                        color = colorResource(R.color.azul_entrar)
                    )
                }
            }
        }
    }
}
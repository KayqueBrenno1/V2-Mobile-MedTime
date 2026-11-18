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
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.v2_medtime.R
import com.example.v2_medtime.screens.components.BackgroundPadrao

@Composable
fun RedefinirSenhaScreen(
    onVoltar: () -> Unit,
    onRedefinirSenha: (String) -> Unit
) {
    var novaSenha by rememberSaveable {
        mutableStateOf("")
    }

    var confirmarSenha by rememberSaveable {
        mutableStateOf("")
    }

    var mostrarNovaSenha by rememberSaveable {
        mutableStateOf(false)
    }

    var mostrarConfirmarSenha by rememberSaveable {
        mutableStateOf(false)
    }

    val azulTexto = colorResource(R.color.azul_texto)
    val azulBotao = colorResource(R.color.azul_botao)
    val azulBorda = colorResource(R.color.azul_borda)

    val senhasCoincidem = novaSenha == confirmarSenha
    val senhaValida = novaSenha.length >= 8
    val podeRedefinir = senhaValida && senhasCoincidem

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
                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = "Redefinir senha",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = azulTexto
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Crie uma nova senha para acessar sua conta com segurança.",
                    fontSize = 17.sp,
                    lineHeight = 27.sp,
                    color = azulTexto
                )

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Nova senha",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = azulTexto
                )

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = novaSenha,
                    onValueChange = { novaSenha = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 68.dp),
                    placeholder = {
                        Text("Digite sua nova senha", color = Color.Black.copy(alpha = 0.4f))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = azulTexto
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                mostrarNovaSenha = !mostrarNovaSenha
                            }
                        ) {
                            Icon(
                                imageVector = if (mostrarNovaSenha) {
                                    Icons.Outlined.Visibility
                                } else {
                                    Icons.Outlined.VisibilityOff
                                },
                                contentDescription = "Mostrar ou ocultar senha",
                                tint = azulTexto
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (mostrarNovaSenha) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = azulBotao,
                        unfocusedBorderColor = azulBorda,
                        focusedTextColor = azulTexto,
                        unfocusedTextColor = azulTexto
                    )
                )

                if (novaSenha.isNotEmpty() && novaSenha.length < 8) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "A senha deve conter pelo menos 8 caracteres.",
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }

                Spacer(modifier = Modifier.height(35.dp))

                Text(
                    text = "Confirmar nova senha",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = azulTexto
                )

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = confirmarSenha,
                    onValueChange = { confirmarSenha = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 68.dp),
                    placeholder = {
                        Text("Confirme sua nova senha", color = Color.Black.copy(alpha = 0.4f))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = azulTexto
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                mostrarConfirmarSenha = !mostrarConfirmarSenha
                            }
                        ) {
                            Icon(
                                imageVector = if (mostrarConfirmarSenha) {
                                    Icons.Outlined.Visibility
                                } else {
                                    Icons.Outlined.VisibilityOff
                                },
                                contentDescription = "Mostrar ou ocultar senha",
                                tint = azulTexto
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (mostrarConfirmarSenha) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = azulBotao,
                        unfocusedBorderColor = azulBorda,
                        focusedTextColor = azulTexto,
                        unfocusedTextColor = azulTexto
                    )
                )

                if (confirmarSenha.isNotEmpty() && !senhasCoincidem) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "As senhas não coincidem.",
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        onRedefinirSenha(novaSenha)
                    },
                    enabled = podeRedefinir,
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
                        text = "Redefinir senha",
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

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
package com.example.v2_medtime.screens.auth

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
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
fun CriarSenhaScreen(
    onVoltar: () -> Unit,
    onProximo: (String) -> Unit,
    senhaInicial: String = ""
) {
    val density = LocalDensity.current
    val tecladoAberto = WindowInsets.ime.getBottom(density) > 0

    var senha by remember {
        mutableStateOf(senhaInicial)
    }

    var confirmarSenha by remember {
        mutableStateOf(senhaInicial)
    }

    var mostrarSenha by rememberSaveable { mutableStateOf(false) }
    var mostrarConfirmacao by rememberSaveable { mutableStateOf(false) }
    var tentouProximo by rememberSaveable { mutableStateOf(false) }

    val azulTexto = colorResource(R.color.azul_texto)
    val azulBorda = colorResource(R.color.azul_borda)
    val azulBotao = colorResource(R.color.azul_botao)

    val erroSenha = tentouProximo && senha.length < 8
    val erroConfirmacao = tentouProximo &&
            (confirmarSenha.isEmpty() || senha != confirmarSenha)

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
            Spacer(modifier = Modifier.height(25.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(35.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (etapa in 1..3) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    color = if (etapa == 2) {
                                        azulBotao
                                    } else {
                                        colorResource(R.color.azul_curva)
                                    },
                                    shape = RoundedCornerShape(50)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = etapa.toString(),
                                fontSize = 14.sp,
                                color = if (etapa == 2) {
                                    Color.White
                                } else {
                                    azulTexto
                                }
                            )
                        }

                        if (etapa < 3) {
                            HorizontalDivider(
                                modifier = Modifier.width(90.dp),
                                color = azulBorda
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(38.dp))

                Text(
                    text = "Segurança da conta",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = azulTexto
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Crie uma senha para proteger sua conta.",
                    fontSize = 17.sp,
                    color = azulTexto
                )

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 68.dp),
                    label = { Text("Criar senha") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { mostrarSenha = !mostrarSenha }
                        ) {
                            Icon(
                                imageVector = if (mostrarSenha) {
                                    Icons.Outlined.Visibility
                                } else {
                                    Icons.Outlined.VisibilityOff
                                },
                                contentDescription = if (mostrarSenha) {
                                    "Ocultar senha"
                                } else {
                                    "Mostrar senha"
                                }
                            )
                        }
                    },
                    visualTransformation = if (mostrarSenha) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    singleLine = true,
                    isError = erroSenha,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = azulBotao,
                        unfocusedBorderColor = azulBorda,
                        focusedTextColor = azulTexto,
                        unfocusedTextColor = azulTexto,
                        focusedLabelColor = azulTexto,
                        unfocusedLabelColor = azulTexto,
                        focusedLeadingIconColor = azulTexto,
                        unfocusedLeadingIconColor = azulTexto,
                        focusedTrailingIconColor = azulTexto,
                        unfocusedTrailingIconColor = azulTexto,
                        errorBorderColor = Color.Red,
                        errorTextColor = azulTexto,
                        errorLabelColor = azulTexto,
                        errorLeadingIconColor = azulTexto,
                        errorTrailingIconColor = azulTexto
                    )
                )

                if (erroSenha) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "A senha deve conter pelo menos 8 caracteres.",
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmarSenha,
                    onValueChange = { confirmarSenha = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 68.dp),
                    label = { Text("Confirmar senha") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                mostrarConfirmacao = !mostrarConfirmacao
                            }
                        ) {
                            Icon(
                                imageVector = if (mostrarConfirmacao) {
                                    Icons.Outlined.Visibility
                                } else {
                                    Icons.Outlined.VisibilityOff
                                },
                                contentDescription = if (mostrarConfirmacao) {
                                    "Ocultar senha"
                                } else {
                                    "Mostrar senha"
                                }
                            )
                        }
                    },
                    visualTransformation = if (mostrarConfirmacao) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    singleLine = true,
                    isError = erroConfirmacao,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = azulBotao,
                        unfocusedBorderColor = azulBorda,
                        focusedTextColor = azulTexto,
                        unfocusedTextColor = azulTexto,
                        focusedLabelColor = azulTexto,
                        unfocusedLabelColor = azulTexto,
                        focusedLeadingIconColor = azulTexto,
                        unfocusedLeadingIconColor = azulTexto,
                        focusedTrailingIconColor = azulTexto,
                        unfocusedTrailingIconColor = azulTexto,
                        errorBorderColor = Color.Red,
                        errorTextColor = azulTexto,
                        errorLabelColor = azulTexto,
                        errorLeadingIconColor = azulTexto,
                        errorTrailingIconColor = azulTexto
                    )
                )

                if (erroConfirmacao) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (confirmarSenha.isEmpty()) {
                            "Confirme sua senha."
                        } else {
                            "As senhas não coincidem."
                        },
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (!tecladoAberto) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        tentouProximo = true

                        if (senha.length >= 8 && confirmarSenha == senha) {
                            onProximo(senha)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = azulBotao
                    )
                ) {
                    Text(
                        text = "Próximo",
                        fontSize = 19.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onVoltar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, azulBorda)
                ) {
                    Text(
                        text = "Voltar",
                        fontSize = 19.sp,
                        color = colorResource(R.color.azul_entrar)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
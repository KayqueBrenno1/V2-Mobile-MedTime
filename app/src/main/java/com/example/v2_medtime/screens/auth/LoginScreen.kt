package com.example.v2_medtime.screens.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.v2_medtime.R
import com.example.v2_medtime.screens.components.BackgroundPadrao

@Composable
fun LoginScreen(
    onVoltar: () -> Unit,
    onEntrar: (String, String) -> Unit,
    onEsqueceuSenha: () -> Unit,
    onEntrarGoogle: () -> Unit,
    onCadastrar: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var mostrarSenha by rememberSaveable { mutableStateOf(false) }

    var tentouEntrar by rememberSaveable { mutableStateOf(false) }

    val emailValido = Patterns.EMAIL_ADDRESS
        .matcher(email.trim())
        .matches()

    val erroEmail = tentouEntrar && !emailValido
    val erroSenha = tentouEntrar && senha.isBlank()

    val azulTexto = colorResource(R.color.azul_texto)
    val azulBotao = colorResource(R.color.azul_botao)
    val azulBorda = colorResource(R.color.azul_borda)
    val azulEntrar = colorResource(R.color.azul_entrar)

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
            Spacer(modifier = Modifier.height(35.dp))

            Row(
                modifier = Modifier.clickable { onVoltar() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBackIos,
                    contentDescription = "Voltar",
                    tint = azulTexto,
                    modifier = Modifier
                        .size(20.dp)
                )

                Text(
                    text = "Voltar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = azulTexto
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                Text(
                    text = "Entrar",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = azulTexto
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Acesse sua conta para continuar.",
                    fontSize = 18.sp,
                    color = azulTexto
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    isError = erroEmail,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp),
                    placeholder = {
                        Text(
                            text = "E-mail",
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
                        keyboardType = KeyboardType.Email
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = azulBotao,
                        unfocusedBorderColor = azulBorda,
                        focusedTextColor = azulTexto,
                        unfocusedTextColor = azulTexto,
                        errorBorderColor = Color.Red,
                        errorTextColor = azulTexto,
                        errorCursorColor = azulTexto,
                        errorLeadingIconColor = azulTexto
                    )
                )

                if (erroEmail) {
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (email.isBlank()) {
                            "Informe seu e-mail."
                        } else {
                            "Informe um e-mail válido."
                        },
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    isError = erroSenha,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp),
                    placeholder = {
                        Text(
                            text = "Senha",
                            color = Color.Black.copy(alpha = 0.4f)
                        )
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
                                },
                                tint = azulTexto
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (mostrarSenha) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        errorBorderColor = Color.Red,
                        focusedBorderColor = azulBotao,
                        unfocusedBorderColor = azulBorda,
                        focusedTextColor = azulTexto,
                        unfocusedTextColor = azulTexto
                    )
                )

                if (erroSenha) {
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Informe sua senha.",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Esqueceu a senha?",
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { onEsqueceuSenha() },
                    fontSize = 16.sp,
                    color = azulEntrar,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        tentouEntrar = true

                        if (emailValido && senha.isNotBlank()) {
                            onEntrar(email.trim(), senha)
                        }
                    },
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
                        text = "Entrar",
                        fontSize = 19.sp,
                        color = colorResource(R.color.branco)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = azulBorda
                    )

                    Text(
                        text = "ou",
                        fontSize = 18.sp,
                        color = azulTexto
                    )

                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = azulBorda
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onEntrarGoogle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        azulBorda
                    )
                ) {
                    GoogleIcone()

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Text(
                        text = "Continuar com Google",
                        fontSize = 18.sp,
                        color = azulTexto
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Não tem uma conta? ",
                        fontSize = 16.sp,
                        color = azulTexto
                    )

                    Text(
                        text = "Cadastre-se",
                        modifier = Modifier.clickable { onCadastrar() },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = azulEntrar
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun GoogleIcone() {
    Icon(
        painter = painterResource(id = R.drawable.group),
        contentDescription = "Ícone da google",
        modifier = Modifier
            .size(24.dp),
        tint = Color.Unspecified
    )
}
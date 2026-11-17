package com.example.v2_medtime.screens.auth

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.v2_medtime.R
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.viewmodel.ConfirmarEmailViewModel

@Composable
fun ConfirmarEmailScreen(
    email: String,
    onVerificar: (String) -> Unit,
    onReenviarCodigo: () -> Unit,
    onVoltar: () -> Unit,
    confirmarEmailViewModel: ConfirmarEmailViewModel = viewModel()
) {
    val codigo = confirmarEmailViewModel.codigo

    val azulTexto = colorResource(R.color.azul_texto)
    val azulBotao = colorResource(R.color.azul_botao)
    val azulBorda = colorResource(R.color.azul_borda)

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
            Spacer(modifier = Modifier.height(95.dp))

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
                                color = if (etapa <= 2) {
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
                            color = if (etapa <= 2) {
                                colorResource(R.color.branco)
                            } else {
                                azulTexto
                            },
                            fontSize = 14.sp
                        )
                    }

                    if (etapa < 3) {
                        HorizontalDivider(
                            modifier = Modifier.width(75.dp),
                            color = azulBorda
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(65.dp))

            Text(
                text = "Confirme seu e-mail",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = azulTexto
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Enviamos um código de 6 dígitos para o e-mail \"${ocultarEmail(email)}\"",
                fontSize = 17.sp,
                lineHeight = 26.sp,
                color = azulTexto
            )

            Spacer(modifier = Modifier.height(38.dp))

            BasicTextField(
                value = codigo,
                onValueChange = { valor ->
                    confirmarEmailViewModel.atualizarCodigo(valor)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),
                cursorBrush = SolidColor(azulBotao),
                decorationBox = { innerTextField ->
                    Box {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            repeat(6) { index ->
                                val selecionado = index == codigo.length &&
                                        codigo.length < 6

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(62.dp)
                                        .background(
                                            color = colorResource(R.color.azul_claro)
                                                .copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        shape = RoundedCornerShape(10.dp),
                                        color = androidx.compose.ui.graphics.Color.Transparent,
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (selecionado) 2.dp else 1.dp,
                                            color = if (selecionado) azulBotao else azulBorda
                                        )
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = codigo.getOrNull(index)?.toString() ?: "",
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = azulTexto,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier.size(1.dp)
                        ) {
                            innerTextField()
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(34.dp))

            Text(
                text = "Reenviar código",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onReenviarCodigo() }
                    .padding(8.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(R.color.azul_entrar)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (confirmarEmailViewModel.validarCodigo()) {
                        onVerificar(codigo)
                    }
                },
                enabled = codigo.length == 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = azulBotao
                )
            ) {
                Text(
                    text = "Verificar",
                    fontSize = 19.sp,
                    color = colorResource(R.color.branco)
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

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun ocultarEmail(email: String): String {
    val partes = email.split("@")

    if (partes.size != 2) return email

    val usuario = partes[0]
    val dominio = partes[1]

    val visivel = usuario.take(2)

    return "$visivel${"*".repeat((usuario.length - 2).coerceAtLeast(1))}@$dominio"
}

package com.example.v2_medtime.screens.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.v2_medtime.R
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.viewmodel.ConfirmarEmailViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun ConfirmarEmailScreen(
    email: String,
    onVerificar: (String) -> Unit,
    onReenviarCodigo: () -> Unit,
    onVoltar: () -> Unit,
    titulo: String = "Confirme seu e-mail",
    mostrarIndicador: Boolean = true,
    confirmarEmailViewModel: ConfirmarEmailViewModel = viewModel()
) {
    val codigo = confirmarEmailViewModel.codigo

    val azulTexto = colorResource(R.color.azul_texto)
    val azulBotao = colorResource(R.color.azul_botao)
    val azulBorda = colorResource(R.color.azul_borda)

    val onVerificarAtual by rememberUpdatedState(onVerificar)

    LaunchedEffect(codigo) {
        if (codigo.length == 6) {
            if (confirmarEmailViewModel.podeVerificarCodigo()) {
                onVerificarAtual(codigo)
            }
        }
    }

    val codigoIncorreto = confirmarEmailViewModel.mensagemErro != null

    val corBordaCodigo = if (codigoIncorreto)
        Color.Red
    else
        colorResource(R.color.azul_borda)

    val corTextoCodigo = if (codigoIncorreto)
        Color.Red
    else
        colorResource(R.color.azul_texto)

    val deslocamento = remember {
        Animatable(0f)
    }

    val density = LocalDensity.current

    LaunchedEffect(codigoIncorreto) {
        if (codigoIncorreto) {
            deslocamento.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 350

                    0f at 0
                    (-10f) at 50
                    10f at 100
                    (-8f) at 150
                    8f at 200
                    (-4f) at 250
                    4f at 300
                    0f at 350
                }
            )
        } else {
            deslocamento.snapTo(0f)
        }
    }

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
            Spacer(modifier = Modifier.height(60.dp))

            if (mostrarIndicador) {
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
                                modifier = Modifier.width(90.dp),
                                color = azulBorda
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(65.dp))

            Text(
                text = titulo,
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
                onValueChange = { novoCodigo ->
                    confirmarEmailViewModel.atualizarCodigo(novoCodigo)
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset {
                                    IntOffset(
                                        x = with(density) {
                                            deslocamento.value.dp.toPx()
                                        }.roundToInt(),
                                        y = 0
                                    )
                                },
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
                                            color = if (codigoIncorreto) {
                                                corBordaCodigo
                                            } else if (selecionado) {
                                                azulBotao
                                            } else {
                                                azulBorda
                                            }
                                        )
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = codigo.getOrNull(index)?.toString() ?: "",
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = corTextoCodigo,
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

            if (codigoIncorreto) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = confirmarEmailViewModel.mensagemErro ?: "",
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            if (confirmarEmailViewModel.carregando) {
                Spacer(modifier = Modifier.height(24.dp))

                CircularProgressIndicator(
                    color = colorResource(R.color.azul_botao),
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Verificando código...",
                    color = colorResource(R.color.azul_texto),
                    fontSize = 16.sp
                )
            }

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

            Spacer(modifier = Modifier.height(150.dp))

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

package com.example.v2_medtime.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.screens.components.BackgroundPadrao
import kotlin.math.roundToInt

private val AzulVinculo = Color(0xFF108BF0)
private val TextoVinculo = Color(0xFF141B3E)
private val FundoVinculo = Color(0xFFEAF3FF)

@Composable
fun InserirCodigoVinculoScreen(
    navController: NavController,
    mensagemErro: String? = null,
    onLimparErro: () -> Unit = {},
    onVerificarCodigo: (String) -> Unit = {}
) {
    var codigo by rememberSaveable { mutableStateOf("") }
    var ultimoCodigoVerificado by rememberSaveable { mutableStateOf<String?>(null) }

    val onVerificarAtual by rememberUpdatedState(onVerificarCodigo)
    val codigoIncorreto = mensagemErro != null
    val deslocamento = remember { Animatable(0f) }
    val density = LocalDensity.current

    LaunchedEffect(codigo) {
        if (codigo.length == 6 && codigo != ultimoCodigoVerificado) {
            ultimoCodigoVerificado = codigo
            onVerificarAtual(codigo)
        }
    }

    LaunchedEffect(mensagemErro) {
        if (mensagemErro != null) {
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
            .background(FundoVinculo)
    ) {
        BackgroundPadrao()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "Inserir código de vínculo",
                color = TextoVinculo,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Insira o código gerado pelo cuidador respectivo",
                color = Color(0xFF273344),
                fontSize = 18.sp,
                lineHeight = 27.sp
            )

            Spacer(modifier = Modifier.height(60.dp))

            BasicTextField(
                value = codigo,
                onValueChange = { novoCodigo ->
                    val formatado = novoCodigo.filter { it in '0'..'9' }.take(6)
                    if (formatado != codigo) {
                        codigo = formatado
                        ultimoCodigoVerificado = null
                        if (codigoIncorreto) onLimparErro()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Código de vínculo de seis dígitos" },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),
                singleLine = true,
                textStyle = TextStyle(color = Color.Transparent),
                cursorBrush = SolidColor(AzulVinculo),
                decorationBox = { campoInterno ->
                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset {
                                    IntOffset(
                                        x = with(density) { deslocamento.value.dp.toPx() }.roundToInt(),
                                        y = 0
                                    )
                                }
                                .clearAndSetSemantics {},
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(6) { indice ->
                                val proximoDigito = indice == codigo.length && codigo.length < 6

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(62.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (codigoIncorreto) Color.Red.copy(alpha = 0.05f) else Color.Transparent,
                                    border = BorderStroke(
                                        width = if (proximoDigito || codigoIncorreto) 2.dp else 1.dp,
                                        color = when {
                                            codigoIncorreto -> Color.Red
                                            proximoDigito -> Color(0xFF438FB7)
                                            else -> Color(0xFFC5D5E7)
                                        }
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = codigo.getOrNull(indice)?.toString().orEmpty(),
                                            color = if (codigoIncorreto) Color.Red else TextoVinculo,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier.size(1.dp)
                        ) {
                            campoInterno()
                        }
                    }
                }
            )

            if (codigoIncorreto) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = mensagemErro.orEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(if (codigoIncorreto) 88.dp else 120.dp))

            OutlinedButton(
                onClick = {
                    onLimparErro()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(
                    1.dp,
                    Color(0xFFB9CADD)
                )
            ) {
                Text(
                    text = "Voltar",
                    color = Color(0xFF287EA8),
                    fontSize = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
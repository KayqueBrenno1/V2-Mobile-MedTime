
package com.example.v2_medtime.screens.auth

import android.R.attr.maxHeight
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.v2_medtime.R

private val AzulEscuro = Color(0xFF10265E)
private val AzulPrincipal = Color(0xFF3478F6)
private val CinzaTexto = Color(0xFF65738F)

@Composable
fun BoasVindasScreen(
    modifier: Modifier = Modifier,
    onComecar: () -> Unit = {},
    onGoogle: () -> Unit = {},
    onEntrar: () -> Unit = {}
) {
    val transicao = rememberInfiniteTransition(label = "animacaoCabecalho")

    val movimentoRelogio by transicao.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "movimentoRelogio"
    )

    val movimentoFrasco by transicao.animateFloat(
        initialValue = 0f,
        targetValue = -9f,
        animationSpec = infiniteRepeatable(
            animation = tween(3300, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "movimentoFrasco"
    )

    val rotacaoFrasco by transicao.animateFloat(
        initialValue = -8f,
        targetValue = -3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotacaoFrasco"
    )

    val movimentoCapsulas by transicao.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "movimentoCapsulas"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE6EBFF))
    ) {
        val alturaTela = maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // CABEÇALHO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFEAF1FF),
                                Color(0xFFBCD5FF),
                                Color(0xFF9FC2FF)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 28.dp, top = 22.dp)
                        .zIndex(1f)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = AzulEscuro,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Med")
                            }

                            withStyle(
                                SpanStyle(
                                    color = AzulPrincipal,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Time")
                            }
                        },
                        fontSize = 38.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                }

                // CÁPSULAS
                Image(
                    painter = painterResource(R.drawable.capsulas_medtime),
                    contentDescription = "Cápsulas de medicamento",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .offset(y = (-75f + movimentoCapsulas).dp)
                        .size(130.dp)
                        .rotate(15f),
                    contentScale = ContentScale.Fit
                )

                // RELÓGIO
                Image(
                    painter = painterResource(R.drawable.relogio_medtime),
                    contentDescription = "Relógio MedTime",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(
                            x = (-35).dp,
                            y = (8f + movimentoRelogio).dp
                        )
                        .size(280.dp),
                    contentScale = ContentScale.Fit
                )

                // FRASCO
                Image(
                    painter = painterResource(R.drawable.frasco_medtime),
                    contentDescription = "Frasco de medicamento",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(
                            x = 20.dp,
                            y = (22f + movimentoFrasco).dp
                        )
                        .size(190.dp)
                        .rotate(rotacaoFrasco),
                    contentScale = ContentScale.Fit
                )
            }

            // PAINEL INFERIOR
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-22).dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 42.dp,
                            topEnd = 42.dp
                        )
                    )
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFF9FBFF),
                                Color(0xFFF0F5FF)
                            )
                        )
                    )
                    .padding(
                        start = 28.dp,
                        end = 28.dp,
                        top = 22.dp,
                        bottom = 42.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(54.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFCAD7F1))
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = AzulEscuro)) {
                            append("Bem-vindo ao\nMed")
                        }

                        withStyle(SpanStyle(color = AzulPrincipal)) {
                            append("Time")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 32.sp,
                    lineHeight = 35.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Organize seus medicamentos, receba lembretes e cuide da sua saúde com mais tranquilidade.",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 17.sp,
                    lineHeight = 25.sp,
                    color = CinzaTexto
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onComecar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulPrincipal
                    )
                ) {
                    Text(
                        text = "Começar",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.branco)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFD5DFEF)
                    )

                    Text(
                        text = "Ou continue com",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        fontSize = 15.sp,
                        color = CinzaTexto
                    )

                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFD5DFEF)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .border(
                            1.dp,
                            Color(0xFFD5DFEF),
                            RoundedCornerShape(50)
                        )
                        .clip(RoundedCornerShape(50))
                        .clickable { onGoogle() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.group),
                        contentDescription = "Google",
                        modifier = Modifier.size(25.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Continuar com Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AzulEscuro
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Já possui uma conta?",
                        fontSize = 16.sp,
                        color = CinzaTexto
                    )

                    Spacer(modifier = Modifier.width(7.dp))

                    Text(
                        text = "Entrar",
                        modifier = Modifier.clickable { onEntrar() },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPrincipal
                    )
                }
            }
        }
    }
}
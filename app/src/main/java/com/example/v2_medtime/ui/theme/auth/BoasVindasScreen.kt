package com.example.v2_medtime.ui.theme.auth

import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.v2_medtime.R

private val AzulFundo = Color(0xFFECF4FF)
private val AzulClaro = Color(0xFFF3F8FF)
private val AzulCurva = Color(0xFFDDEBFF)
private val AzulBotao = Color(0xFF0B8DF2)
private val AzulIcone = Color(0xFF269FCB)
private val AzulTexto = Color(0xFF15183F)

@Composable
fun BoasVindasScreen(
    modifier: Modifier,
    onComecar: () -> Unit = {},
    onGoogle: () -> Unit = {},
    onEntrar: () -> Unit = {}
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulFundo)
    ) {
        val altura = maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val curvaSuperior = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, h * 0.08f)
                cubicTo(
                    w * 0.82f, h * 0.23f,
                    w * 0.48f, h * 0.23f,
                    0f, h * 0.40f
                )
                close()
            }
            drawPath(curvaSuperior, AzulClaro)

            val curvaInferior = Path().apply {
                moveTo(0f, h * 0.95f)
                cubicTo(
                    w * 0.30f, h * 0.85f,
                    w * 0.62f, h * 0.91f,
                    w, h * 0.83f
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(curvaInferior, AzulCurva)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(altura * 0.17f))

            Image(
                painter = painterResource(id = R.drawable.logo_medtime),
                contentDescription = "Logo do MedTime",
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .height(210.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(30.dp))
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onComecar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulBotao
                )
            ) {
                Text(
                    text = "Começar",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFD0DBE9)
                )
                Text(
                    text = "ou",
                    modifier = Modifier.padding(horizontal = 15.dp),
                    fontSize = 17.sp,
                    color = AzulTexto
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFD0DBE9)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .border(
                        1.dp,
                        Color(0xFFB8CCDF),
                        RoundedCornerShape(50)
                    )
                    .clickable { onGoogle() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                GoogleIcone()

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Continuar com Google",
                    fontSize = 18.sp,
                    color = AzulTexto
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Já tem uma conta?",
                    fontSize = 17.sp,
                    color = AzulTexto
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Entrar",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2785B0),
                    modifier = Modifier.clickable { onEntrar() }
                )
            }

            Spacer(modifier = Modifier.height(55.dp))
        }
    }
}

@Composable
private fun CapsulaMedTime() {
    Canvas(modifier = Modifier.size(92.dp)) {
        val centro = Offset(size.width / 2, size.height / 2)

        rotate(-45f, pivot = centro) {
            val largura = size.width * 0.47f
            val altura = size.height * 0.92f
            val esquerda = (size.width - largura) / 2
            val topo = (size.height - altura) / 2

            drawRoundRect(
                color = AzulIcone,
                topLeft = Offset(esquerda, topo),
                size = androidx.compose.ui.geometry.Size(largura, altura),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(largura / 2),
                style = Stroke(width = 4.dp.toPx())
            )

            drawLine(
                color = AzulIcone,
                start = Offset(esquerda, size.height / 2),
                end = Offset(esquerda + largura, size.height / 2),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawLine(
                color = AzulIcone,
                start = Offset(size.width / 2, size.height / 2),
                end = Offset(size.width / 2, topo + altura),
                strokeWidth = 3.dp.toPx()
            )
        }
    }
}

@Composable
private fun GoogleIcone() {
    Text(
        text = "G",
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF4285F4)
    )
}

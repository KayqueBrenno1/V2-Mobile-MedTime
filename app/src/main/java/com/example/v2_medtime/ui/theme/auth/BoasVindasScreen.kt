
package com.example.v2_medtime.ui.theme.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.v2_medtime.R

private val azulFundo = Color(0xFFECF4FF)
private val azulClaro = Color(0xFFF3F8FF)
private val azulCurva = Color(0xFFDDEBFF)
private val azulBotao = Color(0xFF0B8DF2)
private val azulTexto = Color(0xFF15183F)

@Composable
fun BoasVindasScreen(
    modifier: Modifier = Modifier,
    onComecar: () -> Unit = {},
    onGoogle: () -> Unit = {},
    onEntrar: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(azulFundo)
    ) {


        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val largura = size.width
            val altura = size.height

            val curvaSuperior = Path().apply {
                moveTo(0f, 0f)
                lineTo(largura, 0f)
                lineTo(largura, altura * 0.08f)

                cubicTo(
                    largura * 0.82f, altura * 0.23f,
                    largura * 0.48f, altura * 0.23f,
                    0f, altura * 0.40f
                )

                close()
            }

            drawPath(
                path = curvaSuperior,
                color = azulClaro
            )

            val curvaInferior = Path().apply {
                moveTo(0f, altura * 0.95f)

                cubicTo(
                    largura * 0.30f, altura * 0.85f,
                    largura * 0.62f, altura * 0.91f,
                    largura, altura * 0.83f
                )

                lineTo(largura, altura)
                lineTo(0f, altura)
                close()
            }

            drawPath(
                path = curvaInferior,
                color = azulCurva
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.weight(0.9f)
            )

            Image(
                painter = painterResource(R.drawable.logo_medtime),
                contentDescription = "Logo do MedTime",
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .height(210.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(
                modifier = Modifier.weight(1.3f)
            )

            Button(
                onClick = onComecar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = azulBotao
                )
            ) {
                Text(
                    text = "Começar",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

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
                    color = azulTexto
                )

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFD0DBE9)
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFB8CCDF),
                        shape = RoundedCornerShape(50)
                    )
                    .clickable { onGoogle() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
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

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Já tem uma conta?",
                    fontSize = 17.sp,
                    color = azulTexto
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Entrar",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2785B0),
                    modifier = Modifier.clickable {
                        onEntrar()
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(55.dp)
            )
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

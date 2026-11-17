
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.v2_medtime.R
import com.example.v2_medtime.screens.components.BackgroundPadrao

@Composable
fun BoasVindasScreen(
    modifier: Modifier = Modifier,
    onComecar: () -> Unit = {},
    onGoogle: () -> Unit = {},
    onEntrar: () -> Unit = {}
) {
    val azulClaro = colorResource(R.color.azul_claro)
    val azulCurva = colorResource(R.color.azul_curva)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.azul_fundo))
    ) {
        BackgroundPadrao()

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
                    containerColor = colorResource(R.color.azul_botao)
                )
            ) {
                Text(
                    text = "Começar",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.branco)
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
                    color = colorResource(R.color.azul_texto)
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
                    color = colorResource(R.color.azul_texto)
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
                    color = colorResource(R.color.azul_texto)
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

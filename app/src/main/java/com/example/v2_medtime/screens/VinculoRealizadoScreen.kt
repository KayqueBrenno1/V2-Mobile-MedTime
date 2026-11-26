package com.example.v2_medtime.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.screens.components.BackgroundPadrao
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val AzulSucessoVinculo = Color(0xFF108BF0)
private val TextoSucessoVinculo = Color(0xFF141B3E)

@Composable
fun VinculoRealizadoScreen(
    navController: NavController
) {
    val escala = remember { Animatable(0.6f) }
    val opacidade = remember { Animatable(0f) }
    val escalaCheck = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            escala.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 550,
                    easing = FastOutSlowInEasing
                )
            )
        }

        launch {
            opacidade.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 450)
            )
        }

        delay(300)

        escalaCheck.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 350,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF3FF))
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
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(142.dp)
                    .scale(escala.value)
                    .alpha(opacidade.value)
                    .background(
                        Color(0xFFD7E9FF),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(94.dp)
                        .background(
                            AzulSucessoVinculo,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Vínculo realizado",
                        tint = Color.White,
                        modifier = Modifier
                            .size(60.dp)
                            .scale(escalaCheck.value)
                    )
                }
            }

            Spacer(modifier = Modifier.height(38.dp))

            Text(
                text = "Vínculo realizado!",
                color = TextoSucessoVinculo,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Seu vínculo foi realizado com sucesso.",
                color = Color(0xFF273344),
                fontSize = 17.sp,
                lineHeight = 25.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1.5f))

            Button(
                onClick = {
                    navController.popBackStack("abas_principais", inclusive = false)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulSucessoVinculo
                )
            ) {
                Text(
                    text = "Voltar ao menu",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(105.dp))
        }
    }
}
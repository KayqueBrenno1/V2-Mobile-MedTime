package com.example.v2_medtime.screens.auth

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AzulEscuro = Color(0xFF10265E)
private val AzulPrincipal = Color(0xFF3478F6)

@Composable
fun SplashScreen() {
    var iniciarAnimacao by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        iniciarAnimacao = true
    }

    val opacidade by animateFloatAsState(
        targetValue = if (iniciarAnimacao) 1f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "opacidadeSplash"
    )

    val escala by animateFloatAsState(
        targetValue = if (iniciarAnimacao) 1f else 0.85f,
        animationSpec = tween(
            durationMillis = 900,
            easing = FastOutSlowInEasing
        ),
        label = "escalaSplash"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6EBFF)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
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
            modifier = Modifier
                .scale(escala)
                .alpha(opacidade),
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Seu cuidado, no tempo certo.",
            modifier = Modifier.alpha(opacidade),
            fontSize = 16.sp,
            color = AzulEscuro
        )

        Spacer(modifier = Modifier.height(42.dp))

        CircularProgressIndicator(
            color = AzulPrincipal,
            strokeWidth = 3.dp
        )
    }
}
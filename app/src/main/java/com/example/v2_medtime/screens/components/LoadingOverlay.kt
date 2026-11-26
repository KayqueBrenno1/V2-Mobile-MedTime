package com.example.v2_medtime.screens.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    mensagem: String = "Carregando..."
) {
    if (!isLoading) return

    val transicao = rememberInfiniteTransition(
        label = "loading"
    )

    val animacoes = (0..4).map { index ->
        transicao.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 600,
                    delayMillis = index * 120
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bolinha_$index"
        )
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x3376A1F0)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    animacoes.forEach { animacao ->
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .graphicsLayer {
                                    scaleX = animacao.value
                                    scaleY = animacao.value
                                    alpha = animacao.value
                                }
                                .background(
                                    color = Color(0xFF4169B1),
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Text(
                    text = mensagem,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF253D73),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
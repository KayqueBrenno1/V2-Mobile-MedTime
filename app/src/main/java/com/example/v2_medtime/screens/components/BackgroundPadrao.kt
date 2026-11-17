package com.example.v2_medtime.screens.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.colorResource
import com.example.v2_medtime.R

@Composable
fun BackgroundPadrao(modifier: Modifier = Modifier) {
    val azulClaro = colorResource(R.color.azul_claro)
    val azulCurva = colorResource(R.color.azul_curva)

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
}
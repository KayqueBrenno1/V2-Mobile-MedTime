package com.example.v2_medtime.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.v2_medtime.R
import com.example.v2_medtime.screens.components.BackgroundPadrao

@Composable
fun SenhaRedefinidaScreen(
    onVoltarLogin: () -> Unit
) {
    val azulTexto = colorResource(R.color.azul_texto)
    val azulBotao = colorResource(R.color.azul_botao)

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.check)
    )

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
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            LottieAnimation(
                composition = composition,
                iterations = 1,
                speed = 2f,
                modifier = Modifier.size(190.dp)
            )

            Spacer(modifier = Modifier.height(35.dp))

            Text(
                text = "Senha redefinida!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = azulTexto,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "Sua senha foi alterada com sucesso.\n" +
                        "Agora você pode acessar sua conta\n" +
                        "com a nova senha.",
                fontSize = 18.sp,
                lineHeight = 27.sp,
                color = azulTexto,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1.4f))

            Button(
                onClick = onVoltarLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = azulBotao
                )
            ) {
                Text(
                    text = "Voltar para o login",
                    fontSize = 18.sp,
                    color = colorResource(R.color.branco)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
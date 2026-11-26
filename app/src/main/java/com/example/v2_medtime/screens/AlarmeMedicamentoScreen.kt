package com.example.v2_medtime.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.v2_medtime.R
import com.example.v2_medtime.notifications.NotificacaoHelper
import com.example.v2_medtime.viewmodel.AlarmeViewModel

private val FundoAlarme = Color(0xFFF7FAFC)
private val AzulAlarme = Color(0xFF1485BD)
private val VerdeAlarme = Color(0xFF009973)

private val Jersey10FontFamily = FontFamily(
    Font(R.font.jersey_10_regular, FontWeight.Normal)
)

@Composable
fun AlarmeMedicamentoScreen(
    navController: NavController,
    viewModel: AlarmeViewModel,
    onDoseConfirmada: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val alarme = viewModel.alarmeAtual

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FundoAlarme)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MEDTIME",
                color = AzulAlarme,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(188.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F4F8)),
                contentAlignment = Alignment.Center
            ) {
                val imagem = alarme?.imagemUrl
                if (!imagem.isNullOrBlank()) {
                    AsyncImage(
                        model = imagem,
                        contentDescription = alarme.nomeMedicamento,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Medication,
                        contentDescription = alarme?.nomeMedicamento,
                        tint = AzulAlarme,
                        modifier = Modifier.size(76.dp)
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = alarme?.horarioFormatado?.ifBlank { "00:00" } ?: "00:00",
                color = VerdeAlarme,
                fontFamily = Jersey10FontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 76.sp,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.width(IntrinsicSize.Max),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = alarme?.nomeMedicamento.orEmpty(),
                    color = AzulAlarme,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .background(AzulAlarme)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = alarme?.dosagemTexto.orEmpty(),
                color = VerdeAlarme,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = {
                viewModel.confirmarTomei { notificacaoId ->
                    if (notificacaoId != 0) {
                        NotificacaoHelper.cancelar(context, notificacaoId)
                    }
                    onDoseConfirmada()
                    if (!navController.popBackStack()) {
                        navController.navigate("abas_principais") {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            },
            enabled = !viewModel.carregando && alarme != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .height(68.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulAlarme,
                contentColor = Color.White,
                disabledContainerColor = AzulAlarme.copy(alpha = 0.7f)
            )
        ) {
            if (viewModel.carregando) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Text(
                    text = "TOMEI",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
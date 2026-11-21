package com.example.v2_medtime.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.screens.components.BackgroundPadrao

private val AzulGerarCodigo = Color(0xFF108BD5)
private val TextoGerarCodigo = Color(0xFF101A2A)
private val CinzaGerarCodigo = Color(0xFF597085)

@Composable
fun GerarCodigoScreen(
    navController: NavController,
    onGerarCodigo: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEAF3FF))
    ) {
        BackgroundPadrao()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Gerar código",
                color = TextoGerarCodigo,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 14.dp)
            )

            Spacer(modifier = Modifier.height(105.dp))

            Box(
                modifier = Modifier
                    .size(124.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(
                        Color(0xFFD8EAFE),
                        RoundedCornerShape(34.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Link,
                    contentDescription = "Código de vínculo",
                    tint = AzulGerarCodigo,
                    modifier = Modifier.size(65.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Gere um código de vínculo",
                modifier = Modifier.fillMaxWidth(),
                color = TextoGerarCodigo,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "O código permite que um paciente se vincule à sua conta e compartilhe seus dados no MedTime.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                color = CinzaGerarCodigo,
                fontSize = 18.sp,
                lineHeight = 25.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(60.dp))

            Button(
                onClick = onGerarCodigo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulGerarCodigo
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.QrCode,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = "Gerar código",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(50.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    Color(0xFFC4DCEE)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF337596)
                )
            ) {
                Text(
                    text = "Cancelar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(35.dp))
        }
    }
}
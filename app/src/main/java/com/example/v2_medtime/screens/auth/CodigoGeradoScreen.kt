package com.example.v2_medtime.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.screens.components.BackgroundPadrao

private val AzulCodigo = Color(0xFF108BD5)
private val TextoCodigo = Color(0xFF101A2A)
private val CinzaCodigo = Color(0xFF597085)

@Composable
fun CodigoGeradoScreen(
    navController: NavController,
    codigo: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
                color = TextoCodigo,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(65.dp))

            Box(
                modifier = Modifier
                    .size(110.dp)
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
                    tint = AzulCodigo,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Código gerado",
                modifier = Modifier.fillMaxWidth(),
                color = TextoCodigo,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Compartilhe este código com o paciente para que ele possa se vincular a você.",
                modifier = Modifier.fillMaxWidth(),
                color = CinzaCodigo,
                fontSize = 16.sp,
                lineHeight = 25.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(38.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Color(0xFFDDEDFD),
                        RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = codigo,
                    color = TextoCodigo,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(42.dp))

            Button(
                onClick = {
                    val clipboard = context.getSystemService(
                        Context.CLIPBOARD_SERVICE
                    ) as ClipboardManager

                    clipboard.setPrimaryClip(
                        ClipData.newPlainText(
                            "Código de vínculo MedTime",
                            codigo
                        )
                    )

                    Toast.makeText(
                        context,
                        "Código copiado!",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                enabled = codigo.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulCodigo
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = "Copiar código",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedButton(
                onClick = {
                    navController.popBackStack(
                        route = "gerenciar_pacientes",
                        inclusive = false
                    )
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
                    text = "Fechar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(35.dp))
        }
    }
}
package com.example.v2_medtime.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.v2_medtime.R
import com.example.v2_medtime.screens.components.BackgroundPadrao

@Composable
fun EscolhaPerfilScreen(
    modifier: Modifier = Modifier,
    onPaciente: () -> Unit = {},
    onCuidador: () -> Unit = {}
) {
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
                .padding(horizontal = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "MedTime",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.azul_texto)
            )

            Spacer(modifier = Modifier.height(45.dp))

            Text(
                text = "Como você vai usar\no MedTime?",
                fontSize = 30.sp,
                lineHeight = 39.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.azul_texto)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Escolha o seu perfil para continuar.",
                fontSize = 17.sp,
                color = colorResource(R.color.azul_texto)
            )

            Spacer(modifier = Modifier.height(42.dp))

            CardPerfil(
                titulo = "Paciente",
                descricao = "Confirme a ingestão dos seus medicamentos.",
                cuidador = false,
                onClick = onPaciente
            )

            Spacer(modifier = Modifier.height(24.dp))

            CardPerfil(
                titulo = "Cuidador",
                descricao = "Gerencie seus pacientes e acompanhe o tratamento.",
                cuidador = true,
                onClick = onCuidador
            )
        }
    }
}

@Composable
private fun CardPerfil(
    titulo: String,
    descricao: String,
    cuidador: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(146.dp)
            .border(
                width = 1.dp,
                color = colorResource(R.color.azul_borda),
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                color = colorResource(R.color.azul_claro),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(74.dp)
                .background(
                    color = colorResource(R.color.azul_curva),
                    shape = RoundedCornerShape(50)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (cuidador) {
                    Icons.Outlined.PeopleOutline
                } else {
                    Icons.Outlined.Person
                },
                contentDescription = null,
                tint = colorResource(R.color.azul_entrar),
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.width(18.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = titulo,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.azul_texto)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = descricao,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                color = colorResource(R.color.azul_texto)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = colorResource(R.color.azul_texto),
            modifier = Modifier.size(28.dp)
        )
    }
}

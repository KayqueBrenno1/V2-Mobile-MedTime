package com.example.v2_medtime.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AgendaBottomBar(
    selectedTab: String = "Medicamentos",
    onTabSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val azulAtivo = Color(0xFF1592D0)
    val fundoIconeAtivo = Color(0xFFE0F1FC)
    val cinzaInativo = Color(0xFF5A657D)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavOption(
                icon = Icons.Outlined.Home,
                label = "Início",
                isSelected = selectedTab == "Início",
                activeColor = azulAtivo,
                activeBg = fundoIconeAtivo,
                inactiveColor = cinzaInativo,
                onClick = { onTabSelected("Início") }
            )
            BottomNavOption(
                icon = Icons.Outlined.Medication,
                label = "Medicamentos",
                isSelected = selectedTab == "Medicamentos",
                activeColor = azulAtivo,
                activeBg = fundoIconeAtivo,
                inactiveColor = cinzaInativo,
                onClick = { onTabSelected("Medicamentos") }
            )
            BottomNavOption(
                icon = Icons.Outlined.Schedule,
                label = "Histórico",
                isSelected = selectedTab == "Histórico",
                activeColor = azulAtivo,
                activeBg = fundoIconeAtivo,
                inactiveColor = cinzaInativo,
                onClick = { onTabSelected("Histórico") }
            )
            BottomNavOption(
                icon = Icons.Outlined.Person,
                label = "Perfil",
                isSelected = selectedTab == "Perfil",
                activeColor = azulAtivo,
                activeBg = fundoIconeAtivo,
                inactiveColor = cinzaInativo,
                onClick = { onTabSelected("Perfil") }
            )
        }
    }
}

@Composable
private fun BottomNavOption(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    activeBg: Color,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isSelected) activeBg else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) activeColor else inactiveColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
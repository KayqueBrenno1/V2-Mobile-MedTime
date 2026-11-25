package com.example.v2_medtime.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.v2_medtime.R

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF15183F),
    onPrimary = Color.White,
    secondary = Color(0xFF1592D0),
    onSecondary = Color.White,
    background = Color(0xFFF3F8FD),
    onBackground = Color(0xFF15183F),
    surface = Color.White,
    onSurface = Color(0xFF15183F)
)

@Composable
fun V2MedTimeTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
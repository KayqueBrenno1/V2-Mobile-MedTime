package com.example.v2_medtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.v2_medtime.data.remote.RetrofitClient
import com.example.v2_medtime.navigation.AppNavigation
import com.example.v2_medtime.ui.theme.V2MedTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        RetrofitClient.inicializar(applicationContext)

        setContent {
            V2MedTimeTheme {
                AppNavigation(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
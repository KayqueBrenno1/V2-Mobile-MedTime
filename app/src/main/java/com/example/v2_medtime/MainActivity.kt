package com.example.v2_medtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.v2_medtime.screens.CadastroSucessoMedicamentoScreen
import com.example.v2_medtime.screens.MedicamentoScreen
import com.example.v2_medtime.ui.theme.V2MedTimeTheme
import com.example.v2_medtime.viewmodel.MedicamentoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            V2MedTimeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val navController = rememberNavController()
                    val medicamentoViewModel: MedicamentoViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "MedicamentoScreen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("MedicamentoScreen") {
                            MedicamentoScreen(
                                navController = navController,
                                viewModel = medicamentoViewModel
                            )
                        }
                        composable("CadastroSucessoMedicamento") {
                            CadastroSucessoMedicamentoScreen(
                                navController = navController,
                                viewModel = medicamentoViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.example.v2_medtime.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.v2_medtime.screens.auth.BoasVindasScreen
import com.example.v2_medtime.screens.auth.EscolhaPerfilScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.v2_medtime.screens.MedicamentoScreen
import com.example.v2_medtime.screens.CadastroSucessoMedicamentoScreen
import com.example.v2_medtime.viewmodel.MedicamentoViewModel

@Composable
fun AppNavigation(
    modifier: Modifier
) {
    val navController = rememberNavController()
    val medicamentoViewModel: MedicamentoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "boas_vindas"
    ) {
        composable("boas_vindas") {
            BoasVindasScreen(
                onComecar = {
                    navController.navigate("escolha_perfil")
                }
            )
        }

        composable("escolha_perfil") {
            EscolhaPerfilScreen(
                onPaciente = {
                    // Vamos adicionar a tela de cadastro do paciente
                },
                onCuidador = {
                    // Vamos adicionar a tela de cadastro do cuidador
                }
            )
        }

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

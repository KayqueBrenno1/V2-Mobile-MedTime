package com.example.v2_medtime.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.v2_medtime.screens.AgendaMedicamentosScreen
import com.example.v2_medtime.screens.CadastroSucessoMedicamentoScreen
import com.example.v2_medtime.screens.MedicamentoScreen
import com.example.v2_medtime.screens.auth.BoasVindasScreen
import com.example.v2_medtime.screens.auth.CadastroRealizadoScreen
import com.example.v2_medtime.screens.auth.ConfirmarEmailScreen
import com.example.v2_medtime.screens.auth.DadosPessoaisScreen
import com.example.v2_medtime.screens.auth.EscolhaPerfilScreen
import com.example.v2_medtime.screens.auth.LoginScreen
import com.example.v2_medtime.screens.auth.RecuperarSenhaScreen
import com.example.v2_medtime.screens.auth.VerificarCodigoSenhaScreen
import com.example.v2_medtime.viewmodel.MedicamentoViewModel

@Composable
fun AppNavigation(
    modifier: Modifier
) {
    val navController = rememberNavController()
    val medicamentoViewModel: MedicamentoViewModel = viewModel()

    var emailCadastro by rememberSaveable {
        mutableStateOf("")
    }

    var emailRecuperacao by rememberSaveable {
        mutableStateOf("")
    }

    NavHost(
        navController = navController,
        startDestination = "agenda_medicamentos"
    ) {
        composable("boas_vindas") {
            BoasVindasScreen(
                onComecar = {
                    navController.navigate("escolha_perfil")
                },
                onEntrar = {
                    navController.navigate("login")
                }
            )
        }

        composable("escolha_perfil") {
            EscolhaPerfilScreen(
                onPaciente = {
                    navController.navigate("dados_pessoais/paciente")
                },
                onCuidador = {
                    navController.navigate("dados_pessoais/cuidador")
                },
                onVoltar = {
                    navController.popBackStack()
                }
            )
        }

        composable("dados_pessoais/{perfil}") { backStackEntry ->
            val perfil = backStackEntry.arguments
                ?.getString("perfil").orEmpty()

            DadosPessoaisScreen(
                onVoltar = {
                    navController.popBackStack()
                },
                onProximo = { dados ->
                    emailCadastro = dados.email

                    navController.navigate("confirmar_email")
                }
            )
        }

        composable("confirmar_email") {
            ConfirmarEmailScreen(
                email = emailCadastro,
                onVerificar = { codigo ->
                    // Futuramente, enviar o código para validação na API.
                },
                onReenviarCodigo = {
                    // Futuramente, solicitar um novo código à API.
                },
                onVoltar = {
                    navController.popBackStack()
                }
            )
        }

        composable("cadastro_realizado") {
            CadastroRealizadoScreen(
                onAcessarConta = {
                    navController.navigate("login") {
                        popUpTo("cadastro_realizado") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onVoltar = {
                    navController.popBackStack()
                },
                onEntrar = { emailCpf, senha ->
                    navController.navigate("agenda_medicamentos") {
                        popUpTo("boas_vindas") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onEsqueceuSenha = {
                    navController.navigate("recuperar_senha")
                },
                onEntrarGoogle = {
                    // Futuramente, iniciar autenticação Google.
                },
                onCadastrar = {
                    navController.navigate("escolha_perfil")
                }
            )
        }

        composable("recuperar_senha") {
            RecuperarSenhaScreen(
                onVoltar = {
                    navController.popBackStack()
                },
                onEnviarCodigo = { email ->
                    emailRecuperacao = email

                    navController.navigate("verificar_codigo_senha")
                }
            )
        }

        composable("verificar_codigo_senha") {
            VerificarCodigoSenhaScreen(
                email = emailRecuperacao,
                onVerificar = { codigo ->
                    // Futuramente, validar o código pela API.
                    // Após a validação, abrir a tela de nova senha.
                },
                onReenviarCodigo = {
                    // Futuramente, solicitar um novo código pela API.
                },
                onVoltar = {
                    navController.popBackStack()
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
        composable("agenda_medicamentos") {
            AgendaMedicamentosScreen(
                navController = navController,
                viewModel = medicamentoViewModel
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

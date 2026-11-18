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
import com.example.v2_medtime.screens.CadastroSucessoMedicamentoScreen
import com.example.v2_medtime.screens.MedicamentoScreen
import com.example.v2_medtime.screens.auth.BoasVindasScreen
import com.example.v2_medtime.screens.auth.CadastroRealizadoScreen
import com.example.v2_medtime.screens.auth.ConfirmarEmailScreen
import com.example.v2_medtime.screens.auth.DadosPessoaisScreen
import com.example.v2_medtime.screens.auth.EscolhaPerfilScreen
import com.example.v2_medtime.screens.auth.LoginScreen
import com.example.v2_medtime.screens.auth.RecuperarSenhaScreen
import com.example.v2_medtime.screens.auth.RedefinirSenhaScreen
import com.example.v2_medtime.screens.auth.VerificarCodigoSenhaScreen
import com.example.v2_medtime.viewmodel.ConfirmarEmailViewModel
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
        startDestination = "redefinir_senha"
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
            val confirmarEmailViewModel: ConfirmarEmailViewModel = viewModel()

            ConfirmarEmailScreen(
                email = emailCadastro,
                confirmarEmailViewModel = confirmarEmailViewModel,
                onVerificar = { codigo ->
                    // Futuramente, validar o código de confirmação pela API.
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
                    // Futuramente, autenticar com a API.
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
            val confirmarEmailViewModel: ConfirmarEmailViewModel = viewModel()

            VerificarCodigoSenhaScreen(
                email = emailRecuperacao,
                confirmarEmailViewModel = confirmarEmailViewModel,
                onVerificar = { codigo ->
                    // Futuramente, validar o código de recuperação pela API.
                    // Somente após a validação, navegar para redefinir_senha.
                },
                onReenviarCodigo = {
                    // Futuramente, solicitar um novo código à API.
                },
                onVoltar = {
                    navController.popBackStack()
                }
            )
        }

        composable("redefinir_senha") {
            RedefinirSenhaScreen(
                onVoltar = {
                    navController.popBackStack()
                },
                onRedefinirSenha = { novaSenha ->
                    // Futuramente, enviar a nova senha para a API.
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

package com.example.v2_medtime.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.v2_medtime.data.remote.GoogleAuthManager
import com.example.v2_medtime.screens.CadastroSucessoMedicamentoScreen
import com.example.v2_medtime.screens.MedicamentoScreen
import com.example.v2_medtime.screens.auth.BoasVindasScreen
import com.example.v2_medtime.screens.auth.CadastroRealizadoScreen
import com.example.v2_medtime.screens.auth.ConfirmarEmailScreen
import com.example.v2_medtime.screens.auth.CriarSenhaScreen
import com.example.v2_medtime.screens.auth.DadosPessoaisScreen
import com.example.v2_medtime.screens.auth.EscolhaPerfilScreen
import com.example.v2_medtime.screens.auth.LoginScreen
import com.example.v2_medtime.screens.auth.RecuperarSenhaScreen
import com.example.v2_medtime.screens.auth.RedefinirSenhaScreen
import com.example.v2_medtime.screens.auth.SenhaRedefinidaScreen
import com.example.v2_medtime.screens.auth.SplashScreen
import com.example.v2_medtime.screens.auth.VerificarCodigoSenhaScreen
import com.example.v2_medtime.screens.components.LoadingOverlay
import com.example.v2_medtime.viewmodel.CadastroViewModel
import com.example.v2_medtime.viewmodel.ConfirmarEmailViewModel
import com.example.v2_medtime.viewmodel.LoadingViewModel
import com.example.v2_medtime.viewmodel.LoginViewModel
import com.example.v2_medtime.viewmodel.MedicamentoViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    modifier: Modifier
) {
    val navController = rememberNavController()
    val medicamentoViewModel: MedicamentoViewModel = viewModel()
    val cadastroViewModel: CadastroViewModel = viewModel()
    val loadingViewModel: LoadingViewModel = viewModel()
    val confirmarEmailViewModel: ConfirmarEmailViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val googleAuthManager = remember(context) {
        GoogleAuthManager(context)
    }

    LaunchedEffect(cadastroViewModel.erroEnvioCodigo) {
        cadastroViewModel.erroEnvioCodigo?.let { mensagem ->
            snackbarHostState.showSnackbar(mensagem)
        }
    }

    LaunchedEffect(cadastroViewModel.enviandoCodigo) {
        if (cadastroViewModel.enviandoCodigo) {
            loadingViewModel.mostrar("Enviando código...")
        } else {
            loadingViewModel.ocultar()
        }
    }

    LaunchedEffect(cadastroViewModel.codigoEnviado) {
        if (cadastroViewModel.codigoEnviado) {
            cadastroViewModel.limparEstadoEnvioCodigo()

            navController.navigate("confirmar_email") {
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(cadastroViewModel.cadastrandoUsuario) {
        if (cadastroViewModel.cadastrandoUsuario) {
            loadingViewModel.mostrar("Criando sua conta...")
        } else {
            loadingViewModel.ocultar()
        }
    }

    LaunchedEffect(cadastroViewModel.erroCadastro) {
        cadastroViewModel.erroCadastro?.let { mensagem ->
            confirmarEmailViewModel.registrarErro(mensagem)
        }
    }

    LaunchedEffect(cadastroViewModel.cadastroConcluido) {
        if (cadastroViewModel.cadastroConcluido) {
            navController.navigate("cadastro_realizado") {
                popUpTo("confirmar_email") {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(loginViewModel.carregando) {
        if (loginViewModel.carregando) {
            loadingViewModel.mostrar("Entrando na sua conta...")
        } else {
            loadingViewModel.ocultar()
        }
    }

    LaunchedEffect(loginViewModel.erroLogin) {
        loginViewModel.erroLogin?.let { mensagem ->
            snackbarHostState.showSnackbar(mensagem)
            loginViewModel.limparErro()
        }
    }

    LaunchedEffect(loginViewModel.loginConcluido) {
        if (loginViewModel.loginConcluido) {
            navController.navigate("MedicamentoScreen") {
                popUpTo("login") {
                    inclusive = true
                }
                launchSingleTop = true
            }

            loginViewModel.limparEstadoLogin()
        }
    }

    LaunchedEffect(loginViewModel.cadastroGoogleNecessario) {
        if (loginViewModel.cadastroGoogleNecessario) {
            navController.navigate("escolha_perfil")
        }
    }

    var emailCadastro by rememberSaveable {
        mutableStateOf("")
    }

    var emailRecuperacao by rememberSaveable {
        mutableStateOf("")
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier
                .fillMaxSize()
                .blur(
                    if (loadingViewModel.isLoading) 12.dp else 0.dp
                )
        ) {
            composable("splash") {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1800)

                    navController.navigate("boas_vindas") {
                        popUpTo("splash") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

                SplashScreen()
            }

            composable("boas_vindas") {
                BoasVindasScreen(
                    onComecar = {
                        navController.navigate("escolha_perfil")
                    },
                    onGoogle = {
                        coroutineScope.launch {
                            try {
                                android.util.Log.d(
                                    "MedTimeGoogle",
                                    "Iniciando seleção da conta Google"
                                )

                                val tokenGoogle = googleAuthManager.autenticarGoogle()

                                android.util.Log.d(
                                    "MedTimeGoogle",
                                    "ID Token recebido com sucesso"
                                )

                                loginViewModel.autenticarGoogle(tokenGoogle)
                            } catch (error: GetCredentialCancellationException) {
                                // O usuário cancelou a seleção da conta Google.

                            } catch (error: Exception) {
                                android.util.Log.e(
                                    "MedTimeGoogle",
                                    "Erro na autenticação Google",
                                    error
                                )

                                snackbarHostState.showSnackbar(
                                    "Não foi possível entrar com Google. Tente novamente."
                                )
                            }
                        }
                    },
                    onEntrar = {
                        navController.navigate("login")
                    }
                )
            }

            composable("escolha_perfil") {
                EscolhaPerfilScreen(
                    onPaciente = {
                        if (loginViewModel.cadastroGoogleNecessario) {
                            navController.navigate("dados_pessoais/google_paciente")
                        } else {
                            navController.navigate("dados_pessoais/paciente")
                        }
                    },
                    onCuidador = {
                        if (loginViewModel.cadastroGoogleNecessario) {
                            navController.navigate("dados_pessoais/google_cuidador")
                        } else {
                            navController.navigate("dados_pessoais/cuidador")
                        }
                    },
                    onVoltar = {
                        navController.popBackStack()
                    }
                )
            }

            composable("dados_pessoais/{perfil}") { backStackEntry ->
                val perfil = backStackEntry.arguments?.getString("perfil") ?: ""
                val cadastroGoogle = perfil.startsWith("google_")

                DadosPessoaisScreen(
                    onVoltar = {
                        navController.popBackStack()
                    },
                    onProximo = { dados ->
                        if (cadastroGoogle) {
                            val idRole = when (perfil) {
                                "google_cuidador" -> 1
                                "google_paciente" -> 2
                                else -> null
                            }

                            if (idRole != null) {
                                loginViewModel.cadastrarUsuarioGoogle(dados, idRole)
                            }
                        } else {
                            cadastroViewModel.definirDadosPessoais(dados)
                            navController.navigate("criar_senha")
                        }
                    },
                    cadastroGoogle = cadastroGoogle,
                    nomeGoogle = if (cadastroGoogle) {
                        loginViewModel.dadosGoogleCadastro
                            ?.get("nome")?.asString.orEmpty()
                    } else "",
                    emailGoogle = if (cadastroGoogle) {
                        loginViewModel.dadosGoogleCadastro
                            ?.get("email")?.asString.orEmpty()
                    } else "",
                )
            }

            composable("criar_senha") {
                CriarSenhaScreen(
                    senhaInicial = cadastroViewModel.obterSenha().orEmpty(),
                    onVoltar = {
                        navController.popBackStack()
                    },
                    onProximo = { senha ->
                        cadastroViewModel.definirSenha(senha)
                        cadastroViewModel.enviarCodigoVerificacao()
                    }
                )
            }

            composable("confirmar_email") {
                ConfirmarEmailScreen(
                    email = cadastroViewModel.dadosPessoais?.email.orEmpty(),
                    confirmarEmailViewModel = confirmarEmailViewModel,
                    onVerificar = { codigo ->
                        cadastroViewModel.cadastrarUsuario(codigo)
                    },
                    onReenviarCodigo = {
                        cadastroViewModel.enviarCodigoVerificacao(
                            navegarAposEnvio = false
                        )
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
                    onEntrar = { email, senha ->
                        loginViewModel.autenticarLogin(email, senha)
                    },
                    onEsqueceuSenha = {
                        navController.navigate("recuperar_senha")
                    },
                    onEntrarGoogle = {
                        coroutineScope.launch {
                            try {
                                android.util.Log.d(
                                    "MedTimeGoogle",
                                    "Iniciando seleção da conta Google"
                                )

                                val tokenGoogle = googleAuthManager.autenticarGoogle()

                                android.util.Log.d(
                                    "MedTimeGoogle",
                                    "ID Token recebido com sucesso"
                                )

                                loginViewModel.autenticarGoogle(tokenGoogle)
                            } catch (error: GetCredentialCancellationException) {
                                // O usuário cancelou a seleção da conta Google.

                            } catch (error: Exception) {
                                android.util.Log.e(
                                    "MedTimeGoogle",
                                    "Erro na autenticação Google",
                                    error
                                )

                                snackbarHostState.showSnackbar(
                                    "Não foi possível entrar com Google. Tente novamente."
                                )
                            }
                        }
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

            composable("senha_redefinida") {
                SenhaRedefinidaScreen(
                    onVoltarLogin = {
                        navController.navigate("login") {
                            popUpTo("login") {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        LoadingOverlay(
            isLoading = loadingViewModel.isLoading,
            mensagem = loadingViewModel.mensagem
        )
    }
}
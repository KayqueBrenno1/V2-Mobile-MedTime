
package com.example.v2_medtime.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
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
import com.example.v2_medtime.data.local.NotificacaoStorage
import com.example.v2_medtime.data.model.AlarmeMedicamento
import com.example.v2_medtime.data.remote.AuthSessionEvents
import com.example.v2_medtime.data.remote.GoogleAuthManager
import com.example.v2_medtime.notifications.AgendadorAlarmesMedicamento
import com.example.v2_medtime.notifications.VerificacaoPeriodicaWorker
import com.example.v2_medtime.screens.AlterarSenhaScreen
import com.example.v2_medtime.screens.CadastroSucessoMedicamentoScreen
import com.example.v2_medtime.screens.CodigoGeradoScreen
import com.example.v2_medtime.screens.DetalhesMedicamentoScreen
import com.example.v2_medtime.screens.EditarPerfilScreen
import com.example.v2_medtime.screens.EscolhaCadastroMedicamentoScreen
import com.example.v2_medtime.screens.EstoqueMedicamentoScreen
import com.example.v2_medtime.screens.GerarCodigoScreen
import com.example.v2_medtime.screens.GerenciarPacientesScreen
import com.example.v2_medtime.screens.InserirCodigoVinculoScreen
import com.example.v2_medtime.screens.MedicamentoScreen
import com.example.v2_medtime.screens.MedicamentosIdentificadosScreen
import com.example.v2_medtime.screens.PerfilScreen
import com.example.v2_medtime.screens.VinculoRealizadoScreen
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
import com.example.v2_medtime.viewmodel.AlarmeViewModel
import com.example.v2_medtime.viewmodel.CadastroViewModel
import com.example.v2_medtime.viewmodel.ConfirmarEmailViewModel
import com.example.v2_medtime.viewmodel.HistoricoViewModel
import com.example.v2_medtime.viewmodel.InicioViewModel
import com.example.v2_medtime.viewmodel.LoadingViewModel
import com.example.v2_medtime.viewmodel.LoginViewModel
import com.example.v2_medtime.viewmodel.MedicamentoViewModel
import com.example.v2_medtime.viewmodel.PerfilViewModel
import com.example.v2_medtime.viewmodel.RecuperacaoSenhaViewModel
import com.example.v2_medtime.viewmodel.VinculoViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    modifier: Modifier,
    alarmeInicial: AlarmeMedicamento? = null,
    onAlarmeConsumido: () -> Unit = {}
) {
    val navController = rememberNavController()
    val alarmeViewModel: AlarmeViewModel = viewModel()

    // VIEWMODELS DE AUTENTICAÇÃO
    val cadastroViewModel: CadastroViewModel = viewModel()
    val loadingViewModel: LoadingViewModel = viewModel()
    val confirmarEmailViewModel: ConfirmarEmailViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()

    // VIEWMODELS DAS TELAS PRINCIPAIS
    val medicamentoViewModel: MedicamentoViewModel = viewModel()
    val historicoViewModel: HistoricoViewModel = viewModel()
    val inicioViewModel: InicioViewModel = viewModel()
    val perfilViewModel: PerfilViewModel = viewModel()
    val vinculoViewModel: VinculoViewModel = viewModel()
    val recuperacaoSenhaViewModel: RecuperacaoSenhaViewModel = viewModel()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val mostrarMensagem: (String) -> Unit = { mensagem ->
        coroutineScope.launch { snackbarHostState.showSnackbar(mensagem) }
    }

    val googleAuthManager = remember(context) {
        GoogleAuthManager(context)
    }

    LaunchedEffect(alarmeViewModel.erroAlarme) {
        alarmeViewModel.erroAlarme?.let { mensagem ->
            snackbarHostState.showSnackbar(mensagem)
            alarmeViewModel.limparErro()
        }
    }

    LaunchedEffect(alarmeInicial) {
        if (alarmeInicial != null) {
            alarmeViewModel.definirAlarme(alarmeInicial)
            onAlarmeConsumido()
            navController.navigate("alarme_medicamento") {
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(Unit) {
        AuthSessionEvents.sessionEnded.collect {
            AgendadorAlarmesMedicamento.cancelarTodosAlarmes(context)
            VerificacaoPeriodicaWorker.cancelar(context)
            NotificacaoStorage(context).limparTudo()
            loginViewModel.limparEstadoSessao()
            loginViewModel.limparErro()
            perfilViewModel.carregarPerfil(com.google.gson.JsonObject())
            vinculoViewModel.limparSessao()
            inicioViewModel.iniciarSessao(0L, "")
            medicamentoViewModel.iniciarSessao(0L)
            historicoViewModel.iniciarSessao(0L)

            if (navController.currentDestination?.route != "boas_vindas") {
                navController.navigate("boas_vindas") {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    LaunchedEffect(perfilViewModel.erroPerfil) {
        perfilViewModel.erroPerfil?.let { mensagem ->
            snackbarHostState.showSnackbar(mensagem)
            perfilViewModel.limparErroPerfil()
        }
    }

    LaunchedEffect(medicamentoViewModel.erroMensagem) {
        medicamentoViewModel.erroMensagem?.let { mensagem ->
            snackbarHostState.showSnackbar(mensagem)
            medicamentoViewModel.limparErro()
        }
    }

    LaunchedEffect(inicioViewModel.erroInicio) {
        inicioViewModel.erroInicio?.let { mensagem ->
            snackbarHostState.showSnackbar(mensagem)
            inicioViewModel.limparErro()
        }
    }

    // AUTENTICAÇÃO GOOGLE
    val autenticarComGoogle: () -> Unit = {
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
    }

    // ESTADOS DO CADASTRO
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
            cadastroViewModel.limparEstadoCadastro()
            cadastroViewModel.limparCadastro()

            navController.navigate("cadastro_realizado") {
                popUpTo("confirmar_email") {
                    inclusive = true
                }

                launchSingleTop = true
            }
        }
    }

    // ESTADOS DO LOGIN
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
            loginViewModel.usuarioAutenticado?.let(perfilViewModel::carregarPerfil)
            vinculoViewModel.limparSessao()
            val idPaciente = if (loginViewModel.cuidadorAutenticado) 0L else perfilViewModel.perfil.id
            medicamentoViewModel.iniciarSessao(idPaciente)
            inicioViewModel.iniciarSessao(idPaciente, perfilViewModel.perfil.nomeCompleto)
            historicoViewModel.iniciarSessao(idPaciente)
            val destino = if (loginViewModel.cuidadorAutenticado) {
                "gerenciar_pacientes"
            } else {
                "abas_principais"
            }

            navController.navigate(destino) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }

                launchSingleTop = true
            }

            if (alarmeViewModel.alarmeAtual != null) {
                navController.navigate("alarme_medicamento") {
                    launchSingleTop = true
                }
            }

            loginViewModel.limparEstadoLogin()

            VerificacaoPeriodicaWorker.agendarPeriodico(context)
            VerificacaoPeriodicaWorker.executarAgora(context)
        }
    }

    LaunchedEffect(loginViewModel.cadastroGoogleNecessario) {
        if (loginViewModel.cadastroGoogleNecessario) {
            navController.navigate("escolha_perfil") {
                launchSingleTop = true
            }
        }
    }

    // DADOS TEMPORÁRIOS
    var emailCadastro by rememberSaveable {
        mutableStateOf("")
    }

    var emailRecuperacao by rememberSaveable {
        mutableStateOf("")
    }

    var codigoRecuperacao by remember { mutableStateOf("") }

    // NAVEGAÇÃO PELA BARRA INFERIOR
    val navegarPelaNavbar: (String) -> Unit = { aba ->
        val pagina = when (aba) {
            "Início" -> 0
            "Medicamentos" -> 1
            "Histórico" -> 2
            "Perfil" -> 3
            else -> null
        }

        if (pagina != null) {
            navController.navigate("abas_principais/$pagina") {
                popUpTo("inicio") {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
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
                ),

            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },

            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },

            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },

            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            composable("splash") {
                LaunchedEffect(Unit) {
                    val sessaoRestaurada = loginViewModel.restaurarSessao()
                    if (!sessaoRestaurada) {
                        navController.navigate("boas_vindas") {
                            popUpTo("splash") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                SplashScreen()
            }

            composable("boas_vindas") {
                BoasVindasScreen(
                    onComecar = {
                        navController.navigate("escolha_perfil")
                    },
                    onGoogle = autenticarComGoogle,
                    onEntrar = {
                        navController.navigate("login")
                    }
                )
            }

            composable("escolha_perfil") {
                EscolhaPerfilScreen(
                    onPaciente = {
                        if (loginViewModel.cadastroGoogleNecessario) {
                            navController.navigate(
                                "dados_pessoais/google_paciente"
                            )
                        } else {
                            navController.navigate(
                                "dados_pessoais/paciente"
                            )
                        }
                    },
                    onCuidador = {
                        if (loginViewModel.cadastroGoogleNecessario) {
                            navController.navigate(
                                "dados_pessoais/google_cuidador"
                            )
                        } else {
                            navController.navigate(
                                "dados_pessoais/cuidador"
                            )
                        }
                    },
                    onVoltar = {
                        navController.popBackStack()
                    }
                )
            }

            composable("dados_pessoais/{perfil}") { backStackEntry ->
                val perfil = backStackEntry.arguments
                    ?.getString("perfil") ?: ""

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
                                loginViewModel.cadastrarUsuarioGoogle(
                                    dados,
                                    idRole
                                )
                            }
                        } else {
                            cadastroViewModel.definirPerfil(perfil)
                            cadastroViewModel.definirDadosPessoais(dados)

                            navController.navigate("criar_senha")
                        }
                    },
                    cadastroGoogle = cadastroGoogle,
                    nomeGoogle = if (cadastroGoogle) {
                        loginViewModel.dadosGoogleCadastro
                            ?.get("nome")?.asString.orEmpty()
                    } else {
                        ""
                    },
                    emailGoogle = if (cadastroGoogle) {
                        loginViewModel.dadosGoogleCadastro
                            ?.get("email")?.asString.orEmpty()
                    } else {
                        ""
                    }
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
                    email = cadastroViewModel.dadosPessoais
                        ?.email.orEmpty(),
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
                        loginViewModel.autenticarLogin(
                            email,
                            senha
                        )
                    },
                    onEsqueceuSenha = {
                        navController.navigate("recuperar_senha")
                    },
                    onEntrarGoogle = autenticarComGoogle,
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
                        recuperacaoSenhaViewModel.enviarCodigo(
                            email = email.trim(),
                            onSucesso = {
                                if (navController.currentDestination?.route == "recuperar_senha") {
                                    emailRecuperacao = email.trim()
                                    codigoRecuperacao = ""
                                    navController.navigate("verificar_codigo_senha")
                                }
                            },
                            onErro = mostrarMensagem
                        )
                    }
                )
            }

            composable("verificar_codigo_senha") {
                VerificarCodigoSenhaScreen(
                    email = emailRecuperacao,
                    confirmarEmailViewModel = confirmarEmailViewModel,
                    onVerificar = { codigo ->
                        codigoRecuperacao = codigo
                        navController.navigate("redefinir_senha") { launchSingleTop = true }
                    },
                    onReenviarCodigo = {
                        recuperacaoSenhaViewModel.enviarCodigo(
                            email = emailRecuperacao,
                            onSucesso = { mostrarMensagem("Código reenviado.") },
                            onErro = mostrarMensagem
                        )
                    },
                    onVoltar = {
                        navController.popBackStack()
                    }
                )
            }

            composable("redefinir_senha") {
                RedefinirSenhaScreen(
                    onVoltar = {
                        confirmarEmailViewModel.permitirNovaTentativa()
                        navController.popBackStack()
                    },
                    onRedefinirSenha = { novaSenha ->
                        recuperacaoSenhaViewModel.redefinirSenha(
                            email = emailRecuperacao,
                            codigo = codigoRecuperacao,
                            novaSenha = novaSenha,
                            onSucesso = {
                                if (navController.currentDestination?.route == "redefinir_senha") {
                                    codigoRecuperacao = ""
                                    emailRecuperacao = ""
                                    navController.navigate("senha_redefinida") {
                                        popUpTo("recuperar_senha") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onErro = { mensagem ->
                                confirmarEmailViewModel.permitirNovaTentativa()
                                mostrarMensagem(mensagem)
                            }
                        )
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

            composable("gerenciar_pacientes") {
                LaunchedEffect(Unit) {
                    vinculoViewModel.carregarPacientes(mostrarMensagem)
                }

                GerenciarPacientesScreen(
                    navController = navController,
                    pacientes = vinculoViewModel.pacientes,
                    onAdicionarPaciente = {
                        navController.navigate("gerar_codigo")
                    },
                )
            }

            composable("perfil_cuidador") {
                PerfilScreen(
                    navController = navController,
                    viewModel = perfilViewModel,
                    onNavigateTab = {},
                    mostrarBarraInferior = false,
                    perfilCuidador = true
                )
            }

            composable("abas_principais") {
                AbasPrincipais(
                    navController = navController,
                    inicioViewModel = inicioViewModel,
                    medicamentoViewModel = medicamentoViewModel,
                    historicoViewModel = historicoViewModel,
                    perfilViewModel = perfilViewModel
                )
            }

            composable("editar_perfil") {
                EditarPerfilScreen(
                    navController = navController,
                    viewModel = perfilViewModel
                )
            }

            composable("inserir_codigo_vinculo") {
                InserirCodigoVinculoScreen(
                    navController = navController,
                    onVerificarCodigo = { codigo ->
                        vinculoViewModel.validarCodigo(
                            codigo = codigo,
                            onSucesso = {
                                if (navController.currentDestination?.route == "inserir_codigo_vinculo") {
                                    navController.navigate("vinculo_realizado") {
                                        popUpTo("inserir_codigo_vinculo") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onErro = mostrarMensagem
                        )
                    }
                )
            }

            composable("vinculo_realizado") {
                VinculoRealizadoScreen(navController = navController)
            }

            composable("alterar_senha") {
                AlterarSenhaScreen(navController = navController)
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

            composable("detalhes_medicamento/{id}") { backStackEntry ->
                val id = backStackEntry.arguments
                    ?.getString("id")
                    ?.toLongOrNull() ?: 0L

                DetalhesMedicamentoScreen(
                    medicamentoId = id,
                    navController = navController,
                    viewModel = medicamentoViewModel
                )
            }

            composable("editar_medicamento/{id}") { backStackEntry ->
                val id = backStackEntry.arguments
                    ?.getString("id")
                    ?.toLongOrNull()

                MedicamentoScreen(
                    navController = navController,
                    viewModel = medicamentoViewModel,
                    medicamentoId = id
                )
            }

            composable("estoque_medicamento/{id}") { backStackEntry ->
                val id = backStackEntry.arguments
                    ?.getString("id")
                    ?.toLongOrNull() ?: 0L

                EstoqueMedicamentoScreen(
                    medicamentoId = id,
                    navController = navController,
                    viewModel = medicamentoViewModel
                )
            }

            composable("escolha_cadastro_medicamento") {
                EscolhaCadastroMedicamentoScreen(
                    navController = navController,
                    viewModel = medicamentoViewModel
                )
            }

            composable("medicamentos_identificados") {
                MedicamentosIdentificadosScreen(
                    navController = navController,
                    viewModel = medicamentoViewModel
                )
            }

            composable("editar_ocr/{ocrId}") { backStackEntry ->
                val ocrId = backStackEntry.arguments
                    ?.getString("ocrId")
                    ?.toLongOrNull()

                MedicamentoScreen(
                    navController = navController,
                    viewModel = medicamentoViewModel,
                    ocrTempId = ocrId
                )
            }

            composable("gerar_codigo") {
                GerarCodigoScreen(
                    navController = navController,
                    onGerarCodigo = {
                        vinculoViewModel.gerarCodigo(
                            onSucesso = { codigo ->
                                if (navController.currentDestination?.route == "gerar_codigo") {
                                    navController.navigate("codigo_gerado/$codigo") {
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onErro = mostrarMensagem
                        )
                    }
                )
            }

            composable("codigo_gerado/{codigo}") { backStackEntry ->

                val codigo = backStackEntry.arguments
                    ?.getString("codigo").orEmpty()

                CodigoGeradoScreen(
                    navController = navController,
                    codigo = codigo
                )
            }

            composable("alarme_medicamento") {
                com.example.v2_medtime.screens.AlarmeMedicamentoScreen(
                    navController = navController,
                    viewModel = alarmeViewModel,
                    onDoseConfirmada = {
                        inicioViewModel.carregarDadosIniciais()
                        VerificacaoPeriodicaWorker.executarAgora(context)
                    }
                )
            }
        }

        // MENSAGENS E CARREGAMENTO
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        LoadingOverlay(
            isLoading = loadingViewModel.isLoading || vinculoViewModel.carregando || recuperacaoSenhaViewModel.carregando,
            mensagem = if (vinculoViewModel.carregando || recuperacaoSenhaViewModel.carregando) "Aguarde..." else loadingViewModel.mensagem
        )
    }
}

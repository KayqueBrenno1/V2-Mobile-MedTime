package com.example.v2_medtime.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation.NavController
import com.example.v2_medtime.screens.AgendaMedicamentosScreen
import com.example.v2_medtime.screens.HistoricoScreen
import com.example.v2_medtime.screens.InicioScreen
import com.example.v2_medtime.screens.PerfilScreen
import com.example.v2_medtime.screens.components.AgendaBottomBar
import com.example.v2_medtime.viewmodel.HistoricoViewModel
import com.example.v2_medtime.viewmodel.InicioViewModel
import com.example.v2_medtime.viewmodel.MedicamentoViewModel
import com.example.v2_medtime.viewmodel.PerfilViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AbasPrincipais(
    navController: NavController,
    inicioViewModel: InicioViewModel,
    medicamentoViewModel: MedicamentoViewModel,
    historicoViewModel: HistoricoViewModel,
    perfilViewModel: PerfilViewModel,
    modifier: Modifier = Modifier
) {
    val abas = listOf(
        "Início",
        "Medicamentos",
        "Histórico",
        "Perfil"
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { abas.size }
    )

    val coroutineScope = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(pagerState.settledPage, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                when (pagerState.settledPage) {
                    0 -> inicioViewModel.carregarDadosIniciais()
                    1 -> medicamentoViewModel.carregarMedicamentos()
                    2 -> historicoViewModel.carregarHistorico()
                }
                delay(60_000)
            }
        }
    }

    val navegarParaAba: (String) -> Unit = { aba ->
        val pagina = abas.indexOf(aba)

        if (pagina >= 0 && pagina != pagerState.targetPage) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(
                    page = pagina,
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF3F8FD),
        bottomBar = {
            AgendaBottomBar(
                selectedTab = abas[pagerState.targetPage],
                onTabSelected = navegarParaAba
            )
        }
    ) { innerPadding ->
        val direcaoLayout = LocalLayoutDirection.current
        val espacamentoInferiorConteudo = innerPadding.calculateBottomPadding()

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(direcaoLayout),
                    end = innerPadding.calculateEndPadding(direcaoLayout)
                )
                .consumeWindowInsets(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)),
            beyondViewportPageCount = 0,
            key = { it }
        ) { pagina ->

            when (pagina) {
                0 -> InicioScreen(
                    navController = navController,
                    viewModel = inicioViewModel,
                    onNavigateTab = navegarParaAba,
                    mostrarBarraInferior = false,
                    espacamentoInferiorConteudo = espacamentoInferiorConteudo
                )

                1 -> AgendaMedicamentosScreen(
                    navController = navController,
                    viewModel = medicamentoViewModel,
                    onNavigateTab = navegarParaAba,
                    mostrarBarraInferior = false,
                    espacamentoInferiorConteudo = espacamentoInferiorConteudo
                )

                2 -> HistoricoScreen(
                    navController = navController,
                    viewModel = historicoViewModel,
                    onNavigateTab = navegarParaAba,
                    mostrarBarraInferior = false,
                    espacamentoInferiorConteudo = espacamentoInferiorConteudo
                )

                3 -> PerfilScreen(
                    navController = navController,
                    viewModel = perfilViewModel,
                    onNavigateTab = navegarParaAba,
                    mostrarBarraInferior = false,
                    espacamentoInferiorConteudo = espacamentoInferiorConteudo
                )
            }
        }
    }
}
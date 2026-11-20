package com.example.v2_medtime.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            beyondViewportPageCount = 0,
            key = { it }
        ) { pagina ->

            when (pagina) {
                0 -> InicioScreen(
                    navController = navController,
                    viewModel = inicioViewModel,
                    onNavigateTab = navegarParaAba,
                    mostrarBarraInferior = false
                )

                1 -> AgendaMedicamentosScreen(
                    navController = navController,
                    viewModel = medicamentoViewModel,
                    onNavigateTab = navegarParaAba,
                    mostrarBarraInferior = false
                )

                2 -> HistoricoScreen(
                    navController = navController,
                    viewModel = historicoViewModel,
                    onNavigateTab = navegarParaAba,
                    mostrarBarraInferior = false
                )

                3 -> PerfilScreen(
                    navController = navController,
                    viewModel = perfilViewModel,
                    onNavigateTab = navegarParaAba,
                    mostrarBarraInferior = false
                )
            }
        }
    }
}
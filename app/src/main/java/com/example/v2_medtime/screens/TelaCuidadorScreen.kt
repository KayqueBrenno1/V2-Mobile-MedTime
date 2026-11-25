package com.example.v2_medtime.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.viewmodel.PerfilViewModel
import com.example.v2_medtime.viewmodel.VinculoViewModel
import kotlinx.coroutines.launch

data class PacienteVinculado(
    val id: Int,
    val nome: String,
    val fotoUrl: String? = null
)

private val AzulPacientes = Color(0xFF108BD5)
private val TextoPacientes = Color(0xFF101A2A)
private val CinzaPacientes = Color(0xFF597085)

@Composable
fun AbasCuidador(
    navController: NavController,
    vinculoViewModel: VinculoViewModel,
    perfilViewModel: PerfilViewModel,
    onAdicionarPaciente: () -> Unit,
    onErro: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 }
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.settledPage) {
        if (pagerState.settledPage == 0) {
            vinculoViewModel.carregarPacientes(
                onErro = onErro,
                silencioso = true
            )
        }
    }

    val irParaPagina: (Int) -> Unit = { pagina ->
        if (pagina != pagerState.targetPage) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(
                    page = pagina,
                    animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFEAF3FF),
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                BarraInferiorPacientes(
                    onPacientes = { irParaPagina(0) },
                    onPerfil = { irParaPagina(1) },
                    perfilSelecionado = pagerState.targetPage == 1
                )
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { it }
        ) { pagina ->
            when (pagina) {
                0 -> GerenciarPacientesScreen(
                    navController = navController,
                    pacientes = vinculoViewModel.pacientes,
                    onAdicionarPaciente = onAdicionarPaciente,
                    mostrarBarraInferior = false,
                    espacamentoInferiorConteudo = innerPadding.calculateBottomPadding()
                )
                1 -> PerfilScreen(
                    navController = navController,
                    viewModel = perfilViewModel,
                    onNavigateTab = {},
                    mostrarBarraInferior = false,
                    espacamentoInferiorConteudo = innerPadding.calculateBottomPadding(),
                    perfilCuidador = true,
                    onVoltarPacientes = { irParaPagina(0) }
                )
            }
        }
    }
}

@Composable
fun GerenciarPacientesScreen(
    navController: NavController,
    pacientes: List<PacienteVinculado> = emptyList(),
    onAdicionarPaciente: () -> Unit = {},
    mostrarBarraInferior: Boolean = true,
    espacamentoInferiorConteudo: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEAF3FF))
    ) {
        BackgroundPadrao()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .then(if (mostrarBarraInferior) Modifier.navigationBarsPadding() else Modifier)
        ) {
            Spacer(modifier = Modifier.height(78.dp))

            Button(
                onClick = onAdicionarPaciente,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(58.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulPacientes
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Adicionar paciente",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(42.dp))

            Text(
                text = "Pacientes vinculados",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = TextoPacientes,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    bottom = espacamentoInferiorConteudo + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (pacientes.isEmpty()) {
                    item {
                        Text(
                            text = "Nenhum paciente vinculado ainda.",
                            color = CinzaPacientes,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(
                                horizontal = 4.dp,
                                vertical = 20.dp
                            )
                        )
                    }
                } else {
                    items(
                        items = pacientes,
                        key = { it.id }
                    ) { paciente ->
                        CardPacienteVinculado(
                            paciente = paciente,
                            onClick = {
                                navController.navigate("visao_paciente/${paciente.id}") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }

            if (mostrarBarraInferior) {
                BarraInferiorPacientes(
                    onPacientes = {},
                    onPerfil = {
                        navController.navigate("perfil_cuidador") { launchSingleTop = true }
                    }
                )
            }
        }
    }
}

@Composable
private fun CardPacienteVinculado(
    paciente: PacienteVinculado,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color(0xFFF8FAFF),
        shape = RoundedCornerShape(26.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 14.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarCircularUsuario(
                nome = paciente.nome,
                fotoModel = paciente.fotoUrl,
                tamanho = 64
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = paciente.nome,
                    color = TextoPacientes,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Paciente",
                    color = CinzaPacientes,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
internal fun BarraInferiorPacientes(
    onPacientes: () -> Unit,
    onPerfil: () -> Unit,
    perfilSelecionado: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 55.dp,
                vertical = 18.dp
            ),
        shape = RoundedCornerShape(40.dp),
        color = Color(0xFFF8FAFF)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemBarraPacientes(
                titulo = "Pacientes",
                selecionado = !perfilSelecionado,
                icone = Icons.Outlined.Group,
                onClick = onPacientes
            )

            ItemBarraPacientes(
                titulo = "Perfil",
                selecionado = perfilSelecionado,
                icone = Icons.Outlined.Person,
                onClick = onPerfil
            )
        }
    }
}

@Composable
private fun ItemBarraPacientes(
    titulo: String,
    selecionado: Boolean,
    icone: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    if (selecionado) Color(0xFFD9EDFC)
                    else Color.Transparent,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icone,
                contentDescription = titulo,
                tint = if (selecionado) AzulPacientes else CinzaPacientes,
                modifier = Modifier.size(27.dp)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = titulo,
            fontSize = 12.sp,
            color = if (selecionado) AzulPacientes else CinzaPacientes
        )
    }
}
package com.example.v2_medtime.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.screens.components.DiaCalendarioChip
import com.example.v2_medtime.screens.components.EstoqueCircularCard
import com.example.v2_medtime.screens.components.ProximoMedicamentoCard
import com.example.v2_medtime.viewmodel.HistoricoViewModel
import com.example.v2_medtime.viewmodel.InicioViewModel
import com.example.v2_medtime.viewmodel.MedicamentoViewModel

private val AzulPrincipalPaciente = Color(0xFF1592D0)
private val TextoEscuroPaciente = Color(0xFF0F172A)
private val TextoSecundarioPaciente = Color(0xFF64748B)

@Composable
fun VisaoPacienteScreen(
    paciente: PacienteVinculado,
    navController: NavController,
    inicioViewModel: InicioViewModel,
    medicamentoViewModel: MedicamentoViewModel,
    historicoViewModel: HistoricoViewModel,
    modifier: Modifier = Modifier
) {
    var abaSelecionada by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(paciente.id) {
        val idPaciente = paciente.id.toLong()
        inicioViewModel.iniciarSessao(idPaciente, paciente.nome)
        medicamentoViewModel.iniciarSessao(idPaciente)
        historicoViewModel.iniciarSessao(idPaciente)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF3F8FD),
        bottomBar = {
            BarraInferiorVisaoPaciente(
                abaSelecionada = abaSelecionada,
                onSelecionarAba = { novaAba ->
                    abaSelecionada = novaAba
                    when (novaAba) {
                        0 -> inicioViewModel.carregarDadosIniciais()
                        1 -> medicamentoViewModel.carregarMedicamentos()
                        2 -> historicoViewModel.carregarHistorico()
                    }
                }
            )
        }
    ) { innerPadding ->
        val espacamentoInferior = innerPadding.calculateBottomPadding()

        when (abaSelecionada) {
            0 -> ConteudoInicioPaciente(
                paciente = paciente,
                navController = navController,
                inicioViewModel = inicioViewModel,
                espacamentoInferiorConteudo = espacamentoInferior
            )

            1 -> AgendaMedicamentosScreen(
                navController = navController,
                viewModel = medicamentoViewModel,
                mostrarBarraInferior = false,
                espacamentoInferiorConteudo = espacamentoInferior
            )

            2 -> HistoricoScreen(
                navController = navController,
                viewModel = historicoViewModel,
                onNavigateTab = {},
                mostrarBarraInferior = false,
                espacamentoInferiorConteudo = espacamentoInferior
            )
        }
    }
}

@Composable
private fun ConteudoInicioPaciente(
    paciente: PacienteVinculado,
    navController: NavController,
    inicioViewModel: InicioViewModel,
    espacamentoInferiorConteudo: Dp
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F8FD))
    ) {
        BackgroundPadrao()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(
                top = 14.dp,
                bottom = espacamentoInferiorConteudo + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { navController.popBackStack() },
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Voltar para pacientes",
                                tint = TextoEscuroPaciente,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE0F1FC)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Group,
                                contentDescription = null,
                                tint = AzulPrincipalPaciente,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Visualizando paciente",
                                color = AzulPrincipalPaciente,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 2. Identificação do Paciente + Atalhos de Localização e Pulseira
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarCircularUsuario(
                        nome = paciente.nome,
                        fotoModel = paciente.fotoUrl,
                        tamanho = 68
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = paciente.nome,
                            color = TextoEscuroPaciente,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Paciente acompanhado",
                            color = TextoSecundarioPaciente,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Próximos remédios",
                    color = TextoEscuroPaciente,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { inicioViewModel.navegarSemana(-1) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Semana anterior",
                                    tint = TextoEscuroPaciente
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = null,
                                    tint = AzulPrincipalPaciente,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = inicioViewModel.tituloDataSelecionada,
                                    color = TextoEscuroPaciente,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = { inicioViewModel.navegarSemana(1) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Próxima semana",
                                    tint = TextoEscuroPaciente
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            inicioViewModel.diasCalendario.forEach { dia ->
                                DiaCalendarioChip(
                                    item = dia,
                                    selecionado = dia.id == inicioViewModel.diaSelecionadoId,
                                    onClick = { inicioViewModel.selecionarDia(dia.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            if (inicioViewModel.proximosMedicamentos.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = "Nenhum medicamento programado para este dia.",
                            color = TextoSecundarioPaciente,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            } else {
                items(
                    items = inicioViewModel.proximosMedicamentos,
                    key = { it.id }
                ) { dose ->
                    ProximoMedicamentoCard(
                        item = dose,
                        expandido = inicioViewModel.cardExpandidoId == dose.id,
                        onToggleExpandir = { inicioViewModel.alternarExpansaoCard(dose.id) },
                        onTomarClick = {
                            inicioViewModel.marcarComoTomado(dose.id) {
                                Toast.makeText(
                                    context,
                                    "${dose.nome} confirmado para ${paciente.nome}!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onAbrirDetalhes = {
                            navController.navigate("detalhes_medicamento/${dose.medicamentoId}")
                        }
                    )
                }
            }

            if (inicioViewModel.resumoEstoque.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        inicioViewModel.resumoEstoque.take(2).forEach { estoque ->
                            EstoqueCircularCard(
                                item = estoque,
                                onClick = {
                                    navController.navigate("estoque_medicamento/${estoque.medicamentoId}")
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AtalhoMonitoramentoCard(
    icon: ImageVector,
    titulo: String,
    subtitulo: String,
    corIcone: Color,
    fundoIcone: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(fundoIcone),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = titulo,
                    tint = corIcone,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = titulo,
                    color = TextoEscuroPaciente,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitulo,
                    color = TextoSecundarioPaciente,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun BarraInferiorVisaoPaciente(
    abaSelecionada: Int,
    onSelecionarAba: (Int) -> Unit
) {
    val itens = listOf(
        Triple("Início", Icons.Outlined.Home, 0),
        Triple("Medicamentos", Icons.Outlined.Medication, 1),
        Triple("Histórico", Icons.Outlined.Schedule, 2)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            itens.forEach { (titulo, icone, indice) ->
                val selecionado = abaSelecionada == indice
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelecionarAba(indice) }
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (selecionado) Color(0xFFE0F1FC) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icone,
                            contentDescription = titulo,
                            tint = if (selecionado) AzulPrincipalPaciente else Color(0xFF5A657D),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = titulo,
                        color = if (selecionado) AzulPrincipalPaciente else Color(0xFF5A657D),
                        fontSize = 11.sp,
                        fontWeight = if (selecionado) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
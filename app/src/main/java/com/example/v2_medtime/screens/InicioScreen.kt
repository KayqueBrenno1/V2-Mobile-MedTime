package com.example.v2_medtime.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.screens.components.AgendaBottomBar
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.screens.components.DiaCalendarioChip
import com.example.v2_medtime.screens.components.EstoqueCircularCard
import com.example.v2_medtime.screens.components.ProximoMedicamentoCard
import com.example.v2_medtime.viewmodel.InicioViewModel

@Composable
fun InicioScreen(
    navController: NavController,
    viewModel: InicioViewModel,
    onNavigateTab: (String) -> Unit,
    modifier: Modifier = Modifier,
    mostrarBarraInferior: Boolean = true
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF3F8FD))
    ) {
        BackgroundPadrao()

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (mostrarBarraInferior) {
                    AgendaBottomBar(
                        selectedTab = "Início",
                        onTabSelected = onNavigateTab
                    )
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(
                    top = 12.dp,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Cabeçalho: MedTime + Olá, Kayque 👋
                item {
                    Column {
                        Spacer(modifier = Modifier.height(35.dp))
                        Text(
                            text = "Olá, ${viewModel.nomeUsuario} \uD83D\uDC4B",
                            color = Color(0xFF0F172A),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Acompanhe sua medicação de forma simples.",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp
                        )
                    }
                }

                // 2. Filtro Horizontal de Dias (Seg 10 ... Sex 14)
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.diasCalendario.forEach { dia ->
                            DiaCalendarioChip(
                                item = dia,
                                selecionado = dia.id == viewModel.diaSelecionadoId,
                                onClick = { viewModel.selecionarDia(dia.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 3. Lista de Próximos Medicamentos do Dia
                items(
                    items = viewModel.proximosMedicamentos,
                    key = { it.id }
                ) { dose ->
                    ProximoMedicamentoCard(
                        item = dose,
                        expandido = viewModel.cardExpandidoId == dose.id,
                        onToggleExpandir = { viewModel.alternarExpansaoCard(dose.id) },
                        onTomarClick = {
                            viewModel.marcarComoTomado(dose.id) {
                                Toast.makeText(
                                    context,
                                    "${dose.nome} marcado como tomado!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onAbrirDetalhes = {
                            navController.navigate("detalhes_medicamento/${dose.medicamentoId}")
                        }
                    )
                }

                // 4. Cards Circulares de Estoque (2 colunas lado a lado)
                item {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        viewModel.resumoEstoque.take(2).forEach { estoque ->
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
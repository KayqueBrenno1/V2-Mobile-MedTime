package com.example.v2_medtime.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.screens.components.AgendaBottomBar
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.screens.components.MedicamentoAgendaCard
import com.example.v2_medtime.viewmodel.MedicamentoViewModel

@Composable
fun AgendaMedicamentosScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MedicamentoViewModel
) {
    var termoBusca by remember { mutableStateOf("") }
    var filtroSelecionado by remember { mutableStateOf("Todos") }

    val filtros = listOf("Todos", "Ativos", "Pausados", "Concluídos")
    val azulPrincipal = Color(0xFF1592D0)
    val textoEscuro = Color(0xFF0F172A)
    val textoSecundario = Color(0xFF64748B)

    // Filtra dinamicamente pela busca e pelo status selecionado
    val listaFiltrada = viewModel.listaMedicamentos.filter { item ->
        val bateNome = item.dados.nome.contains(termoBusca, ignoreCase = true)
        val bateStatus = filtroSelecionado == "Todos" || item.status.label == filtroSelecionado
        bateNome && bateStatus
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF3F8FD))
    ) {
        // Seu componente oficial de fundo ondulado
        BackgroundPadrao()

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                AgendaBottomBar(
                    selectedTab = "Medicamentos",
                    onTabSelected = { aba ->
                        // Pronto para ligar às outras abas futuramente
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Cabeçalho: < Medicamentos (+)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Voltar",
                            tint = textoEscuro,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Medicamentos",
                            color = textoEscuro,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Botão circular azul "+" para adicionar novo medicamento
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(azulPrincipal)
                            .clickable { navController.navigate("MedicamentoScreen") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Novo medicamento",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Barra de Busca (Pill branca)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    OutlinedTextField(
                        value = termoBusca,
                        onValueChange = { termoBusca = it },
                        placeholder = {
                            Text(
                                text = "Buscar medicamento",
                                color = textoSecundario,
                                fontSize = 15.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Buscar",
                                tint = textoSecundario,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        textStyle = TextStyle(fontSize = 15.sp, color = textoEscuro),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chips de Filtro: Todos | Ativos | Pausados | Concluídos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filtros.forEach { filtro ->
                        val selecionado = filtro == filtroSelecionado
                        Surface(
                            modifier = Modifier.clickable { filtroSelecionado = filtro },
                            shape = CircleShape,
                            color = if (selecionado) azulPrincipal else Color.White,
                            shadowElevation = 1.dp
                        ) {
                            Text(
                                text = filtro,
                                color = if (selecionado) Color.White else textoSecundario,
                                fontSize = 13.sp,
                                fontWeight = if (selecionado) FontWeight.SemiBold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Lista de Medicamentos
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(
                        items = listaFiltrada,
                        key = { _, item -> item.id }
                    ) { index, item ->
                        MedicamentoAgendaCard(
                            item = item,
                            colorIndex = index,
                            onClick = {
                                // Pronto para abrir detalhes/edição do medicamento futuramente
                            }
                        )
                    }
                }
            }
        }
    }
}
package com.example.v2_medtime.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.screens.components.AgendaBottomBar
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.screens.components.HistoricoCard
import com.example.v2_medtime.viewmodel.HistoricoViewModel

@Composable
fun HistoricoScreen(
    navController: NavController,
    viewModel: HistoricoViewModel,
    onNavigateTab: (String) -> Unit,
    modifier: Modifier = Modifier,
    mostrarBarraInferior: Boolean = true,
    espacamentoInferiorConteudo: Dp = 0.dp
) {
    var termoBusca by remember { mutableStateOf("") }

    val textoEscuro = Color(0xFF0F172A)
    val textoSecundario = Color(0xFF64748B)

    val listaFiltrada = viewModel.listaHistorico.filter { item ->
        item.nomeMedicamento.contains(termoBusca, ignoreCase = true) ||
                item.status.label.contains(termoBusca, ignoreCase = true) ||
                item.dataLabel.contains(termoBusca, ignoreCase = true)
    }

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
                        selectedTab = "Histórico",
                        onTabSelected = onNavigateTab
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(35.dp))

                Text(
                    text = "Histórico",
                    color = textoEscuro,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(18.dp))

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
                                text = "Buscar no histórico",
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

                Spacer(modifier = Modifier.height(18.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = espacamentoInferiorConteudo + 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = listaFiltrada,
                        key = { it.id }
                    ) { item ->
                        HistoricoCard(
                            item = item,
                            onClick = {
                                navController.navigate("detalhes_medicamento/${item.medicamentoId}")
                            }
                        )
                    }
                }
            }
        }
    }
}
package com.example.v2_medtime.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.screens.components.DetalheInfoCard
import com.example.v2_medtime.screens.components.gerarHorariosDoDia
import com.example.v2_medtime.viewmodel.MedicamentoViewModel

@Composable
fun DetalhesMedicamentoScreen(
    medicamentoId: Long,
    navController: NavController,
    viewModel: MedicamentoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val item = viewModel.buscarPorId(medicamentoId)

    if (item == null) {

        LaunchedEffect(Unit) {
            navController.popBackStack("agenda_medicamentos", inclusive = false)
        }
        return
    }

    val dados = item.dados
    var mostrarConfirmacaoExcluir by remember { mutableStateOf(false) }

    val pluralFreq = when (dados.intervaloDosagem.lowercase()) {
        "mês" -> if (dados.frequencia == "1") "mês" else "meses"
        else -> if (dados.frequencia == "1") dados.intervaloDosagem else "${dados.intervaloDosagem}s"
    }
    val textoFrequencia = "A cada ${dados.frequencia} $pluralFreq"
    val textoHorarios = gerarHorariosDoDia(dados)
    val textoEstoque = "${dados.quantidadeTotal} ${dados.tipoMedicamento.lowercase()}(s)"
    val textoObservacao = dados.descricao.ifBlank { "Uso contínuo" }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF3F8FD))
    ) {
        BackgroundPadrao()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Voltar",
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Voltar",
                    color = Color(0xFF0F172A),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFFE5F3FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        val modeloFoto: Any? = item.imagemUri ?: item.imagemUrlRemota
                        if (modeloFoto != null) {
                            AsyncImage(
                                model = modeloFoto,
                                contentDescription = dados.nome,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Medication,
                                contentDescription = null,
                                tint = Color(0xFF1592D0),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "${dados.nome} ${dados.concentracao} ${dados.unidadeMedida}".trim(),
                            color = Color(0xFF0F172A),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dados.tipoMedicamento,
                            color = Color(0xFF64748B),
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DetalheInfoCard(
                    icon = Icons.Outlined.Medication,
                    titulo = "Dosagem",
                    valor = "${dados.dose} ${dados.tipoMedicamento.lowercase()}"
                )

                DetalheInfoCard(
                    icon = Icons.Outlined.CalendarToday,
                    titulo = "Frequência",
                    valor = textoFrequencia
                )

                DetalheInfoCard(
                    icon = Icons.Outlined.Schedule,
                    titulo = "Horários",
                    valor = textoHorarios
                )

                // Abre direto a tela de controle de Estoque
                DetalheInfoCard(
                    icon = Icons.Outlined.Inventory2,
                    titulo = "Estoque",
                    valor = textoEstoque,
                    showChevron = true,
                    onClick = {
                        navController.navigate("estoque_medicamento/$medicamentoId")
                    }
                )

                DetalheInfoCard(
                    icon = Icons.Outlined.Description,
                    titulo = "Observação",
                    valor = textoObservacao
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Botão Editar -> Leva o ID para o formulário preencher e salvar alteração
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("editar_medicamento/$medicamentoId")
                    },
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(22.dp)
                    )
                    Text(
                        text = "Editar",
                        color = Color(0xFF0F172A),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botão Excluir
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarConfirmacaoExcluir = true },
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(22.dp)
                    )
                    Text(
                        text = "Excluir",
                        color = Color(0xFFE53935),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (mostrarConfirmacaoExcluir) {
            AlertDialog(
                onDismissRequest = { mostrarConfirmacaoExcluir = false },
                title = { Text("Excluir medicamento") },
                text = { Text("Tem certeza que deseja remover ${dados.nome} da sua agenda?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mostrarConfirmacaoExcluir = false
                            viewModel.excluirMedicamento(medicamentoId) {
                                Toast.makeText(context, "Medicamento excluído!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Excluir", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarConfirmacaoExcluir = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.viewmodel.MedicamentoViewModel

@Composable
fun EstoqueMedicamentoScreen(
    medicamentoId: Long,
    navController: NavController,
    viewModel: MedicamentoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val item = viewModel.buscarPorId(medicamentoId)

    if (item == null) {
        navController.popBackStack()
        return
    }

    val dados = item.dados
    val quantidadeInicial = dados.quantidadeTotal.toIntOrNull() ?: 0
    val dosePorTomada = dados.dose.toIntOrNull()?.coerceAtLeast(1) ?: 1

    var quantidadeAtual by remember(quantidadeInicial) { mutableIntStateOf(quantidadeInicial) }

    val azulPrincipal = Color(0xFF1592D0)
    val textoEscuro = Color(0xFF0F172A)
    val textoSecundario = Color(0xFF64748B)
    val estoqueBaixo = quantidadeAtual <= 5

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF3F8FD))
    ) {
        BackgroundPadrao()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
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
                        tint = textoEscuro,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Controle de Estoque",
                        color = textoEscuro,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Card Central de Quantidade
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (estoqueBaixo) Color(0xFFFEE2E2) else Color(0xFFE5F3FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Inventory2,
                                contentDescription = null,
                                tint = if (estoqueBaixo) Color(0xFFEF4444) else azulPrincipal,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "${dados.nome} ${dados.concentracao} ${dados.unidadeMedida}".trim(),
                            color = textoEscuro,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (estoqueBaixo) "Estoque baixo! Considere repor." else "Quantidade disponível no frasco/caixa",
                            color = if (estoqueBaixo) Color(0xFFEF4444) else textoSecundario,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // Seletor - [ 20 ] +
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                                    .clickable {
                                        if (quantidadeAtual > 0) quantidadeAtual--
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Diminuir",
                                    tint = textoEscuro,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$quantidadeAtual",
                                    color = textoEscuro,
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "${dados.tipoMedicamento.lowercase()}(s)",
                                    color = textoSecundario,
                                    fontSize = 14.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE5F3FD))
                                    .clickable { quantidadeAtual++ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Aumentar",
                                    tint = azulPrincipal,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Atalho rápido para descontar a dose tomada
                        OutlinedButton(
                            onClick = {
                                quantidadeAtual = (quantidadeAtual - dosePorTomada).coerceAtLeast(0)
                            },
                            shape = CircleShape,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircleOutline,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Registrar dose tomada (-$dosePorTomada)",
                                color = Color(0xFF16A34A),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Botão Salvar Estoque
            Button(
                onClick = {
                    viewModel.atualizarEstoque(medicamentoId, quantidadeAtual) {
                        Toast.makeText(context, "Estoque atualizado!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                enabled = !viewModel.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp)
                    .height(54.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = azulPrincipal)
            ) {
                Text(
                    text = "Salvar Estoque",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
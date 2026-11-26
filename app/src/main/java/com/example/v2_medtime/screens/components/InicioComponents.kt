package com.example.v2_medtime.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.v2_medtime.data.model.DiaCalendarioItem
import com.example.v2_medtime.data.model.EstoqueResumoItem
import com.example.v2_medtime.data.model.ProximoMedicamentoItem
import com.example.v2_medtime.data.model.StatusDoseHoje
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DiaCalendarioChip(
    item: DiaCalendarioItem,
    selecionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fundo = if (selecionado) Color(0xFF0077D7) else Color.White
    val corDiaSemana = if (selecionado) Color.White else Color(0xFF64748B)
    val corNumero = if (selecionado) Color.White else Color(0xFF0F172A)

    Surface(
        modifier = modifier
            .height(74.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = fundo,
        shadowElevation = if (selecionado) 4.dp else 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.diaSemana,
                color = corDiaSemana,
                fontSize = 12.sp,
                fontWeight = if (selecionado) FontWeight.SemiBold else FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.diaNumero,
                color = corNumero,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(if (selecionado || item.isHoje) (if (selecionado) Color.White else Color(0xFF0077D7)) else Color.Transparent)
            )
        }
    }
}

@Composable
fun ProximoMedicamentoCard(
    item: ProximoMedicamentoItem,
    expandido: Boolean,
    onToggleExpandir: () -> Unit,
    onTomarClick: () -> Unit,
    onAbrirDetalhes: () -> Unit,
    modifier: Modifier = Modifier
) {
    var agora by remember(item.id) { mutableStateOf(Instant.now()) }
    val horarioLiberacaoInstante = item.horarioProgramado.minusSeconds(30 * 60)
    LaunchedEffect(item.id, horarioLiberacaoInstante) {
        while (true) {
            val instanteAtual = Instant.now()
            if (!instanteAtual.isBefore(horarioLiberacaoInstante)) {
                agora = instanteAtual
                break
            }
            delay(
                (horarioLiberacaoInstante.toEpochMilli() - instanteAtual.toEpochMilli())
                    .coerceAtLeast(1L)
            )
        }
    }
    val podeTomar = item.podeTomar(agora)
    val horarioLiberacao = item.horarioProgramado
        .minusSeconds(30 * 60)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("HH:mm"))

    val (badgeFundo, badgeTexto) = when (item.status) {
        StatusDoseHoje.ATRASADO -> Color(0xFFE55353) to Color.White
        StatusDoseHoje.PENDENTE -> Color(0xFFFACC15) to Color(0xFF1E293B)
        StatusDoseHoje.TOMADO -> Color(0xFF22C55E) to Color.White
    }

    val isTomado = item.status == StatusDoseHoje.TOMADO

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = if (isTomado) onAbrirDetalhes else onToggleExpandir),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE5F3FD))
                        .clickable(onClick = onAbrirDetalhes),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Medication,
                        contentDescription = item.nome,
                        tint = Color(0xFF1592D0),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.nome,
                        color = Color(0xFF0F172A),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.dosagemTexto,
                        color = Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = item.horario,
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(badgeFundo)
                                .padding(horizontal = 12.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = item.status.label,
                                color = badgeTexto,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (!isTomado) {
                    Icon(
                        imageVector = if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expandido) "Recolher" else "Expandir",
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expandido && !isTomado) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onTomarClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = podeTomar,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22A088),
                            disabledContainerColor = Color(0xFFE2E8F0)
                        )
                    ) {
                        Text(
                            text = "TOMAR",
                            color = if (podeTomar) Color.White else Color(0xFF64748B),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                    if (!podeTomar) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Disponível a partir das $horarioLiberacao",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EstoqueCircularCard(
    item: EstoqueResumoItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progresso = if (item.total > 0) {
        (item.atual.toFloat() / item.total.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val estoqueBaixo = item.atual <= 5
    val corArco = if (estoqueBaixo) Color(0xFFEF4444) else Color(0xFF1E88E5)

    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(104.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(104.dp)) {
                    drawArc(
                        color = Color(0xFFF1F5F9),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = corArco,
                        startAngle = -90f,
                        sweepAngle = 360f * progresso,
                        useCenter = false,
                        style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (item.total > 0) "${item.atual}/${item.total}" else "${item.atual}",
                        color = Color(0xFF0F172A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    if (item.unidade.isNotBlank()) {
                        Text(
                            text = item.unidade,
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Text(
                text = item.nome,
                color = Color(0xFF0F172A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
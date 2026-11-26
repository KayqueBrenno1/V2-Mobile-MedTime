package com.example.v2_medtime.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.v2_medtime.data.model.HistoricoItem
import com.example.v2_medtime.data.model.StatusHistorico

private data class CorHistoricoEstilo(val fundo: Color, val icone: Color)

private val paletaHistorico = listOf(
    CorHistoricoEstilo(fundo = Color(0xFFE5F3FD), icone = Color(0xFF1592D0)),
    CorHistoricoEstilo(fundo = Color(0xFFE3F7F2), icone = Color(0xFF16A37A)),
    CorHistoricoEstilo(fundo = Color(0xFFF1E8FD), icone = Color(0xFF8B46D6))
)

@Composable
fun HistoricoCard(
    item: HistoricoItem,
    modifier: Modifier = Modifier
) {
    val estiloCor = paletaHistorico[item.colorIndex % paletaHistorico.size]
    val isTomado = item.status == StatusHistorico.TOMADO
    val statusCor = if (isTomado) Color(0xFF16A34A) else Color(0xFFEF4444)
    val statusIcone = if (isTomado) Icons.Filled.CheckCircle else Icons.Filled.Cancel

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(estiloCor.fundo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Medication,
                    contentDescription = null,
                    tint = estiloCor.icone,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = item.nomeMedicamento,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = statusIcone,
                        contentDescription = item.status.label,
                        tint = statusCor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.status.label,
                        color = statusCor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.dataLabel.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = item.dataLabel,
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = item.horario,
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
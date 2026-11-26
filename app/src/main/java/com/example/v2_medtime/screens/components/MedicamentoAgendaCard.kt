package com.example.v2_medtime.screens.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.v2_medtime.data.model.MedicamentoRequest
import com.example.v2_medtime.viewmodel.MedicamentoCadastrado

private data class CorIconeEstilo(val fundo: Color, val icone: Color)

private val paletaIcones = listOf(
    CorIconeEstilo(fundo = Color(0xFFE5F3FD), icone = Color(0xFF1592D0)), // Azul
    CorIconeEstilo(fundo = Color(0xFFE3F7F2), icone = Color(0xFF16A37A)), // Verde-água
    CorIconeEstilo(fundo = Color(0xFFF1E8FD), icone = Color(0xFF8B46D6))  // Roxo
)

fun gerarHorariosDoDia(dados: MedicamentoRequest): String {
    val partes = dados.horarioInicial.split(":")
    if (partes.size != 2) return dados.horarioInicial

    val horaBase = partes[0].toIntOrNull() ?: return dados.horarioInicial
    val minBase = partes[1].toIntOrNull() ?: 0
    val freq = dados.frequencia.toIntOrNull() ?: 0

    if (dados.intervaloDosagem.lowercase() == "hora" && freq in 1..12) {
        val tomadas = 24 / freq
        return (0 until tomadas).joinToString(" · ") { i ->
            val horaCalculada = (horaBase + i * freq) % 24
            String.format("%02d:%02d", horaCalculada, minBase)
        }
    }
    return dados.horarioInicial
}

@Composable
fun MedicamentoAgendaCard(
    item: MedicamentoCadastrado,
    colorIndex: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dados = item.dados
    val estiloCor = paletaIcones[colorIndex % paletaIcones.size]

    val titulo = dados.nome
    val subtitulo = "${dados.dose} ${dados.tipoMedicamento.lowercase()}"
    val horariosTexto = gerarHorariosDoDia(dados)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge quadrado arredondado com ícone de cápsula ou foto
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(estiloCor.fundo),
                contentAlignment = Alignment.Center
            ) {
                val modeloFoto = item.imagemUrlRemota
                    ?: dados.imagemRemota.takeIf { it.isNotBlank() }
                    ?: item.imagemUri
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
                        tint = estiloCor.icone,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Textos centrais
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = titulo,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Text(
                    text = subtitulo,
                    color = Color(0xFF64748B),
                    fontSize = 14.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = horariosTexto,
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                }
            }

            // Seta > à direita
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Detalhes",
                tint = Color(0xFF64748B),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
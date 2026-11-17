package com.example.v2_medtime.screens.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.v2_medtime.model.MedicamentoRequest

@Composable
fun ResumoMedicamentoCard(
    medicamento: MedicamentoRequest,
    imagemUri: Uri? = null,
    modifier: Modifier = Modifier
) {
    val textoEscuro = Color(0xFF0F172A)
    val textoSecundario = Color(0xFF5A657D)
    val azulPrincipal = Color(0xFF0077D7)
    val azulIconeFundo = Color(0xFFEAF3FC)
    val bordaSuave = Color(0xFFE6ECF5)

    // Formata "De 8 em 8 horas" dinamicamente conforme o formulário
    val pluralUnidade = when (medicamento.intervaloDosagem.lowercase()) {
        "mês" -> if (medicamento.frequencia == "1") "mês" else "meses"
        else -> if (medicamento.frequencia == "1") medicamento.intervaloDosagem else "${medicamento.intervaloDosagem}s"
    }
    val textoFrequencia = "De ${medicamento.frequencia} em\n${medicamento.frequencia} $pluralUnidade"

    // Formata "50 mg • 1 comprimido"
    val subtitulo = "${medicamento.concentracao} ${medicamento.unidadeMedida}  •  ${medicamento.dose} ${medicamento.tipoMedicamento.lowercase()}"

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, bordaSuave, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            // Cabeçalho do Card: Ícone/Foto + Nome + Concentração/Dosagem
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(azulIconeFundo),
                    contentAlignment = Alignment.Center
                ) {
                    if (imagemUri != null) {
                        AsyncImage(
                            model = imagemUri,
                            contentDescription = medicamento.nome,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Medication,
                            contentDescription = "Medicamento",
                            tint = azulPrincipal,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medicamento.nome,
                        color = textoEscuro,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitulo,
                        color = textoSecundario,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = bordaSuave, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Rodapé do Card: 3 Colunas com divisores verticais
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ResumoInfoColuna(
                    icon = Icons.Outlined.Sync,
                    texto = textoFrequencia,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(bordaSuave)
                )

                ResumoInfoColuna(
                    icon = Icons.Outlined.Schedule,
                    texto = "Primeiro horário\n${medicamento.horarioInicial}",
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(bordaSuave)
                )

                ResumoInfoColuna(
                    icon = Icons.Outlined.Inventory2,
                    texto = "Estoque inicial\n${medicamento.quantidadeTotal} un",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ResumoInfoColuna(
    icon: ImageVector,
    texto: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF0077D7),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = texto,
            color = Color(0xFF5A657D),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}
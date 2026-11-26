package com.example.v2_medtime.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.v2_medtime.data.model.obterPerguntasPendentes
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.viewmodel.ItemOcrIdentificado
import com.example.v2_medtime.viewmodel.MedicamentoViewModel
import java.io.File

@Composable
fun MedicamentosIdentificadosScreen(
    navController: NavController,
    viewModel: MedicamentoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val azulPrincipal = Color(0xFF0077D7)
    val azulIcone = Color(0xFF1592D0)
    val textoEscuro = Color(0xFF0F172A)
    val textoSecundario = Color(0xFF5A657D)

    val totalSelecionados = viewModel.remediosIdentificadosOcr.count { it.selecionado }

    var caminhoReceitaExtra by rememberSaveable { mutableStateOf<String?>(null) }

    val cameraExtraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { sucesso ->
            val arquivo = caminhoReceitaExtra?.let(::File)
            caminhoReceitaExtra = null

            if (sucesso && arquivo != null && arquivo.exists() && arquivo.length() > 0L) {
                viewModel.processarReceitaOcr(
                    imagemReceita = arquivo,
                    limparAnteriores = false
                ) {
                    Toast.makeText(
                        context,
                        "Nova receita analisada e adicionada à lista!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                arquivo?.delete()
            }
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF3F8FD))
    ) {
        BackgroundPadrao()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.height(20.dp))

                // Cabeçalho: < Medicamentos Identificados
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { navController.popBackStack() }
                        .padding(vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Voltar",
                        tint = textoEscuro,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Medicamentos Identificados",
                        color = textoEscuro,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "A IA identificou os seguintes medicamentos na sua receita. Selecione os que deseja cadastrar.",
                    color = textoSecundario,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Lista de Medicamentos Identificados + Banner Informativo
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        items = viewModel.remediosIdentificadosOcr,
                        key = { _, item -> item.idTemporario }
                    ) { index, item ->
                        MedicamentoOcrCard(
                            item = item,
                            colorIndex = index,
                            onToggleSelecionado = {
                                viewModel.alternarSelecaoOcr(item.idTemporario)
                            },
                            onEditarClick = {
                                navController.navigate("editar_ocr/${item.idTemporario}")
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFE7F0FA)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = azulIcone,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Confira as informações. Você pode editar os dados de cada medicamento antes de cadastrar.",
                                    color = Color(0xFF2B4C7E),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Rodapé com os 2 botões de ação
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Botão 1: + Cadastrar selecionados (N)
                Button(
                    onClick = {
                        val selecionados = viewModel.remediosIdentificadosOcr.filter { it.selecionado }
                        val temPendencias = selecionados.any { it.dados.obterPerguntasPendentes().isNotEmpty() }

                        if (temPendencias) {
                            navController.navigate("completar_dados_ocr")
                        } else {
                            viewModel.cadastrarSelecionadosOcr {
                                Toast.makeText(
                                    context,
                                    "$totalSelecionados medicamento(s) cadastrado(s)!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (!navController.popBackStack("abas_principais", inclusive = false)) {
                                    navController.popBackStack("visao_paciente/{pacienteId}", inclusive = false)
                                }
                            }
                        }
                    },
                    // ... mantém o restante igual
                    enabled = totalSelecionados > 0 && !viewModel.isLoading && !viewModel.isEscaneandoOcr,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = azulPrincipal)
                ) {
                    if (viewModel.isEscaneandoOcr) {
                        CircularProgressIndicator(
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.AddCircleOutline,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cadastrar selecionados ($totalSelecionados)",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botão 2: Adicionar outro medicamento (Escanear outra receita)
                OutlinedButton(
                    onClick = {
                        val arquivo = File(
                            context.cacheDir,
                            "receita_extra_${System.currentTimeMillis()}.jpg"
                        )
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            arquivo
                        )
                        caminhoReceitaExtra = arquivo.absolutePath
                        cameraExtraLauncher.launch(uri)
                    },
                    enabled = !viewModel.isEscaneandoOcr && !viewModel.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.2.dp, Color(0xFF93C5FD)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                ) {
                    if (viewModel.isEscaneandoOcr) {
                        CircularProgressIndicator(
                            color = azulIcone,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lendo nova receita...",
                            color = azulIcone,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            tint = azulIcone,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Adicionar outro medicamento",
                            color = Color(0xFF1E4E8C),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicamentoOcrCard(
    item: ItemOcrIdentificado,
    colorIndex: Int,
    onToggleSelecionado: () -> Unit,
    onEditarClick: () -> Unit
) {
    val dados = item.dados
    val coresFundo = listOf(Color(0xFFE5F3FD), Color(0xFFF1E8FD), Color(0xFFE3F7F2))
    val coresIcone = listOf(Color(0xFF1592D0), Color(0xFF8B46D6), Color(0xFF16A37A))

    val fundoBadge = coresFundo[colorIndex % coresFundo.size]
    val corIcone = coresIcone[colorIndex % coresIcone.size]

    val pluralUnidade = when (dados.intervaloDosagem.lowercase()) {
        "mês" -> if (dados.frequencia == "1") "mês" else "meses"
        else -> if (dados.frequencia == "1") dados.intervaloDosagem else "${dados.intervaloDosagem}s"
    }
    val pendencias = dados.obterPerguntasPendentes().size
    val partesSubtitulo = buildList {
        if (dados.concentracao.isNotBlank()) {
            add("${dados.concentracao} ${dados.unidadeMedida}".trim())
        }
        if (dados.dose.isNotBlank()) {
            val tipo = dados.tipoMedicamento.ifBlank { dados.unidadeDose }.lowercase()
            add("${dados.dose} $tipo".trim())
        }
        if (dados.frequencia.isNotBlank() && dados.intervaloDosagem.isNotBlank()) {
            val pluralUnidade = when (dados.intervaloDosagem.lowercase()) {
                "mês" -> if (dados.frequencia == "1") "mês" else "meses"
                else -> if (dados.frequencia == "1") dados.intervaloDosagem else "${dados.intervaloDosagem}s"
            }
            add("de ${dados.frequencia} em ${dados.frequencia} $pluralUnidade")
        }
    }
    val subtitulo = partesSubtitulo.joinToString(" • ").ifBlank { "Toque em continuar para completar os dados" }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleSelecionado),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 10.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.selecionado,
                onCheckedChange = { onToggleSelecionado() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF0077D7),
                    uncheckedColor = Color(0xFF94A3B8)
                )
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(fundoBadge),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Medication,
                    contentDescription = null,
                    tint = corIcone,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dados.nome,
                    color = Color(0xFF0F172A),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitulo,
                    color = Color(0xFF64748B),
                    fontSize = 13.sp,
                    lineHeight = 17.sp
                )
                if (pendencias > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (pendencias == 1) "Falta 1 informação para confirmar"
                        else "Faltam $pendencias informações para confirmar",
                        color = Color(0xFF1592D0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            IconButton(onClick = onEditarClick) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Editar medicamento identificado",
                    tint = Color(0xFF1E64B4),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
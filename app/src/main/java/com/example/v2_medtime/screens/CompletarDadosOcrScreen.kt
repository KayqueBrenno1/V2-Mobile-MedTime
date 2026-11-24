package com.example.v2_medtime.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.data.model.MedicamentoRequest
import com.example.v2_medtime.data.model.PerguntaOcr
import com.example.v2_medtime.data.model.obterPerguntasPendentes
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.viewmodel.MedicamentoViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CompletarDadosOcrScreen(
    navController: NavController,
    viewModel: MedicamentoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val azulPrincipal = Color(0xFF0077D7)
    val azulIcone = Color(0xFF1592D0)
    val textoEscuro = Color(0xFF0F172A)
    val textoSecundario = Color(0xFF5A657D)

    // Captura os IDs dos medicamentos selecionados ao entrar no assistente
    val idsSelecionados = remember {
        viewModel.remediosIdentificadosOcr
            .filter { it.selecionado }
            .map { it.idTemporario }
    }

    var indiceMedicamento by rememberSaveable { mutableIntStateOf(0) }
    var indicePergunta by rememberSaveable { mutableIntStateOf(0) }
    var mostrarErroCampo by remember { mutableStateOf(false) }
    var mostrarSeletorHora by remember { mutableStateOf(false) }

    fun sairDoAssistente() {
        focusManager.clearFocus()
        if (!navController.popBackStack()) {
            navController.navigate("abas_principais") {
                launchSingleTop = true
            }
        }
    }

    val idAtual = idsSelecionados.getOrNull(indiceMedicamento)
    val itemAtual = idAtual?.let { viewModel.buscarItemOcrPorId(it) }

    if (itemAtual == null) {
        LaunchedEffect(Unit) {
            sairDoAssistente()
        }
        return
    }

    // Congela a lista de perguntas pendentes para o medicamento atual
    val perguntasDoMedicamento = remember(idAtual) {
        itemAtual.dados.obterPerguntasPendentes()
    }

    // Se este medicamento já não tem pendências, avança automaticamente
    LaunchedEffect(idAtual, perguntasDoMedicamento) {
        if (perguntasDoMedicamento.isEmpty()) {
            if (indiceMedicamento < idsSelecionados.lastIndex) {
                indiceMedicamento++
                indicePergunta = 0
            } else {
                viewModel.cadastrarSelecionadosOcr {
                    Toast.makeText(
                        context,
                        "${idsSelecionados.size} medicamento(s) cadastrado(s)!",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (!navController.popBackStack("abas_principais", inclusive = false)) {
                        navController.popBackStack("visao_paciente/{pacienteId}", inclusive = false)
                    }
                }
            }
        }
    }

    val perguntaAtual = perguntasDoMedicamento.getOrNull(indicePergunta) ?: return
    val dados = itemAtual.dados

    // Preenche automaticamente unidades padrão quando estiverem vazias para não travar no placeholder
    LaunchedEffect(idAtual, perguntaAtual) {
        when (perguntaAtual) {
            PerguntaOcr.CONCENTRACAO -> {
                if (dados.unidadeMedida.isBlank()) {
                    val unidadePadrao = when (dados.tipoMedicamento.lowercase(Locale.ROOT)) {
                        "gotas", "solução", "xarope", "suspensão" -> "ml"
                        else -> "mg"
                    }
                    viewModel.atualizarItemOcr(
                        itemAtual.idTemporario,
                        dados.copy(unidadeMedida = unidadePadrao)
                    )
                }
            }
            PerguntaOcr.DOSE -> {
                if (dados.unidadeDose.isBlank()) {
                    val unidadeDosePadrao = when (dados.tipoMedicamento.lowercase(Locale.ROOT)) {
                        "gotas" -> "gt"
                        "solução", "xarope", "suspensão" -> "ml"
                        else -> "un"
                    }
                    viewModel.atualizarItemOcr(
                        itemAtual.idTemporario,
                        dados.copy(unidadeDose = unidadeDosePadrao)
                    )
                }
            }
            PerguntaOcr.FREQUENCIA -> {
                if (dados.intervaloDosagem.isBlank()) {
                    viewModel.atualizarItemOcr(
                        itemAtual.idTemporario,
                        dados.copy(intervaloDosagem = "hora")
                    )
                }
            }
            else -> Unit
        }
    }

    val timePickerState = rememberTimePickerState(
        initialHour = dados.horarioInicial.substringBefore(":").toIntOrNull() ?: 8,
        initialMinute = dados.horarioInicial.substringAfter(":", "").toIntOrNull() ?: 0,
        is24Hour = true
    )

    fun atualizarDados(novosDados: MedicamentoRequest) {
        mostrarErroCampo = false
        viewModel.atualizarItemOcr(itemAtual.idTemporario, novosDados)
    }

    fun mensagemErroPerguntaAtual(): String? = when (perguntaAtual) {
        PerguntaOcr.NOME ->
            if (dados.nome.isBlank()) "Informe o nome do medicamento." else null
        PerguntaOcr.CONCENTRACAO ->
            if (dados.concentracao.isBlank() || dados.unidadeMedida.isBlank())
                "Informe a concentração e a unidade."
            else null
        PerguntaOcr.TIPO_MEDICAMENTO ->
            if (dados.tipoMedicamento.isBlank()) "Selecione a forma do medicamento." else null
        PerguntaOcr.DOSE ->
            if (dados.dose.isBlank() || dados.unidadeDose.isBlank())
                "Informe a dose por tomada e a unidade."
            else null
        PerguntaOcr.FREQUENCIA ->
            if (dados.frequencia.isBlank() || dados.intervaloDosagem.isBlank())
                "Informe a frequência e o intervalo."
            else null
        PerguntaOcr.HORARIO_INICIAL ->
            if (dados.horarioInicial.isBlank()) "Escolha o horário da primeira dose." else null
        PerguntaOcr.ESTOQUE_ATUAL ->
            if ((dados.quantidadeTotal.toIntOrNull() ?: 0) <= 0)
                "Informe uma quantidade maior que zero."
            else null
    }

    fun avancar() {
        val erro = mensagemErroPerguntaAtual()
        if (erro != null) {
            mostrarErroCampo = true
            Toast.makeText(context, erro, Toast.LENGTH_SHORT).show()
            return
        }

        focusManager.clearFocus()
        mostrarErroCampo = false

        if (indicePergunta < perguntasDoMedicamento.lastIndex) {
            indicePergunta++
        } else if (indiceMedicamento < idsSelecionados.lastIndex) {
            indiceMedicamento++
            indicePergunta = 0
        } else {
            viewModel.cadastrarSelecionadosOcr {
                Toast.makeText(
                    context,
                    "${idsSelecionados.size} medicamento(s) cadastrado(s)!",
                    Toast.LENGTH_SHORT
                ).show()
                if (!navController.popBackStack("abas_principais", inclusive = false)) {
                    navController.popBackStack("visao_paciente/{pacienteId}", inclusive = false)
                }
            }
        }
    }

    fun voltar() {
        focusManager.clearFocus()
        mostrarErroCampo = false
        if (indicePergunta > 0) {
            indicePergunta--
        } else if (indiceMedicamento > 0) {
            val anteriorId = idsSelecionados[indiceMedicamento - 1]
            val anteriorItem = viewModel.buscarItemOcrPorId(anteriorId)
            val perguntasAnteriores = anteriorItem?.dados?.obterPerguntasPendentes().orEmpty()
            indiceMedicamento--
            indicePergunta = (perguntasAnteriores.lastIndex).coerceAtLeast(0)
        } else {
            sairDoAssistente()
        }
    }

    BackHandler {
        voltar()
    }

    val progresso = (indicePergunta + 1).toFloat() / perguntasDoMedicamento.size.coerceAtLeast(1)
    val ehUltimoPassoGeral = indiceMedicamento == idsSelecionados.lastIndex &&
            indicePergunta == perguntasDoMedicamento.lastIndex

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
                .imePadding()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Cabeçalho com botão voltar e contador de medicamentos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { voltar() }
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Voltar",
                            tint = textoEscuro,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Voltar",
                            color = textoEscuro,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (idsSelecionados.size > 1) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFE1EFFB))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Medicamento ${indiceMedicamento + 1} de ${idsSelecionados.size}",
                                color = azulPrincipal,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card compacto com o resumo do que o OCR já identificou
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFE7F1FC),
                    border = BorderStroke(1.dp, Color(0xFFCDE2F8))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Medication,
                                contentDescription = null,
                                tint = azulIcone,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = dados.nome.ifBlank { "Medicamento da receita" },
                                color = textoEscuro,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val resumoLido = montarResumoParcialOcr(dados)
                            if (resumoLido.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = resumoLido,
                                    color = Color(0xFF2B4C7E),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Indicador de progresso das perguntas deste medicamento
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pergunta ${indicePergunta + 1} de ${perguntasDoMedicamento.size}",
                        color = textoSecundario,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${(progresso * 100).toInt()}%",
                        color = azulPrincipal,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progresso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = azulPrincipal,
                    trackColor = Color(0xFFDCE8F5)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Card animado da pergunta atual (uma por vez)
                AnimatedContent(
                    targetState = Triple(indiceMedicamento, indicePergunta, perguntaAtual),
                    transitionSpec = {
                        (slideInHorizontally { it / 2 } + fadeIn())
                            .togetherWith(slideOutHorizontally { -it / 2 } + fadeOut())
                    },
                    label = "pergunta_ocr"
                ) { (_, _, pergunta) ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = pergunta.titulo,
                                color = textoEscuro,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 26.sp
                            )

                            Text(
                                text = pergunta.subtitulo,
                                color = textoSecundario,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )

                            ConteudoPerguntaOcr(
                                pergunta = pergunta,
                                dados = dados,
                                isError = mostrarErroCampo,
                                onAtualizarDados = ::atualizarDados,
                                onAbrirSeletorHora = { mostrarSeletorHora = true }
                            )

                            val erroAtual = mensagemErroPerguntaAtual()
                            if (mostrarErroCampo && erroAtual != null) {
                                Text(
                                    text = erroAtual,
                                    color = VermelhoObrigatorio,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Botão principal de avanço / conclusão
            Button(
                onClick = { avancar() },
                enabled = !viewModel.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .height(54.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = azulPrincipal)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    if (ehUltimoPassoGeral) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = when {
                            ehUltimoPassoGeral -> "Finalizar e Cadastrar"
                            indicePergunta == perguntasDoMedicamento.lastIndex -> "Próximo Medicamento"
                            else -> "Continuar"
                        },
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (mostrarSeletorHora) {
            AlertDialog(
                onDismissRequest = { mostrarSeletorHora = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val horaFormatada = String.format(
                                Locale.ROOT,
                                "%02d:%02d",
                                timePickerState.hour,
                                timePickerState.minute
                            )
                            atualizarDados(dados.copy(horarioInicial = horaFormatada))
                            mostrarSeletorHora = false
                        }
                    ) { Text("Confirmar") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarSeletorHora = false }) { Text("Cancelar") }
                },
                text = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        TimePicker(state = timePickerState)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConteudoPerguntaOcr(
    pergunta: PerguntaOcr,
    dados: MedicamentoRequest,
    isError: Boolean,
    onAtualizarDados: (MedicamentoRequest) -> Unit,
    onAbrirSeletorHora: () -> Unit
) {
    val unidadesMedida = listOf("mg", "g", "ml", "µg", "gt", "un")
    val tiposMedicamento = listOf(
        "Comprimido", "Cápsula", "Gotas", "Solução",
        "Xarope", "Suspensão", "Injeção", "Pomada", "Creme", "Spray"
    )
    val intervalosOpcoes = listOf("hora", "dia")

    when (pergunta) {
        PerguntaOcr.NOME -> {
            DesignTextField(
                label = "Nome do medicamento",
                required = true,
                placeholder = "Ex.: Amoxicilina",
                value = dados.nome,
                isError = isError && dados.nome.isBlank(),
                onValueChange = { onAtualizarDados(dados.copy(nome = it)) }
            )
        }

        PerguntaOcr.CONCENTRACAO -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    DesignTextField(
                        label = "Concentração",
                        required = true,
                        placeholder = "Ex.: 500",
                        value = dados.concentracao,
                        keyboardType = KeyboardType.Decimal,
                        isError = isError && dados.concentracao.isBlank(),
                        onValueChange = { onAtualizarDados(dados.copy(concentracao = it)) }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    DesignDropdownSelect(
                        label = "Unidade",
                        required = true,
                        placeholder = "mg",
                        options = unidadesMedida,
                        selectedValue = dados.unidadeMedida,
                        isError = isError && dados.unidadeMedida.isBlank(),
                        onValueChanged = { onAtualizarDados(dados.copy(unidadeMedida = it)) }
                    )
                }
            }
        }

        PerguntaOcr.TIPO_MEDICAMENTO -> {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tiposMedicamento.forEach { tipo ->
                    val selecionado = dados.tipoMedicamento.equals(tipo, ignoreCase = true)
                    FilterChip(
                        selected = selecionado,
                        onClick = {
                            val unidadeSugerida = dados.unidadeDose.ifBlank {
                                if (tipo.equals("Gotas", ignoreCase = true)) "gt" else "un"
                            }
                            onAtualizarDados(
                                dados.copy(
                                    tipoMedicamento = tipo,
                                    unidadeDose = unidadeSugerida
                                )
                            )
                        },
                        label = { Text(tipo, fontSize = 14.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0077D7),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF1F5F9),
                            labelColor = Color(0xFF0F172A)
                        )
                    )
                }
            }
        }

        PerguntaOcr.DOSE -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    DesignTextField(
                        label = "Dose por tomada",
                        required = true,
                        placeholder = "Ex.: 1",
                        value = dados.dose,
                        keyboardType = KeyboardType.Decimal,
                        isError = isError && dados.dose.isBlank(),
                        onValueChange = { onAtualizarDados(dados.copy(dose = it)) }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    DesignDropdownSelect(
                        label = "Unidade",
                        required = true,
                        placeholder = "un",
                        options = unidadesMedida,
                        selectedValue = dados.unidadeDose,
                        isError = isError && dados.unidadeDose.isBlank(),
                        onValueChanged = { onAtualizarDados(dados.copy(unidadeDose = it)) }
                    )
                }
            }
        }

        PerguntaOcr.FREQUENCIA -> {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        DesignTextField(
                            label = "A cada",
                            required = true,
                            placeholder = "Ex.: 8",
                            value = dados.frequencia,
                            keyboardType = KeyboardType.Number,
                            isError = isError && dados.frequencia.isBlank(),
                            onValueChange = { novo ->
                                if (novo.all { it.isDigit() }) {
                                    onAtualizarDados(dados.copy(frequencia = novo))
                                }
                            }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        DesignDropdownSelect(
                            label = "Intervalo",
                            required = true,
                            placeholder = "hora / dia",
                            options = intervalosOpcoes,
                            selectedValue = dados.intervaloDosagem,
                            isError = isError && dados.intervaloDosagem.isBlank(),
                            onValueChanged = { onAtualizarDados(dados.copy(intervaloDosagem = it)) }
                        )
                    }
                }

                // Atalhos de posologia comuns
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "6 em 6h" to ("6" to "hora"),
                        "8 em 8h" to ("8" to "hora"),
                        "12 em 12h" to ("12" to "hora"),
                        "1x ao dia" to ("1" to "dia")
                    ).forEach { (rotulo, par) ->
                        val ativo = dados.frequencia == par.first &&
                                dados.intervaloDosagem.equals(par.second, ignoreCase = true)
                        FilterChip(
                            selected = ativo,
                            onClick = {
                                onAtualizarDados(
                                    dados.copy(frequencia = par.first, intervaloDosagem = par.second)
                                )
                            },
                            label = { Text(rotulo, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE1EFFB),
                                selectedLabelColor = Color(0xFF0077D7)
                            )
                        )
                    }
                }
            }
        }

        PerguntaOcr.HORARIO_INICIAL -> {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAbrirSeletorHora() }
                ) {
                    DesignTextField(
                        label = "Horário da primeira dose",
                        required = true,
                        placeholder = "Toque para escolher (ex.: 08:00)",
                        value = dados.horarioInicial,
                        enabled = false,
                        isError = isError && dados.horarioInicial.isBlank(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = "Selecionar horário",
                                tint = Color(0xFF0077D7),
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        onValueChange = {}
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("07:00", "08:00", "12:00", "20:00", "22:00").forEach { hora ->
                        FilterChip(
                            selected = dados.horarioInicial == hora,
                            onClick = { onAtualizarDados(dados.copy(horarioInicial = hora)) },
                            label = { Text(hora, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE1EFFB),
                                selectedLabelColor = Color(0xFF0077D7)
                            )
                        )
                    }
                }
            }
        }

        PerguntaOcr.ESTOQUE_ATUAL -> {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                DesignTextField(
                    label = "Quantidade na caixa / frasco",
                    required = true,
                    placeholder = "Ex.: 30",
                    value = dados.quantidadeTotal,
                    keyboardType = KeyboardType.Number,
                    isError = isError && (dados.quantidadeTotal.toIntOrNull() ?: 0) <= 0,
                    trailingIcon = {
                        Box(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(AzulBadgeFundo)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = dados.unidadeDose.ifBlank { "un" },
                                color = AzulPrincipal,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    onValueChange = { novo ->
                        if (novo.all { it.isDigit() }) {
                            onAtualizarDados(dados.copy(quantidadeTotal = novo))
                        }
                    }
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("10", "14", "20", "30", "60").forEach { qtd ->
                        FilterChip(
                            selected = dados.quantidadeTotal == qtd,
                            onClick = { onAtualizarDados(dados.copy(quantidadeTotal = qtd)) },
                            label = { Text("$qtd ${dados.unidadeDose.ifBlank { "un" }}", fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE1EFFB),
                                selectedLabelColor = Color(0xFF0077D7)
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun montarResumoParcialOcr(dados: MedicamentoRequest): String {
    val partes = mutableListOf<String>()
    if (dados.concentracao.isNotBlank()) {
        partes.add("${dados.concentracao} ${dados.unidadeMedida}".trim())
    }
    if (dados.tipoMedicamento.isNotBlank()) {
        partes.add(dados.tipoMedicamento)
    }
    if (dados.dose.isNotBlank()) {
        partes.add("Dose: ${dados.dose} ${dados.unidadeDose}".trim())
    }
    if (dados.frequencia.isNotBlank() && dados.intervaloDosagem.isNotBlank()) {
        partes.add("A cada ${dados.frequencia} ${dados.intervaloDosagem}(s)")
    }
    return partes.joinToString(" • ")
}
package com.example.v2_medtime.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.v2_medtime.data.model.MedicamentoRequest
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.screens.components.SeletorFoto
import com.example.v2_medtime.viewmodel.MedicamentoViewModel

val FundoTela = Color(0xFFF5F8FC)
val TextoEscuro = Color(0xFF1A2138)
val TextoSecundario = Color(0xFF5A657D)
val PlaceholderCinza = Color(0xFF9AA5B8)
val BordaInput = Color(0xFFDCE3EC)
val AzulPrincipal = Color(0xFF0070D2)
val AzulBadgeFundo = Color(0xFFEAF3FC)
val VermelhoObrigatorio = Color(0xFFE53935)

fun validarCampos(estado: MedicamentoRequest, cadastroNovo: Boolean = true): Set<String> {
    val erros = mutableSetOf<String>()

    if (estado.nome.isBlank()) erros.add("nome")
    if (estado.concentracao.isBlank()) erros.add("concentracao")
    if (estado.unidadeMedida.isBlank()) erros.add("unidadeMedida")
    if (estado.dose.isBlank()) erros.add("dose")
    if (estado.unidadeDose.isBlank()) erros.add("unidadeDose")
    if (cadastroNovo && estado.imagemRemota.isBlank()) erros.add("imagemRemota")
    if (estado.tipoMedicamento.isBlank()) erros.add("tipoMedicamento")
    if (cadastroNovo || estado.frequencia.isNotBlank() ||
        estado.intervaloDosagem.isNotBlank() || estado.horarioInicial.isNotBlank()
    ) {
        if (estado.frequencia.isBlank()) erros.add("frequencia")
        if (estado.intervaloDosagem.isBlank()) erros.add("intervaloDosagem")
        if (estado.horarioInicial.isBlank()) erros.add("horarioInicial")
    }
    if (estado.quantidadeTotal.isBlank()) erros.add("quantidadeTotal")

    return erros
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicamentoScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MedicamentoViewModel,
    medicamentoId: Long? = null,
    ocrTempId: Long? = null
) {

    val context = LocalContext.current

    // Verifica se a tela foi aberta no modo Edição ou Novo Cadastro
    val itemParaEditar = remember(medicamentoId) {
        medicamentoId?.let { viewModel.buscarPorId(it) }
    }
    val itemOcrParaEditar = remember(ocrTempId) {
        ocrTempId?.let { viewModel.buscarItemOcrPorId(it) }
    }
    val isEdicao = itemParaEditar != null || itemOcrParaEditar != null

    var fotoSelecionada by rememberSaveable { mutableStateOf<Uri?>(itemParaEditar?.imagemUri) }
    var mostrarSeletorFoto by remember { mutableStateOf(false) }
    // Seletor de Hora
    var mostrarSeletorHora by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = 8,
        initialMinute = 0,
        is24Hour = true
    )

    // Estado do Formulário (pré-preenchido se for edição)
    var estadoFormulario by remember {
        mutableStateOf(
            itemParaEditar?.dados
                ?: itemOcrParaEditar?.dados
                ?: MedicamentoRequest(
                    unidadeMedida = "mg",
                    tipoMedicamento = "Comprimido"
                )
        )
    }
    var camposComErro by remember { mutableStateOf(setOf<String>()) }

    val unidadesMedida = listOf("mg", "g", "ml", "µg", "gt", "un")
    val tiposMedicamento = listOf(
        "Comprimido", "Cápsula", "Gotas", "Solução",
        "Xarope", "Suspensão", "Injeção", "Pomada", "Creme", "Spray"
    )
    val intervalosOpcoes = listOf("hora", "dia")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FundoTela)
    ) {
        BackgroundPadrao()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Top Bar: < Voltar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Voltar",
                    tint = TextoEscuro,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Voltar",
                    color = TextoEscuro,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isEdicao) "Edite as informações do medicamento" else "Preencha as informações do medicamento",
                color = TextoSecundario,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Card Branco Principal
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val previaFoto = fotoSelecionada ?: itemParaEditar?.imagemUrlRemota
                    Surface(
                        onClick = { mostrarSeletorFoto = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = FundoTela,
                        border = BorderStroke(1.dp, BordaInput)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AzulBadgeFundo),
                                contentAlignment = Alignment.Center
                            ) {
                                if (previaFoto == null) {
                                    Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = AzulPrincipal)
                                } else {
                                    AsyncImage(
                                        model = previaFoto,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "Foto do medicamento",
                                    color = TextoEscuro,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (previaFoto == null) "Toque para tirar foto ou abrir a galeria"
                                        else "Toque para alterar a foto",
                                    color = TextoSecundario,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    SeletorFoto(
                        aberto = mostrarSeletorFoto,
                        onFechar = { mostrarSeletorFoto = false },
                        onFotoSelecionada = { fotoSelecionada = it }
                    )

                    if (itemParaEditar == null || fotoSelecionada != null) {
                        Text(
                            text = "A foto selecionada é uma prévia local. O servidor exige um link para salvar a imagem.",
                            color = TextoSecundario,
                            fontSize = 13.sp
                        )
                    }

                    DesignTextField(
                        label = "Link da imagem",
                        required = itemParaEditar == null,
                        placeholder = "https://...",
                        value = estadoFormulario.imagemRemota,
                        keyboardType = KeyboardType.Uri,
                        isError = camposComErro.contains("imagemRemota"),
                        onValueChange = {
                            estadoFormulario = estadoFormulario.copy(imagemRemota = it)
                            camposComErro = camposComErro - "imagemRemota"
                        }
                    )

                    // 1. Medicamento *
                    DesignTextField(
                        label = "Medicamento",
                        required = true,
                        placeholder = "Ex.: Losartana",
                        value = estadoFormulario.nome,
                        enabled = itemParaEditar == null,
                        isError = camposComErro.contains("nome"),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Buscar",
                                tint = TextoSecundario,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        onValueChange = {
                            estadoFormulario = estadoFormulario.copy(nome = it)
                            camposComErro = camposComErro - "nome"
                        }
                    )

                    // 2. Concentração * | Unidade *
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            DesignTextField(
                                label = "Concentração",
                                required = true,
                                placeholder = "Ex.: 50",
                                value = estadoFormulario.concentracao,
                                keyboardType = KeyboardType.Number,
                                isError = camposComErro.contains("concentracao"),
                                onValueChange = {
                                    estadoFormulario = estadoFormulario.copy(concentracao = it)
                                    camposComErro = camposComErro - "concentracao"
                                }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DesignDropdownSelect(
                                label = "Unidade",
                                required = true,
                                placeholder = "mg",
                                options = unidadesMedida,
                                selectedValue = estadoFormulario.unidadeMedida,
                                isError = camposComErro.contains("unidadeMedida"),
                                onValueChanged = {
                                    estadoFormulario = estadoFormulario.copy(unidadeMedida = it)
                                    camposComErro = camposComErro - "unidadeMedida"
                                }
                            )
                        }
                    }

                    // 3. Dosagem * | Tipo de dosagem *
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            DesignTextField(
                                label = "Dosagem",
                                required = true,
                                placeholder = "Ex.: 1",
                                value = estadoFormulario.dose,
                                keyboardType = KeyboardType.Number,
                                isError = camposComErro.contains("dose"),
                                onValueChange = {
                                    estadoFormulario = estadoFormulario.copy(dose = it)
                                    camposComErro = camposComErro - "dose"
                                }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DesignDropdownSelect(
                                label = "Tipo de dosagem",
                                required = true,
                                placeholder = "Comprimido",
                                options = tiposMedicamento,
                                selectedValue = estadoFormulario.tipoMedicamento,
                                isError = camposComErro.contains("tipoMedicamento"),
                                onValueChanged = {
                                    estadoFormulario = estadoFormulario.copy(tipoMedicamento = it)
                                    camposComErro = camposComErro - "tipoMedicamento"
                                }
                            )
                        }
                    }

                    DesignDropdownSelect(
                        label = "Unidade da dose",
                        required = true,
                        placeholder = "Selecione a unidade",
                        options = unidadesMedida,
                        selectedValue = estadoFormulario.unidadeDose,
                        isError = camposComErro.contains("unidadeDose"),
                        onValueChanged = {
                            estadoFormulario = estadoFormulario.copy(unidadeDose = it)
                            camposComErro = camposComErro - "unidadeDose"
                        }
                    )

                    // 4. Frequência * ("De [ 5 ] em 5 dia(s)")
                    val numeroFreq = estadoFormulario.frequencia.ifEmpty { "---" }
                    val unidadeFreq = if (estadoFormulario.intervaloDosagem.isNotEmpty()) {
                        "${estadoFormulario.intervaloDosagem}(s)"
                    } else {
                        "---"
                    }

                    Column {
                        FieldLabel(text = "Frequência", required = true)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "De",
                                color = TextoEscuro,
                                fontSize = 15.sp
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            OutlinedTextField(
                                value = estadoFormulario.frequencia,
                                onValueChange = { novoValor ->
                                    if (novoValor.all { it.isDigit() }) {
                                        estadoFormulario = estadoFormulario.copy(frequencia = novoValor)
                                        camposComErro = camposComErro - "frequencia"
                                    }
                                },
                                placeholder = {
                                    Text(
                                        text = "5",
                                        color = PlaceholderCinza,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center,
                                    color = TextoEscuro
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                isError = camposComErro.contains("frequencia"),
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(52.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = BordaInput,
                                    focusedBorderColor = AzulPrincipal,
                                    unfocusedContainerColor = Color.White,
                                    focusedContainerColor = Color.White,
                                    unfocusedTextColor = TextoEscuro,
                                    focusedTextColor = TextoEscuro,
                                    errorBorderColor = VermelhoObrigatorio,
                                    errorTextColor = TextoEscuro
                                )
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "em $numeroFreq $unidadeFreq",
                                color = TextoEscuro,
                                fontSize = 15.sp
                            )
                        }
                    }

                    // 4.1 Intervalo de dosagem *
                    DesignDropdownSelect(
                        label = "Intervalo de dosagem",
                        required = true,
                        placeholder = "Selecione (ex: hora, dia)",
                        options = intervalosOpcoes,
                        selectedValue = estadoFormulario.intervaloDosagem,
                        isError = camposComErro.contains("intervaloDosagem"),
                        onValueChanged = {
                            estadoFormulario = estadoFormulario.copy(intervaloDosagem = it)
                            camposComErro = camposComErro - "intervaloDosagem"
                        }
                    )

                    // 5. Primeiro horário * | Estoque inicial *
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { mostrarSeletorHora = true }
                        ) {
                            DesignTextField(
                                label = "Primeiro horário",
                                required = true,
                                placeholder = "Ex.: 08:00",
                                value = estadoFormulario.horarioInicial,
                                enabled = false,
                                isError = camposComErro.contains("horarioInicial"),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.AccessTime,
                                        contentDescription = "Selecionar horário",
                                        tint = if (camposComErro.contains("horarioInicial")) VermelhoObrigatorio else TextoSecundario,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                onValueChange = {}
                            )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            DesignTextField(
                                label = "Estoque inicial",
                                required = true,
                                placeholder = "Ex.: 20",
                                value = estadoFormulario.quantidadeTotal,
                                keyboardType = KeyboardType.Number,
                                isError = camposComErro.contains("quantidadeTotal"),
                                trailingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 6.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AzulBadgeFundo)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "un",
                                            color = AzulPrincipal,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                },
                                onValueChange = {
                                    estadoFormulario = estadoFormulario.copy(quantidadeTotal = it)
                                    camposComErro = camposComErro - "quantidadeTotal"
                                }
                            )
                        }
                    }

                    // 6. Descrição (opcional)
                    DesignTextField(
                        label = "Descrição",
                        optionalHint = "(opcional)",
                        placeholder = "Observações sobre o medicamento...",
                        value = estadoFormulario.descricao,
                        singleLine = false,
                        modifier = Modifier.height(100.dp),
                        onValueChange = {
                            estadoFormulario = estadoFormulario.copy(descricao = it)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Finalizar (Cadastro ou Edição via Repository)
            Button(
                onClick = {
                    val errosEncontrados = validarCampos(estadoFormulario, cadastroNovo = itemParaEditar == null)
                    camposComErro = errosEncontrados

                    if (errosEncontrados.isEmpty()) {
                        if (itemOcrParaEditar != null && ocrTempId != null) {
                            // 1. Edição de item extraído pelo OCR (antes de salvar na agenda)
                            viewModel.atualizarItemOcr(ocrTempId, estadoFormulario)
                            Toast.makeText(context, "Item atualizado na lista!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else if (itemParaEditar != null && medicamentoId != null) {
                            // 2. Edição de medicamento já cadastrado na agenda
                            viewModel.atualizarMedicamento(
                                id = medicamentoId,
                                request = estadoFormulario,
                                imagemUri = fotoSelecionada,
                                onSucesso = {
                                    Toast.makeText(context, "Medicamento atualizado!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            )
                        } else {
                            // 3. Novo cadastro manual
                            viewModel.registrarMedicamento(
                                request = estadoFormulario,
                                imagemUri = fotoSelecionada,
                                onSucesso = {
                                    estadoFormulario = MedicamentoRequest(
                                        unidadeMedida = "mg",
                                        tipoMedicamento = "Comprimido"
                                    )
                                    camposComErro = emptySet()

                                    navController.navigate("CadastroSucessoMedicamento")
                                }
                            )
                        }
                    } else {
                        Toast.makeText(context, "Preencha todos os campos obrigatórios (*).", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !viewModel.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                shape = CircleShape
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = if (isEdicao) "Salvar Alterações" else "Cadastrar Medicamento",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Diálogo TimePicker
        if (mostrarSeletorHora) {
            AlertDialog(
                onDismissRequest = { mostrarSeletorHora = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val horaFormatada = String.format(
                                java.util.Locale.ROOT,
                                "%02d:%02d",
                                timePickerState.hour,
                                timePickerState.minute
                            )
                            estadoFormulario = estadoFormulario.copy(horarioInicial = horaFormatada)
                            camposComErro = camposComErro - "horarioInicial"
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

@Composable
fun FieldLabel(
    text: String,
    required: Boolean = false,
    optionalHint: String? = null
) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = TextoEscuro,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            ) {
                append(text)
            }
            if (required) {
                withStyle(
                    SpanStyle(
                        color = VermelhoObrigatorio,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                ) {
                    append(" *")
                }
            }
            if (optionalHint != null) {
                withStyle(
                    SpanStyle(
                        color = TextoSecundario,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                ) {
                    append(" $optionalHint")
                }
            }
        }
    )
}

@Composable
fun DesignTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    optionalHint: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    isError: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column {
        FieldLabel(text = label, required = required, optionalHint = optionalHint)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = !enabled,
            textStyle = TextStyle(fontSize = 14.sp),
            placeholder = {
                Text(
                    text = placeholder,
                    color = PlaceholderCinza,
                    fontSize = 14.sp
                )
            },
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = trailingIcon,
            isError = isError,
            modifier = modifier
                .fillMaxWidth()
                .then(if (singleLine) Modifier.height(52.dp) else Modifier),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = BordaInput,
                focusedBorderColor = AzulPrincipal,
                disabledBorderColor = if (isError) VermelhoObrigatorio else BordaInput,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                unfocusedTextColor = TextoEscuro,
                focusedTextColor = TextoEscuro,
                disabledTextColor = TextoEscuro,
                disabledPlaceholderColor = PlaceholderCinza,
                errorBorderColor = VermelhoObrigatorio,
                errorTextColor = TextoEscuro
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignDropdownSelect(
    label: String,
    placeholder: String,
    options: List<String>,
    selectedValue: String,
    required: Boolean = false,
    isError: Boolean = false,
    onValueChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayValue = selectedValue.ifEmpty { placeholder }

    Column {
        FieldLabel(text = label, required = required)
        Spacer(modifier = Modifier.height(6.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = displayValue,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(
                        MenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                isError = isError,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = BordaInput,
                    focusedBorderColor = AzulPrincipal,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedTextColor = if (selectedValue.isEmpty()) PlaceholderCinza else TextoEscuro,
                    focusedTextColor = TextoEscuro,
                    errorBorderColor = VermelhoObrigatorio,
                    errorTextColor = if (selectedValue.isEmpty()) PlaceholderCinza else TextoEscuro
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = Color.White
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = TextoEscuro) },
                        onClick = {
                            onValueChanged(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
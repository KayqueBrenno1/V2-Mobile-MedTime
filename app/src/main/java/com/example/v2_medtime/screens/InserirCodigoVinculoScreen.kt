package com.example.v2_medtime.screens

import android.Manifest
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import kotlin.math.roundToInt

private val AzulVinculo = Color(0xFF108BF0)
private val TextoVinculo = Color(0xFF141B3E)
private val FundoVinculo = Color(0xFFEAF3FF)
private val RegexCodigoSeisDigitos = Regex("^\\d{6}$")

@Composable
fun InserirCodigoVinculoScreen(
    navController: NavController,
    carregando: Boolean = false,
    mensagemErro: String? = null,
    onLimparErro: () -> Unit = {},
    onVerificarCodigo: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var codigo by rememberSaveable { mutableStateOf("") }
    var ultimoCodigoVerificado by rememberSaveable { mutableStateOf<String?>(null) }

    var temPermissaoCamera by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcherPermissaoCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedida ->
        temPermissaoCamera = concedida
    }

    val onVerificarAtual by rememberUpdatedState(onVerificarCodigo)
    val codigoIncorreto = mensagemErro != null
    val deslocamento = remember { Animatable(0f) }
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        if (!temPermissaoCamera) {
            launcherPermissaoCamera.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(codigo) {
        if (RegexCodigoSeisDigitos.matches(codigo) && codigo != ultimoCodigoVerificado) {
            ultimoCodigoVerificado = codigo
            onVerificarAtual(codigo)
        }
    }

    LaunchedEffect(mensagemErro) {
        if (mensagemErro != null) {
            deslocamento.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 350
                    0f at 0
                    (-10f) at 50
                    10f at 100
                    (-8f) at 150
                    8f at 200
                    (-4f) at 250
                    4f at 300
                    0f at 350
                }
            )
        } else {
            deslocamento.snapTo(0f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoVinculo)
    ) {
        BackgroundPadrao()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Vincular cuidador",
                color = TextoVinculo,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Aponte a câmera para o QR Code do cuidador ou digite o código de 6 dígitos abaixo.",
                color = Color(0xFF273344),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            // 1. Leitor de QR Code (validação automática ao ler 6 dígitos)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                if (temPermissaoCamera) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        LeitorQrCodeCamera(
                            pausado = carregando,
                            onQrCodeLido = { textoBruto ->
                                val limpo = textoBruto.trim()
                                if (
                                    RegexCodigoSeisDigitos.matches(limpo) &&
                                    limpo != ultimoCodigoVerificado
                                ) {
                                    if (codigoIncorreto) onLimparErro()
                                    codigo = limpo
                                }
                            }
                        )

                        GuiaMiraQrCode(modifier = Modifier.size(160.dp))


                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp),
                            shape = CircleShape,
                            color = Color(0xCC0F172A)
                        ) {
                            Text(
                                text = if (carregando) "Validando código..." else "Enquadre o QR Code",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE5F3FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.QrCodeScanner,
                                contentDescription = null,
                                tint = AzulVinculo,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Escanear QR Code",
                            color = TextoVinculo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Permita o acesso à câmera para ler o código automaticamente.",
                            color = Color(0xFF5A657D),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                launcherPermissaoCamera.launch(Manifest.permission.CAMERA)
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = AzulVinculo)
                        ) {
                            Text(
                                text = "Ativar câmera",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Divisor entre QR Code e digitação manual
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFC5D5E7),
                    thickness = 1.dp
                )
                Text(
                    text = "ou digite o código de 6 dígitos",
                    color = Color(0xFF5A657D),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFC5D5E7),
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Campo manual de 6 dígitos (segunda opção)
            BasicTextField(
                value = codigo,
                onValueChange = { novoCodigo ->
                    val formatado = novoCodigo.filter { it in '0'..'9' }.take(6)
                    if (formatado != codigo) {
                        codigo = formatado
                        ultimoCodigoVerificado = null
                        if (codigoIncorreto) onLimparErro()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Código de vínculo de seis dígitos" },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),
                singleLine = true,
                textStyle = TextStyle(color = Color.Transparent),
                cursorBrush = SolidColor(AzulVinculo),
                decorationBox = { campoInterno ->
                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset {
                                    IntOffset(
                                        x = with(density) { deslocamento.value.dp.toPx() }.roundToInt(),
                                        y = 0
                                    )
                                }
                                .clearAndSetSemantics {},
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(6) { indice ->
                                val proximoDigito = indice == codigo.length && codigo.length < 6

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(60.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (codigoIncorreto) Color.Red.copy(alpha = 0.05f) else Color.White,
                                    border = BorderStroke(
                                        width = if (proximoDigito || codigoIncorreto) 2.dp else 1.dp,
                                        color = when {
                                            codigoIncorreto -> Color.Red
                                            proximoDigito -> Color(0xFF438FB7)
                                            else -> Color(0xFFC5D5E7)
                                        }
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = codigo.getOrNull(indice)?.toString().orEmpty(),
                                            color = if (codigoIncorreto) Color.Red else TextoVinculo,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier.size(1.dp)
                        ) {
                            campoInterno()
                        }
                    }
                }
            )

            if (codigoIncorreto) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = mensagemErro.orEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            OutlinedButton(
                onClick = {
                    onLimparErro()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(
                    1.dp,
                    Color(0xFFB9CADD)
                )
            ) {
                Text(
                    text = "Voltar",
                    color = Color(0xFF287EA8),
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
private fun LeitorQrCodeCamera(
    pausado: Boolean,
    onQrCodeLido: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val pausadoAtual by rememberUpdatedState(pausado)
    val onQrCodeLidoAtual by rememberUpdatedState(onQrCodeLido)

    val executorCamera = remember { Executors.newSingleThreadExecutor() }
    val scanner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            runCatching {
                ProcessCameraProvider.getInstance(context).get().unbindAll()
            }
            scanner.close()
            executorCamera.shutdown()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val analiseImagem = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(executorCamera) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage == null || pausadoAtual) {
                                imageProxy.close()
                                return@setAnalyzer
                            }

                            val imagemEntrada = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )

                            scanner.process(imagemEntrada)
                                .addOnSuccessListener { barcodes ->
                                    val textoBruto = barcodes.firstNotNullOfOrNull { it.rawValue }
                                    if (!textoBruto.isNullOrBlank()) {
                                        onQrCodeLidoAtual(textoBruto)
                                    }
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        }
                    }

                runCatching {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analiseImagem
                    )
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}

@Composable
private fun GuiaMiraQrCode(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val tamanhoCanto = 24.dp.toPx()
        val espessura = 3.5.dp.toPx()
        val cor = Color.White

        // Superior esquerdo
        drawLine(cor, Offset(0f, 0f), Offset(tamanhoCanto, 0f), espessura, StrokeCap.Round)
        drawLine(cor, Offset(0f, 0f), Offset(0f, tamanhoCanto), espessura, StrokeCap.Round)

        // Superior direito
        drawLine(cor, Offset(size.width - tamanhoCanto, 0f), Offset(size.width, 0f), espessura, StrokeCap.Round)
        drawLine(cor, Offset(size.width, 0f), Offset(size.width, tamanhoCanto), espessura, StrokeCap.Round)

        // Inferior esquerdo
        drawLine(cor, Offset(0f, size.height - tamanhoCanto), Offset(0f, size.height), espessura, StrokeCap.Round)
        drawLine(cor, Offset(0f, size.height), Offset(tamanhoCanto, size.height), espessura, StrokeCap.Round)

        // Inferior direito
        drawLine(cor, Offset(size.width - tamanhoCanto, size.height), Offset(size.width, size.height), espessura, StrokeCap.Round)
        drawLine(cor, Offset(size.width, size.height - tamanhoCanto), Offset(size.width, size.height), espessura, StrokeCap.Round)
    }
}
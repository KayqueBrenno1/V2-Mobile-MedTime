package com.example.v2_medtime.screens

import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.viewmodel.VinculoViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

private val AzulPrincipalGerar = Color(0xFF108BD5)
private val TextoEscuroGerar = Color(0xFF141B3E)
private val TextoSecundarioGerar = Color(0xFF5A657D)
private val FundoGerar = Color(0xFFEAF3FF)

@Composable
fun GerarCodigoScreen(
    navController: NavController,
    vinculoViewModel: VinculoViewModel,
    onErro: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var codigo by rememberSaveable { mutableStateOf("") }

    val solicitarNovoCodigo = {
        vinculoViewModel.gerarCodigo(
            onSucesso = { novoCodigo ->
                codigo = novoCodigo.trim()
            },
            onErro = onErro
        )
    }

    LaunchedEffect(Unit) {
        if (codigo.isBlank()) {
            solicitarNovoCodigo()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoGerar)
    ) {
        BackgroundPadrao()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Vincular paciente",
                modifier = Modifier.fillMaxWidth(),
                color = TextoEscuroGerar,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Peça para o paciente escanear o QR Code ou digitar o código de 6 dígitos no aplicativo.",
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF273344),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. QR Code em destaque em cima
                    Box(
                        modifier = Modifier
                            .size(216.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF8FAFF))
                            .border(1.5.dp, Color(0xFFD5E6F7), RoundedCornerShape(20.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (codigo.isNotBlank()) {
                            QrCodeImagem(
                                conteudo = codigo,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.QrCode2,
                                    contentDescription = null,
                                    tint = AzulPrincipalGerar.copy(alpha = 0.5f),
                                    modifier = Modifier.size(56.dp)
                                )
                                Text(
                                    text = "Gerando QR Code...",
                                    color = TextoSecundarioGerar,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Aponte a câmera do celular do paciente para este QR Code",
                        color = TextoSecundarioGerar,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(
                        color = Color(0xFFE2ECF7),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // 2. Código de 6 dígitos embaixo (para digitar ou mandar pelo WhatsApp)
                    Text(
                        text = "Ou informe o código de 6 dígitos",
                        color = TextoSecundarioGerar,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(6) { indice ->
                            val caractere = codigo.getOrNull(indice)?.toString() ?: "-"
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF3F8FD),
                                border = BorderStroke(1.dp, Color(0xFFC5D5E7))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = caractere,
                                        color = TextoEscuroGerar,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3. Ações rápidas: Copiar e Compartilhar (WhatsApp, etc.)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (codigo.isNotBlank()) {
                                    clipboardManager.setText(AnnotatedString(codigo))
                                    Toast.makeText(
                                        context,
                                        "Código copiado!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            enabled = codigo.isNotBlank(),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color(0xFFB8CCDF))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null,
                                tint = AzulPrincipalGerar,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Copiar",
                                color = AzulPrincipalGerar,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = {
                                if (codigo.isNotBlank()) {
                                    val mensagem = "Olá! Use este código de 6 dígitos no aplicativo MedTime para vincular sua conta ao meu perfil de cuidador: $codigo"
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, mensagem)
                                    }
                                    runCatching {
                                        context.startActivity(
                                            Intent.createChooser(sendIntent, "Compartilhar código")
                                        )
                                    }
                                }
                            },
                            enabled = codigo.isNotBlank(),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AzulPrincipalGerar
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Enviar",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    TextButton(
                        onClick = { solicitarNovoCodigo() },
                        enabled = !vinculoViewModel.carregando
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = null,
                            tint = TextoSecundarioGerar,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Gerar novo código",
                            color = TextoSecundarioGerar,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(1.dp, Color(0xFFB9CADD))
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

@Composable
private fun QrCodeImagem(
    conteudo: String,
    modifier: Modifier = Modifier,
    tamanhoPx: Int = 512
) {
    val bitmap = remember(conteudo, tamanhoPx) {
        gerarBitmapQrCode(conteudo = conteudo, tamanhoPx = tamanhoPx)
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "QR Code do código de vínculo $conteudo",
            filterQuality = FilterQuality.None,
            modifier = modifier
        )
    }
}

private fun gerarBitmapQrCode(
    conteudo: String,
    tamanhoPx: Int
): Bitmap? = runCatching {
    val hints = mapOf(
        EncodeHintType.MARGIN to 1,
        EncodeHintType.CHARACTER_SET to "UTF-8"
    )
    val matrix = QRCodeWriter().encode(
        conteudo,
        BarcodeFormat.QR_CODE,
        tamanhoPx,
        tamanhoPx,
        hints
    )
    val largura = matrix.width
    val altura = matrix.height
    val pixels = IntArray(largura * altura)
    val corEscura = 0xFF0F172A.toInt()
    val corClara = 0xFFFFFFFF.toInt()

    for (y in 0 until altura) {
        val offset = y * largura
        for (x in 0 until largura) {
            pixels[offset + x] = if (matrix[x, y]) corEscura else corClara
        }
    }

    Bitmap.createBitmap(largura, altura, Bitmap.Config.ARGB_8888).apply {
        setPixels(pixels, 0, largura, 0, 0, largura, altura)
    }
}.getOrNull()
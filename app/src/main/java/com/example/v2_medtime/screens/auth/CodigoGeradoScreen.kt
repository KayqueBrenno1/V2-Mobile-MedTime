package com.example.v2_medtime.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

private val AzulCodigo = Color(0xFF108BD5)
private val TextoCodigo = Color(0xFF101A2A)
private val CinzaCodigo = Color(0xFF597085)

@Composable
fun CodigoGeradoScreen(
    navController: NavController,
    codigo: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF3FF))
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
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD9EDFC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.QrCode2,
                    contentDescription = null,
                    tint = AzulCodigo,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Código de vínculo",
                color = TextoCodigo,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Mostre o QR Code para o paciente escanear ou envie o código de 6 dígitos.",
                color = CinzaCodigo,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            CardQrCodeECodigo(codigo = codigo)

            Spacer(modifier = Modifier.height(22.dp))

            OutlinedButton(
                onClick = {
                    if (!navController.popBackStack("gerenciar_pacientes", inclusive = false)) {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color(0xFFB8CCDF))
            ) {
                Text(
                    text = "Concluir",
                    color = AzulCodigo,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CardQrCodeECodigo(
    codigo: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val codigoLimpo = codigo.trim()

    Surface(
        modifier = modifier.fillMaxWidth(),
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
            // QR Code em destaque em cima
            Box(
                modifier = Modifier
                    .size(216.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF8FAFF))
                    .border(1.5.dp, Color(0xFFD5E6F7), RoundedCornerShape(20.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                QrCodeImagem(
                    conteudo = codigoLimpo,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Peça para o paciente escanear o QR Code no aplicativo",
                color = CinzaCodigo,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            HorizontalDivider(
                color = Color(0xFFE2ECF7),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Código de 6 dígitos embaixo
            Text(
                text = "Ou digite / envie o código de 6 dígitos",
                color = CinzaCodigo,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(6) { indice ->
                    val caractere = codigoLimpo.getOrNull(indice)?.toString() ?: "-"
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
                                color = TextoCodigo,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Código MedTime", codigoLimpo))
                        Toast.makeText(context, "Código copiado!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color(0xFFB8CCDF))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        tint = AzulCodigo,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Copiar",
                        color = AzulCodigo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = {
                        val mensagem = "Olá! Use este código de 6 dígitos no MedTime para vincular sua conta: $codigoLimpo"
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, mensagem)
                        }
                        runCatching {
                            context.startActivity(
                                Intent.createChooser(sendIntent, "Compartilhar código")
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulCodigo
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
            contentDescription = "QR Code do código $conteudo",
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
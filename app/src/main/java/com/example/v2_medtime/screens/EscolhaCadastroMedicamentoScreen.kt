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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.v2_medtime.R
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.viewmodel.MedicamentoViewModel
import java.io.File

private enum class ModoCadastro {
    MANUAL,
    OCR
}

@Composable
fun EscolhaCadastroMedicamentoScreen(
    navController: NavController,
    viewModel: MedicamentoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var modoSelecionado by remember { mutableStateOf(ModoCadastro.MANUAL) }

    val azulPrincipal = Color(0xFF0077D7)
    val azulIcone = Color(0xFF1592D0)
    val textoEscuro = Color(0xFF0F172A)
    val textoSecundario = Color(0xFF5A657D)

    var caminhoReceitaAtual by rememberSaveable { mutableStateOf<String?>(null) }

    val cameraOcrLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { sucesso ->
            val arquivo = caminhoReceitaAtual?.let(::File)
            caminhoReceitaAtual = null

            if (sucesso && arquivo != null && arquivo.exists() && arquivo.length() > 0L) {
                viewModel.processarReceitaOcr(
                    imagemReceita = arquivo,
                    limparAnteriores = true
                ) {
                    if (viewModel.remediosIdentificadosOcr.isNotEmpty()) {
                        navController.navigate("medicamentos_identificados")
                    } else {
                        Toast.makeText(
                            context,
                            "Nenhum medicamento identificado na receita.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .navigationBarsPadding()
                .statusBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))

                // Cabeçalho: < Cadastrar Medicamento
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { navController.popBackStack() }
                        .padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Voltar",
                        tint = textoEscuro,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Cadastrar Medicamento",
                        color = textoEscuro,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Escolha como deseja adicionar\nseu medicamento:",
                    color = textoSecundario,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Opção 1: Cadastro Manual
                OpcaoCadastroCard(
                    icon = Icons.Outlined.Edit,
                    titulo = "Cadastro Manual",
                    subtitulo = "Preencha as informações\ndo medicamento",
                    selecionado = modoSelecionado == ModoCadastro.MANUAL,
                    onClick = { modoSelecionado = ModoCadastro.MANUAL }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Opção 2: Escanear Receita (OCR + IA)
                OpcaoCadastroCard(
                    icon = Icons.Outlined.DocumentScanner,
                    titulo = "Escanear Receita",
                    subtitulo = "Use a câmera \npara ler sua receita \ncom IA",
                    selecionado = modoSelecionado == ModoCadastro.OCR,
                    onClick = { modoSelecionado = ModoCadastro.OCR }
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Divisor "ou"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFD6E2F0),
                        thickness = 1.dp
                    )
                    Text(
                        text = "ou",
                        color = textoSecundario,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFD6E2F0),
                        thickness = 1.dp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Banner Informativo Inferior
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
                            text = "Você também pode cadastrar o medicamento manualmente após escanear a receita.",
                            color = Color(0xFF2B4C7E),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Botão Principal "Continuar"
            Button(
                onClick = {
                    when (modoSelecionado) {
                        ModoCadastro.MANUAL -> {
                            navController.navigate("MedicamentoScreen")
                        }

                        ModoCadastro.OCR -> {
                            val arquivo = File(
                                context.cacheDir,
                                "receita_ocr_${System.currentTimeMillis()}.jpg"
                            )
                            try {
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    arquivo
                                )
                                caminhoReceitaAtual = arquivo.absolutePath
                                cameraOcrLauncher.launch(uri)
                            } catch (e: Exception) {
                                arquivo.delete()
                                caminhoReceitaAtual = null
                                Toast.makeText(
                                    context,
                                    "Não foi possível abrir a câmera neste dispositivo.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                },
                enabled = !viewModel.isEscaneandoOcr,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .height(54.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = azulPrincipal)
            ) {
                if (viewModel.isEscaneandoOcr) {
                    CircularProgressIndicator(
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Analisando receita...",
                        color = colorResource(R.color.azul_texto),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "Continuar",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun OpcaoCadastroCard(
    icon: ImageVector,
    titulo: String,
    subtitulo: String,
    selecionado: Boolean,
    onClick: () -> Unit
) {
    val azulBorda = Color(0xFF4299E1)
    val azulFundoSelecionado = Color(0xFFE6F1FC)
    val azulIconeFundo = if (selecionado) Color(0xFFD2E7FA) else Color(0xFFEAF3FC)
    val azulIcone = Color(0xFF1592D0)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = if (selecionado) azulFundoSelecionado else Color.White,
        border = if (selecionado) BorderStroke(1.5.dp, azulBorda) else null,
        shadowElevation = if (selecionado) 0.dp else 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(if (selecionado) CircleShape else RoundedCornerShape(18.dp))
                    .background(azulIconeFundo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = titulo,
                    tint = azulIcone,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    color = Color(0xFF0F172A),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitulo,
                    color = Color(0xFF5A657D),
                    fontSize = 14.sp,
                    lineHeight = 19.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF5A657D),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
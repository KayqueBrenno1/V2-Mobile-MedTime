package com.example.v2_medtime.screens.components

import android.content.ActivityNotFoundException
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun SeletorFoto(
    aberto: Boolean,
    onFechar: () -> Unit,
    onFotoSelecionada: (Uri) -> Unit
) {
    val contexto = LocalContext.current
    var fotoEmCaptura by rememberSaveable { mutableStateOf<Uri?>(null) }

    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { foto ->
        foto?.let(onFotoSelecionada)
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { sucesso ->
        val foto = fotoEmCaptura
        fotoEmCaptura = null
        if (foto != null) {
            if (sucesso) onFotoSelecionada(foto)
            else contexto.contentResolver.delete(foto, null, null)
        }
    }

    if (aberto) {
        AlertDialog(
            onDismissRequest = onFechar,
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Selecionar foto",
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OpcaoFoto(
                        titulo = "Tirar foto",
                        icone = Icons.Outlined.CameraAlt,
                        onClick = tirarFoto@{
                            onFechar()
                            val arquivo = runCatching {
                                File.createTempFile("foto_medtime_", ".jpg", contexto.cacheDir)
                            }.getOrElse {
                                Toast.makeText(contexto, "Não foi possível preparar a captura.", Toast.LENGTH_SHORT).show()
                                return@tirarFoto
                            }

                            try {
                                val foto = FileProvider.getUriForFile(
                                    contexto,
                                    "${contexto.packageName}.fileprovider",
                                    arquivo
                                )
                                fotoEmCaptura = foto
                                camera.launch(foto)
                            } catch (_: ActivityNotFoundException) {
                                arquivo.delete()
                                fotoEmCaptura = null
                                Toast.makeText(contexto, "Nenhum aplicativo de câmera disponível.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    OpcaoFoto(
                        titulo = "Escolher da galeria",
                        icone = Icons.Outlined.Image,
                        onClick = {
                            onFechar()
                            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    )
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = onFechar,
                    border = BorderStroke(1.dp, Color(0xFFB8CCDF))
                ) {
                    Text("Cancelar", color = Color(0xFF086FA7))
                }
            }
        )
    }
}

@Composable
private fun OpcaoFoto(
    titulo: String,
    icone: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF3F8FD)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE5F3FD), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icone, contentDescription = null, tint = Color(0xFF086FA7))
            }

            Text(
                text = titulo,
                color = Color(0xFF0F172A),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

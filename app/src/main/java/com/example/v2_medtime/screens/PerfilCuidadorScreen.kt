package com.example.v2_medtime.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.R
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.viewmodel.PerfilViewModel

@Composable
fun PerfilCuidadorScreen(
    navController: NavController,
    viewModel: PerfilViewModel,
    modifier: Modifier = Modifier,
    mostrarBarraInferior: Boolean = false,
    espacamentoInferiorConteudo: Dp = 0.dp,
    onVoltarPacientes: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val perfil = viewModel.perfil
    var mostrarDialogoSair by remember { mutableStateOf(false) }

    val textoEscuro = Color(0xFF0F172A)
    val textoSecundario = Color(0xFF64748B)

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
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = espacamentoInferiorConteudo + if (mostrarBarraInferior) 96.dp else 16.dp
                )
        ) {
            Spacer(modifier = Modifier.height(35.dp))

            Text(
                text = "Meu perfil",
                color = textoEscuro,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Card Principal do Usuário -> Abre "Editar perfil"
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("editar_perfil") },
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarCircularUsuario(
                        nome = perfil.nomeCompleto,
                        fotoModel = perfil.fotoUriLocal ?: perfil.fotoUrlRemota,
                        tamanho = 68
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = perfil.nomeCompleto,
                            color = textoEscuro,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = perfil.email,
                            color = textoSecundario,
                            fontSize = 14.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Editar perfil",
                        tint = textoSecundario,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Minha conta",
                color = textoEscuro,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PerfilCuidadorOpcaoCard(
                    icon = Icons.Outlined.Lock,
                    iconeFundo = Color(0xFFE3F7F2),
                    iconeCor = Color(0xFF16A37A),
                    titulo = "Alterar senha",
                    onClick = {
                        navController.navigate("alterar_senha")
                    }
                )

                PerfilCuidadorOpcaoCard(
                    icon = Icons.Outlined.LocationOn,
                    iconeFundo = Color(0xFFE5F3FD),
                    iconeCor = Color(0xFF1592D0),
                    titulo = "Localização",
                    onClick = {
                        Toast.makeText(context, "Permissões de localização", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Botão "Sair da conta"
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarDialogoSair = true },
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFFDECEC),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = "Sair",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Sair da conta",
                        color = Color(0xFFDC2626),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (mostrarBarraInferior) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            ) {
                BarraInferiorPacientes(
                    onPacientes = {
                        if (onVoltarPacientes != null) {
                            onVoltarPacientes()
                        } else if (!navController.popBackStack("gerenciar_pacientes", inclusive = false)) {
                            navController.navigate("gerenciar_pacientes") {
                                launchSingleTop = true
                            }
                        }
                    },
                    onPerfil = {},
                    perfilSelecionado = true
                )
            }
        }

        if (mostrarDialogoSair) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoSair = false },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White,
                icon = {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFFDECEC), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = null,
                            tint = Color(0xFFB91C1C),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = "Sair da conta?",
                        color = textoEscuro,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Você precisará entrar novamente para acessar sua conta.",
                        color = textoSecundario,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB91C1C),
                            contentColor = Color.White
                        ),
                        onClick = {
                            mostrarDialogoSair = false
                            viewModel.sairDaConta {
                                navController.navigate("boas_vindas") {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    ) {
                        Text("Sair", fontWeight = FontWeight.SemiBold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { mostrarDialogoSair = false },
                        border = BorderStroke(1.dp, colorResource(R.color.azul_borda)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF086FA7)
                        )
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }
    }
}

@Composable
private fun PerfilCuidadorOpcaoCard(
    icon: ImageVector,
    iconeFundo: Color,
    iconeCor: Color,
    titulo: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconeFundo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = titulo,
                    tint = iconeCor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = titulo,
                color = Color(0xFF0F172A),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF64748B),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
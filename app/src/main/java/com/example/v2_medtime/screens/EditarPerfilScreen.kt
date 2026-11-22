package com.example.v2_medtime.screens

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.screens.components.SeletorFoto
import com.example.v2_medtime.utils.FormatadorCampos
import com.example.v2_medtime.utils.Validadores
import com.example.v2_medtime.viewmodel.PerfilViewModel

@Composable
fun EditarPerfilScreen(
    navController: NavController,
    viewModel: PerfilViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val perfilAtual = viewModel.perfil

    var nomeCompleto by remember { mutableStateOf(perfilAtual.nomeCompleto) }
    var email by remember { mutableStateOf(perfilAtual.email) }
    var dataNascimento by remember { mutableStateOf(perfilAtual.dataNascimento) }
    val cpfOriginal = perfilAtual.cpf.filter { it.isDigit() }
    var cpf by remember(cpfOriginal) { mutableStateOf(cpfOriginal) }
    val cpfInvalido = cpf != cpfOriginal && !Validadores.validarCpf(cpf)
    var fotoSelecionada by rememberSaveable { mutableStateOf<Uri?>(perfilAtual.fotoUriLocal) }
    var mostrarSeletorFoto by remember { mutableStateOf(false) }

    val textoEscuro = Color(0xFF0F172A)

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
                Spacer(modifier = Modifier.height(12.dp))

                // Cabeçalho: < Voltar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { navController.popBackStack() }
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Voltar",
                        tint = textoEscuro,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Voltar",
                        color = textoEscuro,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AvatarCircularUsuario(
                    nome = nomeCompleto,
                    fotoModel = fotoSelecionada ?: perfilAtual.fotoUrlRemota,
                    tamanho = 120,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                TextButton(
                    onClick = { mostrarSeletorFoto = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Alterar foto")
                }

                if (fotoSelecionada != null) {
                    Text(
                        text = "A foto será salva no seu perfil.",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                }

                SeletorFoto(
                    aberto = mostrarSeletorFoto,
                    onFechar = { mostrarSeletorFoto = false },
                    onFotoSelecionada = { fotoSelecionada = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Campos de Edição
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    CampoPerfilItem(
                        icon = Icons.Outlined.PersonOutline,
                        label = "Nome completo",
                        value = nomeCompleto,
                        onValueChange = { nomeCompleto = it }
                    )

                    CampoPerfilItem(
                        icon = Icons.Outlined.Email,
                        label = "E-mail",
                        value = email,
                        onValueChange = { email = it }
                    )

                    CampoPerfilItem(
                        icon = Icons.Outlined.CalendarToday,
                        label = "Data de nascimento",
                        value = dataNascimento,
                        onValueChange = { dataNascimento = it }
                    )

                    CampoPerfilItem(
                        icon = Icons.Outlined.Badge,
                        label = "CPF (opcional)",
                        value = cpf,
                        onValueChange = { cpf = it.filter { digito -> digito.isDigit() }.take(11) },
                        tipoTeclado = KeyboardType.Number,
                        transformacaoVisual = FormatadorCampos.cpfVisualTransformation(),
                        mensagemErro = if (cpfInvalido) "Informe um CPF válido." else null
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Botão Salvar Alterações
            Button(
                onClick = {
                    viewModel.salvarAlteracoesPerfil(
                        nomeCompleto = nomeCompleto,
                        email = email,
                        dataNascimento = dataNascimento,
                        cpf = if (cpf == cpfOriginal) perfilAtual.cpf else cpf,
                        fotoUriLocal = fotoSelecionada
                    ) {
                        Toast.makeText(context, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                enabled = !viewModel.isLoading && nomeCompleto.isNotBlank() && email.isNotBlank() && !cpfInvalido,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .height(54.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077D7))
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = "Salvar alterações",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CampoPerfilItem(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    transformacaoVisual: VisualTransformation = VisualTransformation.None,
    mensagemErro: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 6.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE7F0FA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF1E4E8C),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color(0xFF0F172A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                isError = mensagemErro != null,
                keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
                visualTransformation = transformacaoVisual,
                singleLine = true,
                textStyle = TextStyle(fontSize = 15.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF0077D7),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedTextColor = Color(0xFF0F172A),
                    focusedTextColor = Color(0xFF0F172A)
                )
            )

            if (mensagemErro != null) {
                Text(
                    text = mensagemErro,
                    modifier = Modifier.padding(top = 4.dp),
                    color = Color(0xFFB91C1C),
                    fontSize = 13.sp
                )
            }
        }
    }
}
package com.example.v2_medtime.screens.auth

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.v2_medtime.R
import com.example.v2_medtime.data.model.DadosPessoais
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.screens.components.SeletorDataNascimento
import com.example.v2_medtime.utils.FormatadorCampos

@Composable
fun DadosPessoaisScreen(
    modifier: Modifier = Modifier,
    onVoltar: () -> Unit = {},
    onProximo: (DadosPessoais) -> Unit = {}
) {
    var nome by rememberSaveable {
        mutableStateOf("")
    }

    var cpf by rememberSaveable {
        mutableStateOf("")
    }

    var dataNascimento by rememberSaveable {
        mutableStateOf("")
    }

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var telefone by rememberSaveable {
        mutableStateOf("")
    }

    var mostrarCalendario by rememberSaveable {
        mutableStateOf(false)
    }

    if (mostrarCalendario) {
        SeletorDataNascimento(
            onDismiss = {
                mostrarCalendario = false
            },
            onSelecionarData = { data ->
                dataNascimento = data
                mostrarCalendario = false
            }
        )
    }

    val azulTexto = colorResource(R.color.azul_texto)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.azul_fundo))
    ) {
        BackgroundPadrao()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 28.dp)
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {

                Row(
                    modifier = Modifier
                        .clickable { onVoltar() }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBackIos,
                        contentDescription = null,
                        tint = azulTexto,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Voltar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = azulTexto
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                IndicadorEtapasCadastro(
                    etapaAtual = 1
                )

                Spacer(modifier = Modifier.height(38.dp))

                Text(
                    text = "Dados pessoais",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = azulTexto
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Informe seus dados para criar sua conta.",
                    fontSize = 17.sp,
                    color = colorResource(R.color.azul_texto)
                )

                Spacer(modifier = Modifier.height(25.dp))

                // Nome completo
                CampoDadosPessoais(
                    valor = nome,
                    onValorChange = { nome = it },
                    titulo = "Nome completo",
                    placeholder = "Maria Silva",
                    icone = {
                        Icon(
                            imageVector = Icons.Outlined.PersonOutline,
                            contentDescription = null
                        )
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // CPF
                CampoDadosPessoais(
                    valor = cpf,
                    onValorChange = { valor ->
                        cpf = valor.filter { it.isDigit() }.take(11)
                    },
                    titulo = "CPF (Opcional)",
                    keyboardType = KeyboardType.Number,
                    visualTransformation = FormatadorCampos.cpfVisualTransformation(),
                    icone = {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = null
                        )
                    }
                )


                Spacer(modifier = Modifier.height(10.dp))

                // Data de nascimento
                CampoDadosPessoais(
                    valor = dataNascimento,
                    onValorChange = { valor ->
                        dataNascimento = valor.filter { it.isDigit() }.take(8)
                    },
                    titulo = "Data de nascimento",
                    placeholder = "DD/MM/AAAA",
                    keyboardType = KeyboardType.Number,
                    visualTransformation = FormatadorCampos.dataVisualTransformation(),
                    icone = {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                mostrarCalendario = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = "Selecionar data de nascimento",
                                tint = colorResource(R.color.azul_botao)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // E-mail
                CampoDadosPessoais(
                    valor = email,
                    onValorChange = { email = it },
                    titulo = "E-mail",
                    keyboardType = KeyboardType.Email,
                    icone = {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null
                        )
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Telefone
                CampoDadosPessoais(
                    valor = telefone,
                    onValorChange = { telefone = it },
                    titulo = "Telefone",
                    keyboardType = KeyboardType.Phone,
                    icone = {
                        Icon(
                            imageVector = Icons.Outlined.Phone,
                            contentDescription = null
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Próximo
            Button(
                onClick = {
                    onProximo(
                        DadosPessoais(
                            nome = nome,
                            cpf = cpf,
                            dataNascimento = dataNascimento,
                            email = email,
                            telefone = telefone
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.azul_botao)
                )
            ) {
                Text(
                    text = "Próximo",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.branco)
                )
            }

            Spacer(modifier = Modifier.height(34.dp))
        }
    }
}

@Composable
private fun IndicadorEtapasCadastro(
    etapaAtual: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (etapa in 1..3) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = if (etapa == etapaAtual) {
                            colorResource(R.color.azul_botao)
                        } else {
                            colorResource(R.color.azul_curva)
                        },
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = etapa.toString(),
                    fontSize = 14.sp,
                    color = if (etapa == etapaAtual) {
                        colorResource(R.color.branco)
                    } else {
                        colorResource(R.color.azul_texto)
                    }
                )
            }

            if (etapa < 3) {
                HorizontalDivider(
                    modifier = Modifier.width(90.dp),
                    color = colorResource(R.color.azul_borda)
                )
            }
        }
    }
}

@Composable
private fun CampoDadosPessoais(
    valor: String,
    onValorChange: (String) -> Unit,
    titulo: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    icone: @Composable () -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 68.dp),
        label = {
            Text(
                text = titulo,
                fontSize = 16.sp
            )
        },
        placeholder = {
            if (placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    fontSize = 16.sp
                )
            }
        },
        leadingIcon = icone,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorResource(R.color.azul_botao),
            unfocusedBorderColor = colorResource(R.color.azul_borda),
            focusedTextColor = colorResource(R.color.azul_texto),
            unfocusedTextColor = colorResource(R.color.azul_texto),
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedLabelColor = colorResource(R.color.azul_texto),
            unfocusedLabelColor = colorResource(R.color.azul_texto),
            focusedLeadingIconColor = colorResource(R.color.azul_texto),
            unfocusedLeadingIconColor = colorResource(R.color.azul_texto)
        )
    )
}

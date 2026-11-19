package com.example.v2_medtime.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.v2_medtime.R
import com.example.v2_medtime.data.model.DadosPessoais
import com.example.v2_medtime.screens.components.BackgroundPadrao
import com.example.v2_medtime.utils.FormatadorCampos
import com.example.v2_medtime.utils.Validadores

@Composable
fun DadosPessoaisScreen(
    modifier: Modifier = Modifier,
    onVoltar: () -> Unit = {},
    onProximo: (DadosPessoais) -> Unit = {}
) {
    val density = LocalDensity.current
    val tecladoAberto = WindowInsets.ime.getBottom(density) > 0

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

    var tentouProximo by rememberSaveable {
        mutableStateOf(false)
    }

    val erroNome = tentouProximo && !Validadores.validarNome(nome)
    val erroCpf = tentouProximo && !Validadores.validarCpf(cpf)
    val erroData = tentouProximo &&
            !Validadores.validarDataNascimento(dataNascimento)
    val erroEmail = tentouProximo && !Validadores.validarEmail(email)
    val erroTelefone = tentouProximo &&
            !Validadores.validarTelefone(telefone)

    val azulTexto = colorResource(R.color.azul_texto)
    val azulBorda = colorResource(R.color.azul_borda)

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(
                    modifier = Modifier.height(
                        if (tecladoAberto) 16.dp else 35.dp
                    )
                )

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
                    },
                    mensagemErro = if (erroNome) {
                        "Informe seu nome completo."
                    } else null,
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
                    },
                    mensagemErro = if (erroCpf) {
                        "Informe um CPF válido."
                    } else null,
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
                    mensagemErro = if (erroData) {
                        "Informe uma data de nascimento válida."
                    } else null,
                )

                Spacer(modifier = Modifier.height(10.dp))

                // E-mail
                CampoDadosPessoais(
                    valor = email,
                    onValorChange = { valor ->
                        if (valor.length <= 255) {
                            email = valor.filterNot { it.isWhitespace() }
                        }
                    },
                    titulo = "E-mail",
                    placeholder = "exemplo@email.com",
                    keyboardType = KeyboardType.Email,
                    icone = {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null
                        )
                    },
                    mensagemErro = if (erroEmail) {
                        if (email.isBlank()) "Informe seu e-mail."
                        else "Informe um e-mail válido."
                    } else null,
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Telefone
                CampoDadosPessoais(
                    valor = telefone,
                    onValorChange = { valor ->
                        telefone = valor.filter { it.isDigit() }.take(11)
                    },
                    titulo = "Telefone (Opcional)",
                    placeholder = "(11) 99999-9999",
                    keyboardType = KeyboardType.Phone,
                    visualTransformation = FormatadorCampos.telefoneVisualTransformation(),
                    icone = {
                        Icon(
                            imageVector = Icons.Outlined.Phone,
                            contentDescription = null
                        )
                    },
                    mensagemErro = if (erroTelefone) {
                        "Informe um telefone válido."
                    } else null,
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!tecladoAberto) {
                // Próximo
                Button(
                    onClick = {
                        tentouProximo = true

                        if (
                            Validadores.validarNome(nome) &&
                            Validadores.validarCpf(cpf) &&
                            Validadores.validarDataNascimento(dataNascimento) &&
                            Validadores.validarEmail(email) &&
                            Validadores.validarTelefone(telefone)
                        ) {
                            onProximo(
                                DadosPessoais(
                                    nome = nome.trim(),
                                    cpf = cpf,
                                    dataNascimento = dataNascimento,
                                    email = email.trim(),
                                    telefone = telefone
                                )
                            )
                        }
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

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onVoltar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        azulBorda
                    )
                ) {
                    Text(
                        text = "Voltar",
                        fontSize = 19.sp,
                        color = colorResource(R.color.azul_entrar)
                    )
                }
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
    imeAction: ImeAction = ImeAction.Next,
    mensagemErro: String? = null,
    icone: @Composable () -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        isError = mensagemErro != null,
        visualTransformation = visualTransformation,
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
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        colors = OutlinedTextFieldDefaults.colors(
            errorBorderColor = Color.Red,
            errorTextColor = colorResource(R.color.azul_texto),
            errorCursorColor = colorResource(R.color.azul_texto),
            errorLabelColor = colorResource(R.color.azul_texto),
            errorLeadingIconColor = colorResource(R.color.azul_texto),
            errorPlaceholderColor = Color.Black.copy(alpha = 0.4f),
            errorContainerColor = Color.Transparent,
            focusedPlaceholderColor = Color.Black.copy(alpha = 0.4f),
            unfocusedPlaceholderColor = Color.Black.copy(alpha = 0.4f),
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

    if (mensagemErro != null) {
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = mensagemErro,
            fontSize = 14.sp,
            color = Color.Red
        )
    }
}

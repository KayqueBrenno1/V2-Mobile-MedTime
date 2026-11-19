    package com.example.v2_medtime.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.model.DadosPessoais
import com.example.v2_medtime.data.repository.CadastroRepository
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import java.io.IOException

class CadastroViewModel : ViewModel() {
    fun limparEstadoEnvioCodigo() {
        codigoEnviado = false
        erroEnvioCodigo = null
    }

    var perfil by mutableStateOf("")
        private set

    var dadosPessoais by mutableStateOf<DadosPessoais?>(null)
        private set

    private var senhaCadastro: String? = null

    fun definirPerfil(novoPerfil: String) {
        perfil = novoPerfil
    }

    fun definirDadosPessoais(dados: DadosPessoais) {
        dadosPessoais = dados
    }

    fun definirSenha(senha: String) {
        senhaCadastro = senha
    }

    fun obterSenha(): String? {
        return senhaCadastro
    }

    fun limparCadastro() {
        perfil = ""
        dadosPessoais = null
        senhaCadastro = null
        limparEstadoEnvioCodigo()
    }

    override fun onCleared() {
        limparCadastro()
        super.onCleared()
    }

    private val cadastroRepository = CadastroRepository()

    var enviandoCodigo by mutableStateOf(false)
        private set

    var erroEnvioCodigo by mutableStateOf<String?>(null)
        private set

    var codigoEnviado by mutableStateOf(false)
        private set

    fun enviarCodigoVerificacao(
        navegarAposEnvio: Boolean = true
    ) {

        if (enviandoCodigo) return

        val email = dadosPessoais?.email

        if (email.isNullOrBlank()) {
            erroEnvioCodigo = "E-mail não informado."
            return
        }

        viewModelScope.launch {

            enviandoCodigo = true
            erroEnvioCodigo = null
            codigoEnviado = false

            try {
                val resposta = cadastroRepository.gerarCodigoVerificacaoEmail(email)

                if (resposta.isSuccessful && resposta.body()?.get("status")?.asBoolean == true) {
                    codigoEnviado = navegarAposEnvio
                } else {
                    erroEnvioCodigo = resposta.errorBody()?.string()
                        ?.let { corpo ->
                            runCatching {
                                com.google.gson.JsonParser.parseString(corpo)
                                    .asJsonObject
                                    .get("message")
                                    ?.asString
                            }.getOrNull()
                        } ?: "Não foi possível enviar o código de verificação."
                }

            } catch (error: IOException) {
                erroEnvioCodigo = "Não foi possível conectar ao servidor."
            } catch (error: Exception) {
                erroEnvioCodigo = "Ocorreu um erro ao enviar o código."
            } finally {
                enviandoCodigo = false
            }
        }
    }

    var cadastrandoUsuario by mutableStateOf(false)
        private set

    var erroCadastro by mutableStateOf<String?>(null)
        private set

    var cadastroConcluido by mutableStateOf(false)
        private set


    fun cadastrarUsuario(codigo: String) {
        if (cadastrandoUsuario || cadastroConcluido) return

        val dados = dadosPessoais
        val senha = senhaCadastro

        val idRole = when (perfil.lowercase()) {
            "cuidador" -> 1
            "paciente" -> 2
            else -> null
        }

        if (dados == null || senha.isNullOrBlank() || idRole == null) {
            erroCadastro = "Dados do cadastro incompletos."
            return
        }

        if (!codigo.matches(Regex("^\\d{6}$"))) {
            erroCadastro = "Informe um código de 6 dígitos."
            return
        }

        val data = dados.dataNascimento.filter { it.isDigit() }

        if (data.length != 8) {
            erroCadastro = "Data de nascimento inválida."
            return
        }

        val dataFormatada =
            "${data.substring(4, 8)}-${data.substring(2, 4)}-${data.substring(0, 2)}"

        val json = JsonObject().apply {
            addProperty("nome", dados.nome.trim())
            addProperty("email", dados.email.trim())
            addProperty("senha", senha)
            addProperty("codigo", codigo)
            addProperty("data_nascimento", dataFormatada)

            if (dados.cpf.isBlank()) {
                add("cpf", com.google.gson.JsonNull.INSTANCE)
            } else {
                addProperty("cpf", dados.cpf)
            }

            if (dados.telefone.isBlank()) {
                add("telefone", com.google.gson.JsonNull.INSTANCE)
            } else {
                addProperty("telefone", dados.telefone)
            }

            add("usuario_role", JsonObject().apply {
                addProperty("id_role", idRole)
            })
        }

        viewModelScope.launch {
            cadastrandoUsuario = true
            erroCadastro = null

            try {
                val resposta = cadastroRepository.cadastrarUsuario(json)

                if (
                    resposta.isSuccessful &&
                    resposta.body()?.get("status")?.asBoolean == true
                ) {
                    cadastroConcluido = true
                } else {
                    erroCadastro = resposta.errorBody()?.string()
                        ?.let { corpo ->
                            runCatching {
                                JsonParser.parseString(corpo)
                                    .asJsonObject
                                    .get("message")
                                    ?.asString
                            }.getOrNull()
                        } ?: "Não foi possível concluir o cadastro."
                }
            } catch (error: IOException) {
                erroCadastro = "Não foi possível conectar ao servidor."
            } catch (error: Exception) {
                erroCadastro = "Ocorreu um erro ao cadastrar o usuário."
            } finally {
                cadastrandoUsuario = false
            }
        }
    }

    fun limparEstadoCadastro() {
        cadastrandoUsuario = false
        erroCadastro = null
        cadastroConcluido = false
    }
}
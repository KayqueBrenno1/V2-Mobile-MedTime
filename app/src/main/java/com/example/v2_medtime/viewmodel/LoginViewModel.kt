package com.example.v2_medtime.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.local.TokenManager
import com.example.v2_medtime.data.model.DadosPessoais
import com.example.v2_medtime.data.repository.LoginRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)

    private val repository = LoginRepository()

    var carregando by mutableStateOf(false)
        private set

    var erroLogin by mutableStateOf<String?>(null)
        private set

    var loginConcluido by mutableStateOf(false)
        private set

    var usuarioAutenticado by mutableStateOf<JsonObject?>(null)
        private set

    var cadastroGoogleNecessario by mutableStateOf(false)
        private set

    var tokenGoogleCadastro by mutableStateOf<String?>(null)
        private set

    var dadosGoogleCadastro by mutableStateOf<JsonObject?>(null)
        private set

    fun autenticarLogin(email: String, senha: String) {

        if (carregando)
            return

        viewModelScope.launch {
            carregando = true
            erroLogin = null
            loginConcluido = false
            usuarioAutenticado = null

            try {
                val response = repository.autenticarLogin(email, senha)

                if (response.isSuccessful) {
                    val usuario = response.body()

                    if (usuario != null &&
                        usuario.has("jwt") &&
                        !usuario.get("jwt").isJsonNull
                    ) {
                        val jwt = usuario.get("jwt").asString

                        if (jwt.isNotBlank() && tokenManager.salvarToken(jwt)) {
                            usuarioAutenticado = usuario
                            loginConcluido = true
                        } else {
                            erroLogin = "Não foi possível salvar sua sessão."
                        }
                    } else {
                        erroLogin = "Resposta de autenticação inválida."
                    }
                } else {
                    val mensagem = try {
                        val erro = response.errorBody()?.string()
                        val json = com.google.gson.JsonParser.parseString(erro).asJsonObject
                        json.get("message")?.asString
                    } catch (error: Exception) {
                        null
                    }

                    android.util.Log.e(
                        "MEDTIME_LOGIN",
                        "HTTP ${response.code()} - ${response.errorBody()?.string()}"
                    )

                    erroLogin = mensagem ?: when (response.code()) {
                        400 -> "Informe seu e-mail e senha."
                        401 -> "E-mail ou senha inválidos."
                        403 -> "Não foi possível acessar esta conta."
                        else -> "Não foi possível realizar o login."
                    }
                }

            } catch (error: Exception) {
                erroLogin = "Não foi possível conectar ao servidor."
            } finally {
                carregando = false
            }
        }
    }

    fun limparErro() {
        erroLogin = null
    }

    fun limparEstadoLogin() {
        erroLogin = null
        loginConcluido = false
    }

    fun autenticarGoogle(tokenGoogle: String) {
        viewModelScope.launch {
            carregando = true
            erroLogin = null
            cadastroGoogleNecessario = false

            try {
                val response = repository.autenticarGoogle(tokenGoogle)

                if (response.isSuccessful) {
                    val dados = response.body() ?.getAsJsonObject("response")

                    if (dados == null) {
                        erroLogin = "Resposta inválida do servidor."
                        return@launch
                    }

                    if (dados.get("cadastro_necessario")?.asBoolean == true) {
                        tokenGoogleCadastro = tokenGoogle
                        dadosGoogleCadastro = dados
                        cadastroGoogleNecessario = true
                        return@launch
                    }

                    val jwt = dados.get("jwt")?.asString

                    if (jwt.isNullOrBlank()) {
                        erroLogin = "O servidor não retornou um token válido."
                        return@launch
                    }

                    if (!tokenManager.salvarToken(jwt)) {
                        erroLogin = "Não foi possível salvar sua sessão."
                        return@launch
                    }

                    usuarioAutenticado = dados
                    loginConcluido = true

                } else {
                    erroLogin = when (response.code()) {
                        400 -> "Não foi possível validar sua conta Google."
                        401 -> "Autenticação Google inválida ou expirada."
                        403 -> "Sua conta não tem permissão para acessar o MedTime."
                        else -> "Erro ao entrar com Google (${response.code()})."
                    }
                }

            } catch (error: Exception) {
                android.util.Log.e(
                    "MedTimeGoogleAPI",
                    "Erro ao autenticar Google no backend",
                    error
                )

                erroLogin = "Não foi possível conectar ao servidor."
            } finally {
                carregando = false
            }
        }
    }

    fun cadastrarUsuarioGoogle(dados: DadosPessoais, idRole: Int) {
        viewModelScope.launch {
            carregando = true
            erroLogin = null

            try {
                val token = tokenGoogleCadastro

                if (token.isNullOrBlank()) {
                    erroLogin = "Sua sessão Google expirou. Entre novamente com Google."
                    return@launch
                }

                if (idRole !in listOf(1, 2)) {
                    erroLogin = "Perfil de usuário inválido."
                    return@launch
                }

                val data = dados.dataNascimento

                if (data.length != 8 || !data.all { it.isDigit() }) {
                    erroLogin = "Data de nascimento inválida."
                    return@launch
                }

                val dataFormatada = "${data.substring(4, 8)}-${data.substring(2, 4)}-${data.substring(0, 2)}"

                val response = repository.cadastrarUsuarioGoogle(
                    token = token,
                    cpf = dados.cpf,
                    telefone = dados.telefone,
                    dataNascimento = dataFormatada,
                    idRole = idRole
                )

                if (response.isSuccessful) {
                    val resultado = response.body()
                        ?.getAsJsonObject("response")

                    if (resultado == null) {
                        erroLogin = "Resposta inválida do servidor."
                        return@launch
                    }

                    var jwt = resultado.get("jwt")?.asString

                    var dadosUsuario = resultado

                    if (jwt.isNullOrBlank()) {
                        val responseLogin = repository.autenticarGoogle(token)

                        if (!responseLogin.isSuccessful) {
                            erroLogin = "Cadastro realizado, mas não foi possível iniciar a sessão (${responseLogin.code()})."
                            return@launch
                        }

                        val resultadoLogin = responseLogin.body()
                            ?.getAsJsonObject("response")

                        if (resultadoLogin == null) {
                            erroLogin = "Resposta inválida ao iniciar a sessão."
                            return@launch
                        }

                        jwt = resultadoLogin.get("jwt")?.asString
                        dadosUsuario = resultadoLogin
                    }

                    if (jwt.isNullOrBlank()) {
                        erroLogin = "Cadastro realizado, mas o servidor não retornou o token de acesso."
                        return@launch
                    }

                    if (!tokenManager.salvarToken(jwt)) {
                        erroLogin = "Não foi possível salvar sua sessão."
                        return@launch
                    }

                    usuarioAutenticado = dadosUsuario
                    tokenGoogleCadastro = null
                    dadosGoogleCadastro = null
                    cadastroGoogleNecessario = false
                    loginConcluido = true

                } else {
                    erroLogin = when (response.code()) {
                        400 -> "Verifique os dados informados."
                        401 -> "Sua autenticação Google expirou. Tente novamente."
                        409 -> "Já existe uma conta com esses dados."
                        else -> "Não foi possível concluir o cadastro (${response.code()})."
                    }
                }

            } catch (error: Exception) {
                android.util.Log.e(
                    "MedTimeGoogleCadastro",
                    "Erro ao concluir cadastro Google",
                    error
                )

                erroLogin = "Não foi possível concluir o cadastro."
            } finally {
                carregando = false
            }
        }
    }
}
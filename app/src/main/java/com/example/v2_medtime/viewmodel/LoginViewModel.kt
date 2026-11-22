package com.example.v2_medtime.viewmodel

import android.app.Application
import android.util.Base64
import com.google.gson.JsonParser
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.local.TokenManager
import com.example.v2_medtime.data.model.DadosPessoais
import com.example.v2_medtime.data.repository.LoginRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.time.Instant

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)

    private val repository = LoginRepository()

    var carregando by mutableStateOf(false)
        private set

    var erroLogin by mutableStateOf<String?>(null)
        private set

    var loginConcluido by mutableStateOf(false)
        private set

    var cuidadorAutenticado by mutableStateOf(false)
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

                        if (jwt.isNotBlank() && tokenManager.salvarSessao(jwt, usuario)) {
                            usuarioAutenticado = usuario
                            cuidadorAutenticado = obterIdPerfil(jwt) == 1
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

    private fun obterIdPerfil(jwt: String): Int? = runCatching {
        val conteudo = Base64.decode(jwt.split('.')[1], Base64.URL_SAFE)
        JsonParser.parseString(conteudo.toString(Charsets.UTF_8))
            .asJsonObject.get("role")?.asInt
    }.getOrNull()

    suspend fun restaurarSessao(): Boolean {
        carregando = true
        return try {
            val sessao = tokenManager.obterSessao()
            if (sessao == null || tokenExpirado(sessao.token)) {
                tokenManager.removerToken()
                return false
            }

            cuidadorAutenticado = obterIdPerfil(sessao.token) == 1
            usuarioAutenticado = JsonObject().apply {
                addProperty("id", sessao.idUsuario)
                addProperty("nome", sessao.nome)
                addProperty("email", sessao.email)
            }

            try {
                val resposta = repository.validarSessao(sessao.idUsuario, cuidadorAutenticado)
                if (resposta.code() == 401) {
                    tokenManager.removerToken()
                    limparEstadoSessao()
                    false
                } else {
                    loginConcluido = true
                    true
                }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (_: Exception) {
                // Sem resposta do servidor, mantém a sessão ainda não expirada.
                loginConcluido = true
                true
            }
        } finally {
            carregando = false
        }
    }

    fun limparEstadoSessao() {
        usuarioAutenticado = null
        cuidadorAutenticado = false
        loginConcluido = false
        cadastroGoogleNecessario = false
        tokenGoogleCadastro = null
        dadosGoogleCadastro = null
    }

    private fun tokenExpirado(jwt: String): Boolean = runCatching {
        val parte = jwt.split('.')[1]
        val conteudo = Base64.decode(parte, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        val claims = JsonParser.parseString(String(conteudo, Charsets.UTF_8)).asJsonObject
        val expiracao = claims.get("exp")?.takeUnless { it.isJsonNull }?.asLong
        expiracao != null && Instant.now().epochSecond >= expiracao
    }.getOrDefault(true)

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

                    if (!tokenManager.salvarSessao(jwt, dados)) {
                        erroLogin = "Não foi possível salvar sua sessão."
                        return@launch
                    }

                    usuarioAutenticado = dados
                    cuidadorAutenticado = obterIdPerfil(jwt) == 1
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

                    if (!tokenManager.salvarSessao(jwt, dadosUsuario)) {
                        erroLogin = "Não foi possível salvar sua sessão."
                        return@launch
                    }

                    usuarioAutenticado = dadosUsuario
                    cuidadorAutenticado = obterIdPerfil(jwt) == 1
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
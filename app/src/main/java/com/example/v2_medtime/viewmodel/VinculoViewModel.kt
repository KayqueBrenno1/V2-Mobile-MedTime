package com.example.v2_medtime.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.repository.VinculoRepository
import com.example.v2_medtime.notifications.VerificacaoPeriodicaWorker
import com.example.v2_medtime.screens.PacienteVinculado
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import retrofit2.Response

data class CuidadorVinculado(
    val id: Int,
    val nome: String,
    val email: String = "",
    val fotoUrl: String? = null
)

class VinculoViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = VinculoRepository()

    var carregando by mutableStateOf(false)
        private set

    var carregandoPacientes by mutableStateOf(false)
        private set

    var pacientes by mutableStateOf<List<PacienteVinculado>>(emptyList())
        private set

    var meuCuidador by mutableStateOf<CuidadorVinculado?>(null)
        private set

    var erroCodigoVinculo by mutableStateOf<String?>(null)
        private set

    var cuidadorVinculado by mutableStateOf<CuidadorVinculado?>(null)
        private set

    fun limparErroCodigoVinculo() {
        erroCodigoVinculo = null
    }

    fun limparSessao() {
        viewModelScope.coroutineContext.cancelChildren()
        pacientes = emptyList()
        cuidadorVinculado = null
        carregando = false
    }

    fun gerarCodigo(onSucesso: (String) -> Unit, onErro: (String) -> Unit) {
        executar(onErro) {
            val resposta = repositorio.gerarCodigo()
            val codigo = resposta.body()?.getAsJsonObject("response")?.get("codigo")?.asString

            if (confirmouSucesso(resposta) && codigo != null && codigo.matches(Regex("[0-9]{6}"))) {
                onSucesso(codigo)
            } else {
                onErro(mensagemErro(resposta, "Não foi possível gerar o código de vínculo."))
            }
        }
    }

    fun validarCodigo(codigo: String, onSucesso: () -> Unit, onErro: (String) -> Unit = {}) {
        erroCodigoVinculo = null
        executar({ mensagem ->
            erroCodigoVinculo = mensagem
            onErro(mensagem)
        }) {
            val resposta = repositorio.validarCodigo(codigo)
            if (confirmouSucesso(resposta)) {
                erroCodigoVinculo = null
                VerificacaoPeriodicaWorker.executarAgora(getApplication())
                onSucesso()
            } else {
                val mensagem = mensagemErro(resposta, "Código inválido ou expirado.")
                erroCodigoVinculo = mensagem
                onErro(mensagem)
            }
        }
    }

    fun carregarPacientes(onErro: (String) -> Unit) {
        if (carregandoPacientes) return

        viewModelScope.launch {
            carregandoPacientes = true
            try {
                val resposta = repositorio.listarPacientes()

                when {
                    resposta.code() == 404 -> {
                        pacientes = emptyList()
                    }
                    confirmouSucesso(resposta) -> {
                        val vinculos = resposta.body()?.getAsJsonObject("response")
                            ?.getAsJsonArray("vinculo_paciente")
                        pacientes = vinculos?.mapNotNull { registro ->
                            val dados = registro.asJsonObject
                            val idPaciente = dados.get("id_paciente")?.asInt ?: return@mapNotNull null
                            PacienteVinculado(
                                id = idPaciente,
                                nome = dados.get("nome")?.takeUnless { it.isJsonNull }?.asString
                                    ?: "Paciente #$idPaciente",
                                fotoUrl = dados.get("foto")?.takeUnless { it.isJsonNull }?.asString
                            )
                        }.orEmpty().distinctBy { it.id }
                    }
                    else -> onErro(mensagemErro(resposta, "Não foi possível carregar os pacientes."))
                }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (_: Exception) {
                onErro("Não foi possível carregar os pacientes.")
            } finally {
                carregandoPacientes = false
            }
        }
    }

    fun carregarMeuCuidador(onErro: (String) -> Unit = {}) {
        executar(onErro) {
            val resposta = repositorio.buscarMeuCuidador()
            when {
                resposta.code() == 404 -> {
                    cuidadorVinculado = null
                }
                confirmouSucesso(resposta) -> {
                    val dados = resposta.body()?.getAsJsonObject("response")
                        ?.getAsJsonArray("vinculo_paciente")
                        ?.firstOrNull()
                        ?.asJsonObject

                    val idCuidador = dados?.get("id_cuidador")?.takeUnless { it.isJsonNull }?.asInt
                    if (dados != null && idCuidador != null) {
                        val nome = listOf("nome_cuidador", "nome")
                            .firstNotNullOfOrNull { chave ->
                                dados.get(chave)?.takeUnless { it.isJsonNull }?.asString?.takeIf { it.isNotBlank() }
                            } ?: "Cuidador #$idCuidador"

                        val email = listOf("email_cuidador", "email")
                            .firstNotNullOfOrNull { chave ->
                                dados.get(chave)?.takeUnless { it.isJsonNull }?.asString?.takeIf { it.isNotBlank() }
                            }.orEmpty()

                        val foto = listOf("foto_cuidador", "foto")
                            .firstNotNullOfOrNull { chave ->
                                dados.get(chave)?.takeUnless { it.isJsonNull }?.asString?.takeIf { it.isNotBlank() }
                            }

                        cuidadorVinculado = CuidadorVinculado(
                            id = idCuidador,
                            nome = nome,
                            email = email,
                            fotoUrl = foto
                        )
                    } else {
                        cuidadorVinculado = null
                    }
                }
                else -> onErro(mensagemErro(resposta, "Não foi possível carregar os dados do cuidador."))
            }
        }
    }

    fun encerrarVinculo(
        idPaciente: Long,
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        executar(onErro) {
            repositorio.encerrarVinculo(idPaciente)
                .onSuccess {
                    cuidadorVinculado = null
                    onSucesso()
                }
                .onFailure { erro ->
                    onErro(erro.message ?: "Não foi possível encerrar o vínculo.")
                }
        }
    }

    private fun extrairObjetoCuidador(response: JsonObject?): JsonObject? {
        if (response == null) return null
        val chavesPossiveis = listOf("meu_cuidador", "cuidador", "vinculo_paciente", "vinculo")
        for (chave in chavesPossiveis) {
            val elemento: JsonElement = response.get(chave) ?: continue
            if (elemento.isJsonObject) return elemento.asJsonObject
            if (elemento.isJsonArray && elemento.asJsonArray.size() > 0) {
                val primeiro = elemento.asJsonArray[0]
                if (primeiro.isJsonObject) return primeiro.asJsonObject
            }
        }
        return if (response.has("nome") || response.has("id_cuidador")) response else null
    }

    private fun executar(onErro: (String) -> Unit, acao: suspend () -> Unit) {
        if (carregando) return

        viewModelScope.launch {
            carregando = true
            try {
                acao()
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (_: Exception) {
                onErro("Não foi possível concluir a solicitação de vínculo.")
            } finally {
                carregando = false
            }
        }
    }

    private fun confirmouSucesso(resposta: Response<JsonObject>): Boolean =
        resposta.isSuccessful && resposta.body()?.get("status")?.asBoolean == true

    private fun mensagemErro(resposta: Response<JsonObject>, mensagemPadrao: String): String {
        val corpo = resposta.errorBody()?.string()
        return runCatching {
            val dados = corpo?.let { JsonParser.parseString(it).asJsonObject } ?: resposta.body()
            dados?.get("message")?.asString
        }.getOrNull() ?: mensagemPadrao
    }
}
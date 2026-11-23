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
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import retrofit2.Response

class VinculoViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = VinculoRepository()

    var carregando by mutableStateOf(false)
        private set

    var pacientes by mutableStateOf<List<PacienteVinculado>>(emptyList())
        private set

    fun limparSessao() {
        viewModelScope.coroutineContext.cancelChildren()
        pacientes = emptyList()
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

    fun validarCodigo(codigo: String, onSucesso: () -> Unit, onErro: (String) -> Unit) {
        executar(onErro) {
            val resposta = repositorio.validarCodigo(codigo)
            if (confirmouSucesso(resposta)) {
                VerificacaoPeriodicaWorker.executarAgora(getApplication())
                onSucesso()
            } else {
                onErro(mensagemErro(resposta, "Não foi possível validar o código de vínculo."))
            }
        }
    }

    fun carregarPacientes(onErro: (String) -> Unit) {
        executar(onErro) {
            pacientes = emptyList()
            val resposta = repositorio.listarPacientes()

            when {
                resposta.code() == 404 -> Unit
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
        }
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
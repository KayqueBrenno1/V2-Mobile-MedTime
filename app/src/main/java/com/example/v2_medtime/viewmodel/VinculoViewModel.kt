package com.example.v2_medtime.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.remote.confirmouSucesso
import com.example.v2_medtime.data.remote.mensagemDeErro
import com.example.v2_medtime.data.remote.texto
import com.example.v2_medtime.data.repository.VinculoRepository
import com.example.v2_medtime.screens.PacienteVinculado
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

data class CuidadorVinculado(
    val id: Int,
    val idPaciente: Long = 0L,
    val nome: String,
    val email: String? = null,
    val fotoUrl: String? = null
)

class VinculoViewModel(
    private val repositorio: VinculoRepository = VinculoRepository()
) : ViewModel() {

    var carregando by mutableStateOf(false)
        private set

    var pacientes by mutableStateOf<List<PacienteVinculado>>(emptyList())
        private set

    var meuCuidador by mutableStateOf<CuidadorVinculado?>(null)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    fun limparSessao() {
        viewModelScope.coroutineContext.cancelChildren()
        pacientes = emptyList()
        meuCuidador = null
        carregando = false
        isRefreshing = false
    }

    fun carregarMeuCuidador(onErro: (String) -> Unit = {}) {
        executar(onErro) {
            val resposta = repositorio.buscarMeuCuidador()
            when {
                resposta.code() == 404 -> {
                    meuCuidador = null
                }

                resposta.confirmouSucesso() -> {
                    val responseObj = resposta.body()?.getAsJsonObject("response")
                    val vinculoElement = responseObj?.get("vinculo_paciente")

                    val dados: JsonObject? = when {
                        vinculoElement != null && vinculoElement.isJsonArray ->
                            vinculoElement.asJsonArray.firstOrNull()?.takeIf { it.isJsonObject }?.asJsonObject
                        vinculoElement != null && vinculoElement.isJsonObject ->
                            vinculoElement.asJsonObject
                        else -> null
                    }

                    if (dados != null) {
                        val idCuidador = dados.texto("id_cuidador")?.toIntOrNull()
                            ?: dados.texto("id")?.toIntOrNull()
                            ?: 0
                        val idPaciente = dados.texto("id_paciente")?.toLongOrNull() ?: 0L
                        val nome = dados.texto("nome_cuidador")
                            ?.takeIf { it.isNotBlank() }
                            ?: dados.texto("nome")?.takeIf { it.isNotBlank() }
                            ?: "Cuidador"
                        val email = dados.texto("email_cuidador")
                            ?.takeIf { it.isNotBlank() }
                            ?: dados.texto("email")?.takeIf { it.isNotBlank() }
                        val foto = dados.texto("foto_cuidador")
                            ?.takeIf { it.isNotBlank() }
                            ?: dados.texto("foto")?.takeIf { it.isNotBlank() }

                        meuCuidador = CuidadorVinculado(
                            id = idCuidador,
                            idPaciente = idPaciente,
                            nome = nome,
                            email = email,
                            fotoUrl = foto
                        )
                    } else {
                        meuCuidador = null
                    }
                }

                else -> {
                    onErro(resposta.mensagemDeErro("Não foi possível carregar os dados do cuidador."))
                }
            }
        }
    }

    fun carregarPacientes(
        onErro: (String) -> Unit = {},
        silencioso: Boolean = false
    ) {
        executar(onErro = onErro, silencioso = silencioso) {
            buscarPacientes(onErro)
        }
    }

    fun atualizarPacientes(onErro: (String) -> Unit = {}) {
        if (carregando || isRefreshing) return
        viewModelScope.launch {
            isRefreshing = true
            try {
                buscarPacientes(onErro)
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                onErro(erro.message ?: "Erro de comunicação com o servidor.")
            } finally {
                isRefreshing = false
            }
        }
    }

    private suspend fun buscarPacientes(onErro: (String) -> Unit) {
        val resposta = repositorio.listarPacientes()
        when {
            resposta.code() == 404 -> {
                pacientes = emptyList()
            }

            resposta.confirmouSucesso() -> {
                val lista = resposta.body()?.getAsJsonObject("response")
                    ?.getAsJsonArray("vinculo_paciente")
                    ?.mapNotNull { elem ->
                        val obj = elem.takeIf { it.isJsonObject }?.asJsonObject ?: return@mapNotNull null
                        val id = obj.texto("id_paciente")?.toIntOrNull() ?: return@mapNotNull null
                        val nome = obj.texto("nome_paciente")
                            ?.takeIf { it.isNotBlank() }
                            ?: obj.texto("nome")?.takeIf { it.isNotBlank() }
                            ?: "Paciente #$id"
                        val foto = obj.texto("foto_paciente")
                            ?.takeIf { it.isNotBlank() }
                            ?: obj.texto("foto")?.takeIf { it.isNotBlank() }

                        PacienteVinculado(
                            id = id,
                            nome = nome,
                            fotoUrl = foto
                        )
                    }.orEmpty()

                pacientes = lista
            }

            else -> {
                onErro(resposta.mensagemDeErro("Não foi possível carregar os pacientes."))
            }
        }
    }

    fun gerarCodigo(
        onSucesso: (String) -> Unit,
        onErro: (String) -> Unit
    ) {
        executar(onErro) {
            val resposta = repositorio.gerarCodigo()
            if (resposta.confirmouSucesso()) {
                val codigo = resposta.body()?.getAsJsonObject("response")?.texto("codigo").orEmpty()
                if (codigo.isNotBlank()) {
                    onSucesso(codigo)
                } else {
                    onErro("Código não retornado pelo servidor.")
                }
            } else {
                onErro(resposta.mensagemDeErro("Não foi possível gerar o código de vínculo."))
            }
        }
    }

    fun validarCodigo(
        codigo: String,
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        executar(onErro) {
            val resposta = repositorio.validarCodigo(codigo)
            if (resposta.confirmouSucesso()) {
                onSucesso()
            } else {
                onErro(resposta.mensagemDeErro("Código inválido ou expirado."))
            }
        }
    }

    fun encerrarVinculo(
        idPaciente: Long,
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        val pacienteId = idPaciente.takeIf { it > 0L } ?: meuCuidador?.idPaciente ?: 0L
        executar(onErro) {
            repositorio.encerrarVinculo(pacienteId)
                .onSuccess {
                    meuCuidador = null
                    onSucesso()
                }
                .onFailure { erro ->
                    onErro(erro.message ?: "Não foi possível encerrar o vínculo.")
                }
        }
    }

    private fun executar(
        onErro: (String) -> Unit,
        silencioso: Boolean = false,
        bloco: suspend () -> Unit
    ) {
        viewModelScope.launch {
            if (!silencioso) carregando = true
            try {
                bloco()
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                onErro(erro.message ?: "Erro de comunicação com o servidor.")
            } finally {
                if (!silencioso) carregando = false
            }
        }
    }
}
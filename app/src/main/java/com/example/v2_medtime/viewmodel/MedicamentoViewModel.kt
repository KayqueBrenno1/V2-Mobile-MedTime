package com.example.v2_medtime.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.model.MedicamentoRequest
import com.example.v2_medtime.data.repository.FakeMedicamentoRepository
import com.example.v2_medtime.data.repository.MedicamentoRepository
import kotlinx.coroutines.launch
import java.io.File

enum class StatusMedicamento(val label: String) {
    ATIVO("Ativos"),
    PAUSADO("Pausados"),
    CONCLUIDO("Concluídos")
}

data class MedicamentoCadastrado(
    val id: Long = System.nanoTime(),
    val dados: MedicamentoRequest,
    val imagemUri: Uri? = null,
    val imagemUrlRemota: String? = null, // Pronto para receber a URL da foto vinda da API
    val status: StatusMedicamento = StatusMedicamento.ATIVO
)

class MedicamentoViewModel(
    private val repository: MedicamentoRepository = FakeMedicamentoRepository()
) : ViewModel() {

    var ultimoMedicamento by mutableStateOf<MedicamentoCadastrado?>(null)
        private set

    val listaMedicamentos = mutableStateListOf<MedicamentoCadastrado>()

    var isLoading by mutableStateOf(false)
        private set

    var erroMensagem by mutableStateOf<String?>(null)
        private set

    init {
        carregarMedicamentos()
    }

    fun carregarMedicamentos() {
        viewModelScope.launch {
            isLoading = true
            erroMensagem = null
            repository.listarMedicamentos()
                .onSuccess { itens ->
                    listaMedicamentos.clear()
                    listaMedicamentos.addAll(itens)
                }
                .onFailure { erro ->
                    erroMensagem = erro.message ?: "Erro ao carregar medicamentos"
                }
            isLoading = false
        }
    }

    fun buscarPorId(id: Long): MedicamentoCadastrado? {
        return listaMedicamentos.find { it.id == id }
    }

    fun registrarMedicamento(
        request: MedicamentoRequest,
        imagemFile: File? = null,
        imagemUri: Uri? = null,
        onSucesso: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            erroMensagem = null
            repository.cadastrarMedicamento(request, imagemFile, imagemUri)
                .onSuccess { novoItem ->
                    ultimoMedicamento = novoItem
                    listaMedicamentos.add(0, novoItem)
                    onSucesso()
                }
                .onFailure { erro ->
                    erroMensagem = erro.message ?: "Erro ao cadastrar medicamento"
                }
            isLoading = false
        }
    }

    fun atualizarMedicamento(
        id: Long,
        request: MedicamentoRequest,
        imagemFile: File? = null,
        imagemUri: Uri? = null,
        onSucesso: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            erroMensagem = null
            repository.atualizarMedicamento(id, request, imagemFile, imagemUri)
                .onSuccess { itemAtualizado ->
                    val index = listaMedicamentos.indexOfFirst { it.id == id }
                    if (index != -1) {
                        listaMedicamentos[index] = itemAtualizado
                    }
                    onSucesso()
                }
                .onFailure { erro ->
                    erroMensagem = erro.message ?: "Erro ao atualizar medicamento"
                }
            isLoading = false
        }
    }

    fun atualizarEstoque(
        id: Long,
        novaQuantidade: Int,
        onSucesso: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            erroMensagem = null
            repository.atualizarEstoque(id, novaQuantidade)
                .onSuccess {
                    val index = listaMedicamentos.indexOfFirst { it.id == id }
                    if (index != -1) {
                        val atual = listaMedicamentos[index]
                        listaMedicamentos[index] = atual.copy(
                            dados = atual.dados.copy(
                                quantidadeTotal = novaQuantidade.coerceAtLeast(0).toString()
                            )
                        )
                    }
                    onSucesso()
                }
                .onFailure { erro ->
                    erroMensagem = erro.message ?: "Erro ao atualizar estoque"
                }
            isLoading = false
        }
    }

    fun excluirMedicamento(
        id: Long,
        onSucesso: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            erroMensagem = null
            repository.excluirMedicamento(id)
                .onSuccess {
                    listaMedicamentos.removeAll { it.id == id }
                    onSucesso()
                }
                .onFailure { erro ->
                    erroMensagem = erro.message ?: "Erro ao excluir medicamento"
                }
            isLoading = false
        }
    }
}
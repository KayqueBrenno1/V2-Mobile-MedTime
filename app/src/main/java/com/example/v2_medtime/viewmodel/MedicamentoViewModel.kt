package com.example.v2_medtime.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.v2_medtime.data.model.MedicamentoRequest
import com.example.v2_medtime.data.repository.ApiMedicamentoRepository
import com.example.v2_medtime.data.repository.MedicamentoRepository
import com.example.v2_medtime.notifications.VerificacaoPeriodicaWorker
import kotlinx.coroutines.cancelChildren
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
    val imagemUrlRemota: String? = null,
    val status: StatusMedicamento = StatusMedicamento.ATIVO
)

data class ItemOcrIdentificado(
    val idTemporario: Long = System.nanoTime(),
    val dados: MedicamentoRequest,
    val selecionado: Boolean = true
)

class MedicamentoViewModel(
    application: Application,
    private val repository: MedicamentoRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        ApiMedicamentoRepository(application.contentResolver, application.cacheDir)
    )

    var ultimoMedicamento by mutableStateOf<MedicamentoCadastrado?>(null)
        private set

    val listaMedicamentos = mutableStateListOf<MedicamentoCadastrado>()

    var isLoading by mutableStateOf(false)
        private set

    private var carregandoLista = false

    var erroMensagem by mutableStateOf<String?>(null)
        private set

    // ==========================================================
    // ESTADOS DO FLUXO DE OCR + IA
    // ==========================================================
    var isEscaneandoOcr by mutableStateOf(false)
        private set

    val remediosIdentificadosOcr = mutableStateListOf<ItemOcrIdentificado>()

    fun iniciarSessao(idUsuario: Long) {
        viewModelScope.coroutineContext.cancelChildren()
        (repository as? ApiMedicamentoRepository)?.idUsuario = idUsuario
        listaMedicamentos.clear()
        remediosIdentificadosOcr.clear()
        ultimoMedicamento = null
        erroMensagem = null
        isLoading = false
        isEscaneandoOcr = false

        if (idUsuario > 0L) carregarMedicamentos()
    }

    fun limparErro() {
        erroMensagem = null
    }

    fun carregarMedicamentos() {
        if (carregandoLista) return

        viewModelScope.launch {
            if (carregandoLista) return@launch
            carregandoLista = true
            isLoading = true
            erroMensagem = null
            try {
                repository.listarMedicamentos()
                    .onSuccess { itens ->
                        listaMedicamentos.clear()
                        listaMedicamentos.addAll(itens)
                    }
                    .onFailure { erro ->
                        erroMensagem = erro.message ?: "Erro ao carregar medicamentos"
                    }
            } finally {
                carregandoLista = false
                isLoading = false
            }
        }
    }

    fun buscarPorId(id: Long): MedicamentoCadastrado? {
        return listaMedicamentos.find { it.id == id }
    }

    fun recarregarMedicamento(id: Long) {
        if (id <= 0L) return
        carregarMedicamentos()
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
                    VerificacaoPeriodicaWorker.executarAgora(getApplication())
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
            val imagemAtual = listaMedicamentos.firstOrNull { it.id == id }?.imagemUri
            val novaImagemUri = imagemUri?.takeUnless { it == imagemAtual }
            repository.atualizarMedicamento(id, request, imagemFile, novaImagemUri)
                .onSuccess { itemAtualizado ->
                    val index = listaMedicamentos.indexOfFirst { it.id == id }
                    if (index != -1) {
                        listaMedicamentos[index] = itemAtualizado
                    }
                    VerificacaoPeriodicaWorker.executarAgora(getApplication())
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
                    VerificacaoPeriodicaWorker.executarAgora(getApplication())
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
                    VerificacaoPeriodicaWorker.executarAgora(getApplication())
                    onSucesso()
                }
                .onFailure { erro ->
                    erroMensagem = erro.message ?: "Erro ao excluir medicamento"
                }
            isLoading = false
        }
    }

    // ==========================================================
    // MÉTODOS DO FLUXO DE OCR + IA (MÚLTIPLAS RECEITAS E EDIÇÃO)
    // ==========================================================
    fun processarReceitaOcr(
        imagemReceita: File,
        limparAnteriores: Boolean = false,
        onSucesso: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isEscaneandoOcr = true
            erroMensagem = null
            repository.escanearReceitaOcr(imagemReceita)
                .onSuccess { listaExtraida ->
                    if (limparAnteriores) {
                        remediosIdentificadosOcr.clear()
                    }
                    listaExtraida.forEach { request ->
                        remediosIdentificadosOcr.add(
                            ItemOcrIdentificado(dados = request, selecionado = true)
                        )
                    }
                    onSucesso()
                }
                .onFailure { erro ->
                    erroMensagem = erro.message ?: "Não foi possível ler a receita"
                }
            isEscaneandoOcr = false
        }
    }

    fun alternarSelecaoOcr(idTemporario: Long) {
        val index = remediosIdentificadosOcr.indexOfFirst { it.idTemporario == idTemporario }
        if (index != -1) {
            val atual = remediosIdentificadosOcr[index]
            remediosIdentificadosOcr[index] = atual.copy(selecionado = !atual.selecionado)
        }
    }

    fun buscarItemOcrPorId(idTemporario: Long): ItemOcrIdentificado? {
        return remediosIdentificadosOcr.find { it.idTemporario == idTemporario }
    }

    fun atualizarItemOcr(idTemporario: Long, novosDados: MedicamentoRequest) {
        val index = remediosIdentificadosOcr.indexOfFirst { it.idTemporario == idTemporario }
        if (index != -1) {
            val atual = remediosIdentificadosOcr[index]
            remediosIdentificadosOcr[index] = atual.copy(dados = novosDados)
        }
    }

    fun cadastrarSelecionadosOcr(onConcluido: () -> Unit) {
        val selecionados = remediosIdentificadosOcr.filter { it.selecionado }
        if (selecionados.isEmpty() || isLoading) return

        viewModelScope.launch {
            isLoading = true
            erroMensagem = null
            try {
                for (item in selecionados) {
                    val resultado = repository.cadastrarMedicamento(item.dados, null, null)
                    val novoItem = resultado.getOrNull()
                    if (novoItem == null) {
                        erroMensagem = resultado.exceptionOrNull()?.message
                            ?: "Não foi possível cadastrar um medicamento da receita."
                        return@launch
                    }

                    ultimoMedicamento = novoItem
                    listaMedicamentos.add(0, novoItem)
                    remediosIdentificadosOcr.remove(item)
                }
                VerificacaoPeriodicaWorker.executarAgora(getApplication())
                onConcluido()
            } finally {
                isLoading = false
            }
        }
    }
}
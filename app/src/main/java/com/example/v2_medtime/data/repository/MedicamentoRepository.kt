package com.example.v2_medtime.data.repository

import android.net.Uri
import com.example.v2_medtime.data.model.MedicamentoRequest
import com.example.v2_medtime.viewmodel.MedicamentoCadastrado
import com.example.v2_medtime.viewmodel.StatusMedicamento
import java.io.File

interface MedicamentoRepository {
    suspend fun listarMedicamentos(): Result<List<MedicamentoCadastrado>>
    suspend fun cadastrarMedicamento(
        request: MedicamentoRequest,
        imagemFile: File?,
        imagemUriLocal: Uri?
    ): Result<MedicamentoCadastrado>
    suspend fun atualizarMedicamento(
        id: Long,
        request: MedicamentoRequest,
        imagemFile: File?,
        imagemUriLocal: Uri?
    ): Result<MedicamentoCadastrado>
    suspend fun atualizarEstoque(id: Long, novaQuantidade: Int): Result<Unit>
    suspend fun excluirMedicamento(id: Long): Result<Unit>
}

/**
 * Implementação local em memória para desenvolvimento de UI e fluxo offline.
 * Quando a API estiver pronta, basta criar ApiMedicamentoRepository implementando
 * MedicamentoRepository e injetar no ViewModel.
 */
class FakeMedicamentoRepository : MedicamentoRepository {

    private val bancoLocal = mutableListOf(
        MedicamentoCadastrado(
            id = 1L,
            dados = MedicamentoRequest(
                nome = "Losartana",
                concentracao = "50",
                unidadeMedida = "mg",
                dose = "1",
                tipoMedicamento = "Comprimido",
                frequencia = "8",
                intervaloDosagem = "hora",
                horarioInicial = "08:00",
                quantidadeTotal = "20"
            ),
            status = StatusMedicamento.ATIVO
        ),
        MedicamentoCadastrado(
            id = 2L,
            dados = MedicamentoRequest(
                nome = "Metformina",
                concentracao = "850",
                unidadeMedida = "mg",
                dose = "1",
                tipoMedicamento = "Comprimido",
                frequencia = "12",
                intervaloDosagem = "hora",
                horarioInicial = "08:00",
                quantidadeTotal = "30"
            ),
            status = StatusMedicamento.ATIVO
        ),
        MedicamentoCadastrado(
            id = 3L,
            dados = MedicamentoRequest(
                nome = "Omeprazol",
                concentracao = "20",
                unidadeMedida = "mg",
                dose = "1",
                tipoMedicamento = "Cápsula",
                frequencia = "24",
                intervaloDosagem = "hora",
                horarioInicial = "07:00",
                quantidadeTotal = "14"
            ),
            status = StatusMedicamento.ATIVO
        )
    )

    override suspend fun listarMedicamentos(): Result<List<MedicamentoCadastrado>> {
        return Result.success(bancoLocal.toList())
    }

    override suspend fun cadastrarMedicamento(
        request: MedicamentoRequest,
        imagemFile: File?,
        imagemUriLocal: Uri?
    ): Result<MedicamentoCadastrado> {
        val novoItem = MedicamentoCadastrado(
            id = System.nanoTime(),
            dados = request,
            imagemUri = imagemUriLocal,
            status = StatusMedicamento.ATIVO
        )
        bancoLocal.add(0, novoItem)
        return Result.success(novoItem)
    }

    override suspend fun atualizarMedicamento(
        id: Long,
        request: MedicamentoRequest,
        imagemFile: File?,
        imagemUriLocal: Uri?
    ): Result<MedicamentoCadastrado> {
        val index = bancoLocal.indexOfFirst { it.id == id }
        if (index == -1) return Result.failure(Exception("Medicamento não encontrado"))

        val atualizado = bancoLocal[index].copy(
            dados = request,
            imagemUri = imagemUriLocal ?: bancoLocal[index].imagemUri
        )
        bancoLocal[index] = atualizado
        return Result.success(atualizado)
    }

    override suspend fun atualizarEstoque(id: Long, novaQuantidade: Int): Result<Unit> {
        val index = bancoLocal.indexOfFirst { it.id == id }
        if (index == -1) return Result.failure(Exception("Medicamento não encontrado"))

        val atual = bancoLocal[index]
        bancoLocal[index] = atual.copy(
            dados = atual.dados.copy(quantidadeTotal = novaQuantidade.coerceAtLeast(0).toString())
        )
        return Result.success(Unit)
    }

    override suspend fun excluirMedicamento(id: Long): Result<Unit> {
        bancoLocal.removeAll { it.id == id }
        return Result.success(Unit)
    }
}
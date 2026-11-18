package com.example.v2_medtime.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.v2_medtime.data.model.MedicamentoRequest
enum class StatusMedicamento(val label: String) {
    ATIVO("Ativos"),
    PAUSADO("Pausados"),
    CONCLUIDO("Concluídos")
}

data class MedicamentoCadastrado(
    val id: Long = System.nanoTime(),
    val dados: MedicamentoRequest,
    val imagemUri: Uri? = null,
    val status: StatusMedicamento = StatusMedicamento.ATIVO
)

class MedicamentoViewModel : ViewModel() {
    var ultimoMedicamento by mutableStateOf<MedicamentoCadastrado?>(null)
        private set

    val listaMedicamentos = mutableStateListOf(
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

    fun registrarMedicamento(request: MedicamentoRequest, imagemUri: Uri?) {
        val novoItem = MedicamentoCadastrado(
            dados = request,
            imagemUri = imagemUri,
            status = StatusMedicamento.ATIVO
        )
        ultimoMedicamento = novoItem
        listaMedicamentos.add(0, novoItem)
    }
}
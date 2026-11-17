package com.example.v2_medtime.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.v2_medtime.model.MedicamentoRequest

data class MedicamentoCadastrado(
    val dados: MedicamentoRequest,
    val imagemUri: Uri? = null
)

class MedicamentoViewModel : ViewModel() {
    var ultimoMedicamento by mutableStateOf<MedicamentoCadastrado?>(null)
        private set

    val listaMedicamentos = mutableStateListOf<MedicamentoCadastrado>()

    fun registrarMedicamento(request: MedicamentoRequest, imagemUri: Uri?) {
        val novoItem = MedicamentoCadastrado(dados = request, imagemUri = imagemUri)
        ultimoMedicamento = novoItem
        listaMedicamentos.add(0, novoItem)
    }
}
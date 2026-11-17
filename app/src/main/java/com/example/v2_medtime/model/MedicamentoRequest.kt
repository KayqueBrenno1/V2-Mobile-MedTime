package com.example.v2_medtime.model

data class MedicamentoRequest (

    val nome: String = "",
    val dose: String = "",
    val unidadeDose: String = "",
    val tipoMedicamento: String = "",
    val concentracao: String = "",
    val intervaloDosagem: String = "",
    val horarioInicial: String = "",
    val quantidadeTotal: String = "",
    val descricao: String = "",
    val frequencia: String = "",
    val unidadeMedida: String = ""
)

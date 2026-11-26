package com.example.v2_medtime.data.model

data class AlarmeMedicamento(
    val notificacaoId: Int = 0,
    val idReceita: Long = 0L,
    val nomeMedicamento: String = "",
    val dosagemTexto: String = "",
    val horarioFormatado: String = "00:00",
    val horarioProgramadoIso: String = "",
    val imagemUrl: String? = null
)
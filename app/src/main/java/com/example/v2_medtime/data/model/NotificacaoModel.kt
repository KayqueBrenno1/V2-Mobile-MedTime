package com.example.v2_medtime.data.model

import java.time.Instant

data class DoseAgendadaNotificacao(
    val notificacaoId: Int,
    val idReceita: Long,
    val nomeMedicamento: String,
    val dosagemTexto: String,
    val horarioFormatado: String,
    val horarioProgramadoIso: String,
    val horarioProgramado: Instant,
    val imagemUrl: String? = null
)

enum class TipoAlertaNotificacao {
    DOSE_ATRASADA,
    ESTOQUE_BAIXO,
    NOVO_VINCULO
}

data class AlertaNotificacaoItem(
    val chaveUnica: String,
    val notificacaoId: Int,
    val tipo: TipoAlertaNotificacao,
    val titulo: String,
    val mensagem: String,
    val prioridadeAlta: Boolean = false
)
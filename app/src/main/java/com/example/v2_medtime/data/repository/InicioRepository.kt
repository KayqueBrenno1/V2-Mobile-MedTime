package com.example.v2_medtime.data.repository

import com.example.v2_medtime.data.model.DiaCalendarioItem
import com.example.v2_medtime.data.model.EstoqueResumoItem
import com.example.v2_medtime.data.model.ProximoMedicamentoItem
import com.example.v2_medtime.data.model.StatusDoseHoje
import com.example.v2_medtime.data.remote.RetrofitClient
import com.example.v2_medtime.data.remote.texto
import com.google.gson.JsonObject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

interface InicioRepository {
    suspend fun obterDiasCalendario(): Result<List<DiaCalendarioItem>>
    suspend fun obterProximosMedicamentos(diaId: String): Result<List<ProximoMedicamentoItem>>
    suspend fun obterResumoEstoque(): Result<List<EstoqueResumoItem>>
    suspend fun registrarDoseTomada(doseId: Long): Result<Unit>
}

class ApiInicioRepository : InicioRepository {

    var idUsuario: Long = 0L
    private val api get() = RetrofitClient.api
    private val dosesPorId = mutableMapOf<Long, Pair<Long, String>>()
    private var proximoIdDose = 0L

    override suspend fun obterDiasCalendario(): Result<List<DiaCalendarioItem>> {
        val hoje = LocalDate.now()
        val idioma = Locale.forLanguageTag("pt-BR")
        return Result.success((-2L..2L).map { deslocamento ->
            val dia = hoje.plusDays(deslocamento)
            DiaCalendarioItem(
                id = dia.toString(),
                diaSemana = dia.dayOfWeek.getDisplayName(TextStyle.SHORT, idioma)
                    .trimEnd('.').replaceFirstChar { it.uppercase() },
                diaNumero = dia.dayOfMonth.toString(),
                isHoje = dia == hoje
            )
        })
    }

    override suspend fun obterProximosMedicamentos(diaId: String) = requisitar {
        val fuso = ZoneId.systemDefault()
        val resposta = api.consultarAgenda(idUsuario, diaId, fuso.id)
        val agenda = resposta.objetos("agenda", "Não foi possível carregar a agenda.")
        dosesPorId.clear()

        agenda.mapNotNull { dose ->
            val idReceita = dose.texto("id_receita_medica")?.toLongOrNull() ?: return@mapNotNull null
            val horario = dose.texto("horario_programado") ?: return@mapNotNull null
            val instante = Instant.parse(horario)
            val idDose = ++proximoIdDose
            dosesPorId[idDose] = idReceita to horario

            ProximoMedicamentoItem(
                id = idDose,
                medicamentoId = idReceita,
                nome = dose.texto("nome_medicamento").orEmpty(),
                concentracao = dose.texto("concentracao").orEmpty(),
                unidadeMedida = dose.texto("unidade_concentracao").orEmpty(),
                dosagemTexto = listOfNotNull(dose.texto("dosagem"), dose.texto("tipo_dosagem")).joinToString(" "),
                horario = instante.atZone(fuso).format(DateTimeFormatter.ofPattern("HH:mm")),
                status = when (dose.texto("status")) {
                    "TOMADO" -> StatusDoseHoje.TOMADO
                    "ATRASADO" -> StatusDoseHoje.ATRASADO
                    else -> StatusDoseHoje.PENDENTE
                }
            )
        }
    }

    override suspend fun obterResumoEstoque() = requisitar {
        val resposta = api.consultarEstoque(idUsuario)
        if (resposta.code() == 404) {
            emptyList()
        } else {
            val receitas = api.listarReceitas(idUsuario)
            val idsReceitas = if (receitas.code() == 404) {
                emptyMap()
            } else {
                receitas.objetos("receita_medica", "Não foi possível localizar as receitas.")
                    .associate { it.texto("id_medicamento") to it.texto("id_receita_medica")?.toLongOrNull() }
            }
            resposta.objetos("estoques", "Não foi possível carregar o estoque.").mapNotNull { estoque ->
                val idReceita = idsReceitas[estoque.texto("id_medicamento")] ?: return@mapNotNull null
                val quantidade = estoque.texto("quantidade_em_estoque")?.toBigDecimalOrNull()?.toInt() ?: 0
                EstoqueResumoItem(
                    medicamentoId = idReceita,
                    nome = estoque.texto("nome_medicamento").orEmpty(),
                    atual = quantidade,
                    total = 0,
                    unidade = estoque.texto("tipo_dosagem").orEmpty()
                )
            }
        }
    }

    override suspend fun registrarDoseTomada(doseId: Long) = requisitar {
        val (idReceita, horario) = dosesPorId[doseId] ?: error("Atualize a agenda antes de registrar a dose.")
        val dados = JsonObject().apply { addProperty("horario_programado", horario) }
        api.confirmarIngestao(idReceita, dados).exigirSucesso("Não foi possível registrar a dose.")
    }
}

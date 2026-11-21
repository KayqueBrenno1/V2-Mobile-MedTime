package com.example.v2_medtime.data.repository

import com.example.v2_medtime.data.model.HistoricoItem
import com.example.v2_medtime.data.model.StatusHistorico
import com.example.v2_medtime.data.remote.RetrofitClient
import com.example.v2_medtime.data.remote.texto
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

interface HistoricoRepository {
    suspend fun listarHistorico(): Result<List<HistoricoItem>>
}

class ApiHistoricoRepository : HistoricoRepository {

    var idUsuario: Long = 0L

    override suspend fun listarHistorico() = requisitar {
        val resposta = RetrofitClient.api.consultarHistorico(idUsuario)
        if (resposta.code() == 404) {
            emptyList()
        } else {
            resposta.objetos("historico_medicamento", "Não foi possível carregar o histórico.")
                .mapIndexedNotNull { indice, registro ->
                    val id = registro.texto("id_historico_medicamento")?.toLongOrNull()
                        ?: registro.texto("id")?.toLongOrNull() ?: return@mapIndexedNotNull null
                    val instante = registro.texto("hora_ingestao")?.let(Instant::parse)
                    val horario = instante?.atZone(ZoneId.systemDefault())
                    HistoricoItem(
                        id = id,
                        medicamentoId = registro.texto("id_receita_medica")?.toLongOrNull() ?: 0L,
                        nomeMedicamento = registro.texto("nome_medicamento").orEmpty(),
                        concentracao = registro.texto("concentracao").orEmpty(),
                        unidadeMedida = registro.texto("unidade_concentracao").orEmpty(),
                        dataLabel = horario?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).orEmpty(),
                        horario = horario?.format(DateTimeFormatter.ofPattern("HH:mm")).orEmpty(),
                        status = StatusHistorico.TOMADO,
                        colorIndex = indice
                    )
                }
        }
    }
}

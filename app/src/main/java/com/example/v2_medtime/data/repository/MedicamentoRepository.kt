package com.example.v2_medtime.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.v2_medtime.data.model.MedicamentoRequest
import com.example.v2_medtime.data.remote.RetrofitClient
import com.example.v2_medtime.data.remote.confirmouSucesso
import com.example.v2_medtime.data.remote.mensagemDeErro
import com.example.v2_medtime.data.remote.texto
import com.example.v2_medtime.viewmodel.MedicamentoCadastrado
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import java.text.Normalizer
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
    suspend fun escanearReceitaOcr(imagemReceita: File): Result<List<MedicamentoRequest>>
}

class ApiMedicamentoRepository(
    contentResolver: ContentResolver,
    private val cacheDir: File
) : MedicamentoRepository {

    var idUsuario: Long = 0L
    private val api get() = RetrofitClient.api
    private val cloudinaryRepository = CloudinaryRepository(contentResolver, cacheDir)

    private data class ProgramacaoSalva(
        val frequencia: String = "",
        val intervalo: String = "",
        val horarioInicial: String = ""
    )

    private fun arquivoCacheProgramacoes(): File =
        File(cacheDir, "medtime_programacoes_receitas.json")

    private fun lerCacheProgramacoes(): MutableMap<Long, ProgramacaoSalva> = runCatching {
        val arquivo = arquivoCacheProgramacoes()
        if (!arquivo.exists()) return@runCatching mutableMapOf()
        val root = JsonParser.parseString(arquivo.readText()).asJsonObject
        val mapa = mutableMapOf<Long, ProgramacaoSalva>()
        for ((chave, valor) in root.entrySet()) {
            val id = chave.toLongOrNull() ?: continue
            val obj = valor.asJsonObject
            mapa[id] = ProgramacaoSalva(
                frequencia = obj.texto("frequencia").orEmpty(),
                intervalo = obj.texto("intervalo").orEmpty(),
                horarioInicial = obj.texto("horarioInicial").orEmpty()
            )
        }
        mapa
    }.getOrDefault(mutableMapOf())

    private fun salvarProgramacaoCache(idReceita: Long, request: MedicamentoRequest) {
        if (idReceita <= 0L) return
        if (request.frequencia.isBlank() && request.intervaloDosagem.isBlank() && request.horarioInicial.isBlank()) return
        runCatching {
            val mapa = lerCacheProgramacoes()
            val anterior = mapa[idReceita]
            mapa[idReceita] = ProgramacaoSalva(
                frequencia = request.frequencia.ifBlank { anterior?.frequencia.orEmpty() },
                intervalo = request.intervaloDosagem.ifBlank { anterior?.intervalo.orEmpty() },
                horarioInicial = request.horarioInicial.ifBlank { anterior?.horarioInicial.orEmpty() }
            )
            val root = JsonObject()
            mapa.forEach { (id, prog) ->
                root.add(id.toString(), JsonObject().apply {
                    addProperty("frequencia", prog.frequencia)
                    addProperty("intervalo", prog.intervalo)
                    addProperty("horarioInicial", prog.horarioInicial)
                })
            }
            arquivoCacheProgramacoes().writeText(root.toString())
        }
    }

    private fun removerProgramacaoCache(idReceita: Long) {
        runCatching {
            val mapa = lerCacheProgramacoes()
            if (mapa.remove(idReceita) != null) {
                val root = JsonObject()
                mapa.forEach { (id, prog) ->
                    root.add(id.toString(), JsonObject().apply {
                        addProperty("frequencia", prog.frequencia)
                        addProperty("intervalo", prog.intervalo)
                        addProperty("horarioInicial", prog.horarioInicial)
                    })
                }
                arquivoCacheProgramacoes().writeText(root.toString())
            }
        }
    }

    override suspend fun listarMedicamentos() = requisitar {
        val usuario = usuarioDaSessao()
        val resposta = api.listarReceitas(usuario)
        if (resposta.code() == 404) {
            emptyList()
        } else {
            val receitas = resposta.objetos("receita_medica", "Não foi possível carregar os medicamentos.")
            val unidades = runCatching {
                api.listarUnidadesMedida().objetos("unidade_medida", "Não foi possível carregar as unidades.")
            }.getOrDefault(emptyList())
            val tipos = runCatching {
                api.listarTiposDosagem().objetos("tipo_dosagem", "Não foi possível carregar os tipos de dosagem.")
            }.getOrDefault(emptyList())
            val intervalos = runCatching {
                api.listarTiposIntervalo().objetos("tipoIntervalo", "Não foi possível carregar os intervalos.")
            }.getOrDefault(emptyList())

            val programacoesAgenda = carregarProgramacoesDaAgenda(usuario)
            val cacheLocal = lerCacheProgramacoes()

            receitas.map { dados ->
                val medicamento = dados.objeto("medicamento")
                val receita = dados.objeto("receita_medica")
                val estoque = dados.objeto("estoque")
                val programacao = dados.objeto(
                    "programacao_medicamento",
                    "programacao",
                    "programacoes_medicamento"
                )
                val idReceitaStr = dados.valor("id_receita_medica", "id")
                    ?: receita.valor("id_receita_medica", "id")
                    ?: error("A API retornou uma receita sem identificador.")
                val idReceita = idReceitaStr.toLongOrNull()
                    ?: error("A API retornou um identificador de receita inválido.")

                val progAgenda = programacoesAgenda[idReceita]
                val progCache = cacheLocal[idReceita]

                val unidadeEmbalagem = dados.valor("sigla_embalagem", "unidade_medida_embalagem")
                    ?: medicamento.valor("sigla_embalagem", "unidade_medida_embalagem")
                    ?: unidades.valorPorId(
                        medicamento.valor("id_unidade_medida_embalagem"),
                        "sigla"
                    )
                val unidadeDose = dados.valor("sigla_dose", "unidade_dose")
                    ?: receita.valor("sigla_dose", "unidade_dose")
                    ?: unidades.valorPorId(receita.valor("id_unidade_medida"), "sigla")
                val tipoDosagem = dados.valor("tipo_dosagem")
                    ?: medicamento.valor("tipo_dosagem")
                    ?: tipos.valorPorId(medicamento.valor("id_tipo_dosagem"), "tipo_dosagem")
                val tipoIntervalo = (dados.valor("tipo_intervalo", "intervalo_dosagem")
                    ?: programacao.valor("tipo_intervalo", "intervalo_dosagem")
                    ?: intervalos.valorPorId(programacao.valor("id_tipo_intervalo"), "tipo_intervalo"))
                    .ifBlank { progAgenda?.intervalo.orEmpty() }
                    .ifBlank { progCache?.intervalo.orEmpty() }

                val frequencia = (dados.valor("frequencia")
                    ?: programacao.valor("frequencia")
                    ?: receita.valor("frequencia")
                    ?: "")
                    .normalizarNumero()
                    .ifBlank { progAgenda?.frequencia.orEmpty() }
                    .ifBlank { progCache?.frequencia.orEmpty() }

                val horarioInicial = listOf<String?>(
                    dados.valor(
                        "data_hora_inicio",
                        "horario_inicial",
                        "horario_programado",
                        "hora_inicio",
                        "horario_inicio"
                    ),
                    programacao.valor(
                        "data_hora_inicio",
                        "horario_inicial",
                        "horario_programado",
                        "hora_inicio",
                        "horario_inicio"
                    )
                ).firstNotNullOfOrNull { it?.let(::extrairHorario) }
                    .orEmpty()
                    .ifBlank { progAgenda?.horarioInicial.orEmpty() }
                    .ifBlank { progCache?.horarioInicial.orEmpty() }

                val imagem = dados.valor("imagem")
                    ?: medicamento.valor("imagem")
                    ?: ""

                val requestMontado = MedicamentoRequest(
                    nome = dados.valor("nome_medicamento", "nome")
                        ?: medicamento.valor("nome")
                        ?: "",
                    concentracao = (dados.valor("concentracao")
                        ?: medicamento.valor("concentracao")
                        ?: "").normalizarNumero(),
                    unidadeMedida = unidadeEmbalagem,
                    dose = (dados.valor("dosagem", "dose")
                        ?: receita.valor("dosagem", "dose")
                        ?: "").normalizarNumero(),
                    unidadeDose = unidadeDose,
                    tipoMedicamento = tipoDosagem.replaceFirstChar { it.uppercase() },
                    quantidadeTotal = (dados.valor("quantidade_em_estoque", "quantidade")
                        ?: estoque.valor("quantidade_em_estoque", "quantidade")
                        ?: "").normalizarNumero(),
                    descricao = dados.valor("descricao") ?: receita.valor("descricao") ?: "",
                    frequencia = frequencia,
                    intervaloDosagem = tipoIntervalo,
                    horarioInicial = horarioInicial,
                    imagemRemota = imagem
                )

                salvarProgramacaoCache(idReceita, requestMontado)

                MedicamentoCadastrado(
                    id = idReceita,
                    dados = requestMontado,
                    imagemUrlRemota = imagem.ifBlank { null }
                )
            }
        }
    }

    private suspend fun carregarProgramacoesDaAgenda(idUsuario: Long): Map<Long, ProgramacaoSalva> {
        val fuso = ZoneId.systemDefault()
        val hoje = LocalDate.now(fuso)
        val formatador = DateTimeFormatter.ofPattern("HH:mm")

        val itensPorReceita = mutableMapOf<Long, MutableList<JsonObject>>()
        listOf(hoje, hoje.plusDays(1)).forEach { dia ->
            val resp = runCatching { api.consultarAgenda(idUsuario, dia.toString(), fuso.id) }.getOrNull()
            if (resp != null && resp.code() != 404 && resp.confirmouSucesso()) {
                val agenda = resp.body()?.getAsJsonObject("response")?.getAsJsonArray("agenda")
                agenda?.forEach { elem ->
                    val obj = elem.asJsonObject
                    val idRec = obj.texto("id_receita_medica")?.toLongOrNull() ?: return@forEach
                    itensPorReceita.getOrPut(idRec) { mutableListOf() }.add(obj)
                }
            }
        }

        return itensPorReceita.mapValues { (_, doses) ->
            val ordenadas = doses.mapNotNull { d ->
                val iso = d.texto("horario_programado") ?: return@mapNotNull null
                val inst = runCatching { Instant.parse(iso) }.getOrNull() ?: return@mapNotNull null
                inst to d
            }.sortedBy { it.first }

            val primeira = ordenadas.firstOrNull()
            val horarioInicial = primeira?.first?.atZone(fuso)?.format(formatador).orEmpty()
            val freqApi = primeira?.second?.texto("frequencia")?.normalizarNumero().orEmpty()
            val intervaloApi = primeira?.second?.texto("tipo_intervalo").orEmpty()

            if (freqApi.isNotBlank() && intervaloApi.isNotBlank()) {
                ProgramacaoSalva(freqApi, intervaloApi, horarioInicial)
            } else if (ordenadas.size >= 2) {
                val diffMin = Duration.between(ordenadas[0].first, ordenadas[1].first).toMinutes()
                when {
                    diffMin > 0 && diffMin % (24 * 60) == 0L ->
                        ProgramacaoSalva((diffMin / (24 * 60)).toString(), "Dia", horarioInicial)
                    diffMin > 0 && diffMin % 60 == 0L ->
                        ProgramacaoSalva((diffMin / 60).toString(), "Hora", horarioInicial)
                    else -> ProgramacaoSalva("", "", horarioInicial)
                }
            } else {
                ProgramacaoSalva("", "", horarioInicial)
            }
        }
    }

    override suspend fun cadastrarMedicamento(
        request: MedicamentoRequest,
        imagemFile: File?,
        imagemUriLocal: Uri?
    ) = requisitar {
        try {
            val usuario = usuarioDaSessao()

            val imagemUrl = imagemFile?.let {
                cloudinaryRepository.uploadImage(it).getOrThrow()
            } ?: imagemUriLocal?.let {
                cloudinaryRepository.uploadImage(it).getOrThrow()
            } ?: request.imagemRemota

            val requestComImagem = request.copy(imagemRemota = imagemUrl)
            val dados = prepararMedicamento(requestComImagem, criando = true)
            val resposta = api.cadastrarMedicamento(usuario, dados)

            val corpoErro = resposta.errorBody()?.string()
            val corpo = resposta.body()

            if (!resposta.isSuccessful) {
                error("Erro HTTP ${resposta.code()}: " + (corpoErro ?: resposta.message()))
            }

            val dadosResposta = requireNotNull(corpo) {
                "A API retornou uma resposta vazia."
            }.getAsJsonObject("response")
                ?: error("A API não retornou o objeto response.")

            val id = requireNotNull(
                dadosResposta.texto("id_receita_medica")?.toLongOrNull()
            ) {
                "O servidor não retornou o identificador da receita."
            }

            salvarProgramacaoCache(id, requestComImagem)

            MedicamentoCadastrado(
                id = id,
                dados = requestComImagem,
                imagemUri = imagemUriLocal,
                imagemUrlRemota = requestComImagem.imagemRemota
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("CADASTRO_MED", "ERRO NO CADASTRO", e)
            throw e
        }
    }

    override suspend fun atualizarMedicamento(
        id: Long,
        request: MedicamentoRequest,
        imagemFile: File?,
        imagemUriLocal: Uri?
    ) = requisitar {
        val usuario = usuarioDaSessao()
        val imagemUrl = imagemFile?.let { cloudinaryRepository.uploadImage(it).getOrThrow() }
            ?: imagemUriLocal?.let { cloudinaryRepository.uploadImage(it).getOrThrow() }
            ?: request.imagemRemota
        val requestComImagem = request.copy(imagemRemota = imagemUrl)
        val dados = prepararMedicamento(requestComImagem, criando = false)
        api.editarMedicamento(id, usuario, dados).exigirSucesso("Não foi possível atualizar o medicamento.")
        salvarProgramacaoCache(id, requestComImagem)
        MedicamentoCadastrado(
            id = id,
            dados = requestComImagem,
            imagemUri = imagemUriLocal,
            imagemUrlRemota = requestComImagem.imagemRemota
        )
    }

    override suspend fun atualizarEstoque(id: Long, novaQuantidade: Int) = requisitar {
        val dados = JsonObject().apply {
            add("estoque", JsonObject().apply {
                addProperty("quantidade_em_estoque", novaQuantidade)
            })
        }

        api.editarMedicamento(id, usuarioDaSessao(), dados)
            .exigirSucesso("Não foi possível atualizar o estoque.")
    }

    override suspend fun excluirMedicamento(id: Long) = requisitar {
        val resposta = api.excluirMedicamento(id, usuarioDaSessao())
        if (resposta.code() != 204) {
            resposta.exigirSucesso("Não foi possível excluir o medicamento.")
        }
        removerProgramacaoCache(id)
    }

    override suspend fun escanearReceitaOcr(imagemReceita: File) = requisitar {
        val usuario = usuarioDaSessao()
        require(imagemReceita.length() in 1L..(5L * 1024L * 1024L)) {
            "Selecione uma imagem de até 5 MB."
        }
        val tipo = if (imagemReceita.extension.equals("png", ignoreCase = true)) "image/png" else "image/jpeg"
        val arquivo = MultipartBody.Part.createFormData(
            "receita", imagemReceita.name, imagemReceita.asRequestBody(tipo.toMediaType())
        )
        val medicamentos = api.escanearReceita(usuario, arquivo)
            .objetos("medicamentos", "Não foi possível ler a receita.")
        val tipos = api.listarTiposDosagem().objetos("tipo_dosagem", "Não foi possível carregar os tipos de dosagem.")
        val unidades = api.listarUnidadesMedida().objetos("unidade_medida", "Não foi possível carregar as unidades.")
        val intervalos = api.listarTiposIntervalo().objetos("tipoIntervalo", "Não foi possível carregar os intervalos.")
        medicamentos.map { dados ->
            val medicamento = dados.getAsJsonObject("medicamento")
            val receita = dados.getAsJsonObject("receita_medica")
            val programacao = dados.getAsJsonObject("programacao_medicamento")
            MedicamentoRequest(
                nome = medicamento.texto("nome").orEmpty(),
                concentracao = medicamento.texto("concentracao").orEmpty(),
                unidadeMedida = unidades.valorPorId(medicamento.texto("id_unidade_medida_embalagem"), "sigla"),
                dose = receita.texto("dosagem").orEmpty(),
                unidadeDose = unidades.valorPorId(receita.texto("id_unidade_medida"), "sigla"),
                tipoMedicamento = tipos.valorPorId(medicamento.texto("id_tipo_dosagem"), "tipo_dosagem")
                    .replaceFirstChar { it.uppercase() },
                descricao = receita.texto("descricao").orEmpty(),
                frequencia = programacao.texto("frequencia").orEmpty(),
                intervaloDosagem = intervalos.valorPorId(programacao.texto("id_tipo_intervalo"), "tipo_intervalo")
            )
        }
    }

    private suspend fun prepararMedicamento(request: MedicamentoRequest, criando: Boolean): JsonObject {
        val unidades = api.listarUnidadesMedida().objetos("unidade_medida", "Não foi possível carregar as unidades.")
        val tipos = api.listarTiposDosagem().objetos("tipo_dosagem", "Não foi possível carregar os tipos de dosagem.")
        val idEmbalagem = unidades.idPorValor("sigla", request.unidadeMedida, "Selecione uma unidade de embalagem válida.")
        val idDose = unidades.idPorValor("sigla", request.unidadeDose, "Selecione a unidade da dose.")
        val idTipo = tipos.idPorValor("tipo_dosagem", request.tipoMedicamento, "Selecione um tipo de dosagem válido.")
        val concentracao = request.concentracao.replace(',', '.').toBigDecimalOrNull()
            ?: error("Informe uma concentração válida.")
        val dosagem = request.dose.replace(',', '.').toBigDecimalOrNull()
            ?: error("Informe uma dose válida.")
        val quantidade = request.quantidadeTotal.toIntOrNull()
            ?: error("Informe uma quantidade de estoque válida.")
        val imagem = request.imagemRemota.trim()
        if (criando) {
            require(imagem.isNotBlank()) { "Selecione uma foto para cadastrar o medicamento." }
        }
        if (imagem.isNotEmpty()) {
            require(imagem.length <= 255 && Uri.parse(imagem).let {
                it.scheme in listOf("http", "https") && !it.host.isNullOrBlank()
            }) { "A URL da imagem retornada pelo Cloudinary é inválida." }
        }

        return JsonObject().apply {
            add("medicamento", JsonObject().apply {
                addProperty("nome", request.nome.trim())
                if (imagem.isNotEmpty()) addProperty("imagem", imagem)
                addProperty("concentracao", concentracao)
                addProperty("id_unidade_medida_embalagem", idEmbalagem)
                addProperty("id_tipo_dosagem", idTipo)
            })
            add("estoque", JsonObject().apply {
                addProperty("quantidade_em_estoque", quantidade)
            })
            add("receita_medica", JsonObject().apply {
                addProperty("dosagem", dosagem)
                addProperty("id_unidade_medida", idDose)
                addProperty("descricao", request.descricao)
            })
            if (criando || request.horarioInicial.isNotBlank()) {
                add("programacao_medicamento", prepararProgramacao(request))
            }
        }
    }

    private suspend fun prepararProgramacao(request: MedicamentoRequest): JsonObject {
        val intervalos = api.listarTiposIntervalo().objetos("tipoIntervalo", "Não foi possível carregar os intervalos.")
        val idIntervalo = intervalos.idPorValor("tipo_intervalo", request.intervaloDosagem, "Selecione um intervalo válido.")
        val frequencia = request.frequencia.toIntOrNull()?.takeIf { it > 0 }
            ?: error("Informe uma frequência válida.")
        val hora = LocalTime.parse(request.horarioInicial)
        val fuso = ZoneId.systemDefault()
        val inicio = LocalDate.now(fuso).atTime(hora).atZone(fuso)
        val proximoInicio = if (inicio.toInstant().isBefore(Instant.now())) inicio.plusDays(1) else inicio

        return JsonObject().apply {
            addProperty("frequencia", frequencia)
            addProperty("id_tipo_intervalo", idIntervalo)
            addProperty("data_hora_inicio", proximoInicio.toInstant().toString())
        }
    }

    private fun usuarioDaSessao(): Long =
        idUsuario.takeIf { it > 0L } ?: error("Entre novamente para acessar seus medicamentos.")
}

internal fun Response<JsonObject>.exigirSucesso(mensagem: String) {
    check(confirmouSucesso()) { mensagemDeErro(mensagem) }
}

internal fun Response<JsonObject>.dadosOuErro(mensagem: String): JsonObject {
    exigirSucesso(mensagem)
    return requireNotNull(body()?.getAsJsonObject("response")) { mensagem }
}

internal fun Response<JsonObject>.objetos(campo: String, mensagem: String): List<JsonObject> =
    dadosOuErro(mensagem).getAsJsonArray(campo)?.map { it.asJsonObject }.orEmpty()

internal suspend fun <T> requisitar(acao: suspend () -> T): Result<T> = try {
    Result.success(acao())
} catch (cancelamento: CancellationException) {
    throw cancelamento
} catch (erro: Exception) {
    Result.failure(erro)
}

private fun JsonObject.objeto(vararg campos: String): JsonObject =
    campos.firstNotNullOfOrNull { campo ->
        get(campo)?.let { valor ->
            when {
                valor.isJsonObject -> valor.asJsonObject
                valor.isJsonArray -> valor.asJsonArray.firstOrNull()?.takeIf { it.isJsonObject }?.asJsonObject
                else -> null
            }
        }
    } ?: JsonObject()

private fun JsonObject.valor(vararg campos: String): String? =
    campos.firstNotNullOfOrNull { campo -> texto(campo)?.takeIf { it.isNotBlank() } }

private fun String.normalizarNumero(): String =
    toBigDecimalOrNull()?.stripTrailingZeros()?.toPlainString() ?: this

private fun List<JsonObject>.valorPorId(id: String?, campo: String): String =
    firstOrNull { it.texto("id") == id }?.texto(campo).orEmpty()

private fun List<JsonObject>.idPorValor(campo: String, valor: String, mensagem: String): Int =
    firstOrNull { normalizar(it.texto(campo).orEmpty()) == normalizar(valor) }
        ?.texto("id")?.toIntOrNull() ?: error(mensagem)

private fun extrairHorario(valor: String): String? {
    val horario = runCatching {
        Instant.parse(valor).atZone(ZoneId.systemDefault()).toLocalTime()
    }.getOrNull() ?: runCatching {
        LocalDateTime.parse(valor).atZone(ZoneId.systemDefault()).toLocalTime()
    }.getOrNull() ?: runCatching {
        LocalTime.parse(valor)
    }.getOrNull() ?: return null

    return horario.format(DateTimeFormatter.ofPattern("HH:mm"))
}

private fun normalizar(valor: String): String =
    Normalizer.normalize(valor.trim().lowercase(), Normalizer.Form.NFKD)
        .replace(Regex("\\p{M}"), "")
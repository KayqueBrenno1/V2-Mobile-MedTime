package com.example.v2_medtime.data.model

data class MedicamentoRequest(
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
    val unidadeMedida: String = "",
    val imagemRemota: String = ""
)

enum class PerguntaOcr(
    val titulo: String,
    val subtitulo: String
) {
    NOME(
        titulo = "Qual é o nome do medicamento?",
        subtitulo = "Não conseguimos identificar o nome com clareza na receita."
    ),
    CONCENTRACAO(
        titulo = "Qual é a concentração?",
        subtitulo = "Informe a dosagem da fórmula e a unidade (ex.: 500 mg)."
    ),
    TIPO_MEDICAMENTO(
        titulo = "Qual é a forma do medicamento?",
        subtitulo = "Selecione como esse medicamento é administrado."
    ),
    DOSE(
        titulo = "Quanto você toma por vez?",
        subtitulo = "Informe a quantidade física tomada em cada horário (ex.: 1 comp)."
    ),
    FREQUENCIA(
        titulo = "De quanto em quanto tempo você toma?",
        subtitulo = "Defina o intervalo entre uma dose e outra."
    ),
    HORARIO_INICIAL(
        titulo = "Que horas você vai tomar a primeira dose?",
        subtitulo = "Usaremos esse horário como base para programar seus alarmes."
    ),
    ESTOQUE_ATUAL(
        titulo = "Quantas unidades você tem agora?",
        subtitulo = "Informe o total disponível na caixa ou frasco para controlarmos o estoque."
    )
}

fun MedicamentoRequest.obterPerguntasPendentes(): List<PerguntaOcr> = buildList {
    if (nome.isBlank()) add(PerguntaOcr.NOME)
    if (concentracao.isBlank() || unidadeMedida.isBlank()) add(PerguntaOcr.CONCENTRACAO)
    if (tipoMedicamento.isBlank()) add(PerguntaOcr.TIPO_MEDICAMENTO)
    if (dose.isBlank() || unidadeDose.isBlank()) add(PerguntaOcr.DOSE)
    if (frequencia.isBlank() || intervaloDosagem.isBlank()) add(PerguntaOcr.FREQUENCIA)
    if (horarioInicial.isBlank()) add(PerguntaOcr.HORARIO_INICIAL)
    if (quantidadeTotal.isBlank()) add(PerguntaOcr.ESTOQUE_ATUAL)
}
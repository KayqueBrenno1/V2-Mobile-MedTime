package com.example.v2_medtime.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

object FormatadorCampos {
    fun data(value: String): String {
        val numeros = value.filter { it.isDigit() }.take(8)

        return buildString {
            numeros.forEachIndexed { index, char ->
                if (index == 2 || index == 4) append('/')
                append(char)
            }
        }
    }

    fun dataVisualTransformation(): VisualTransformation {
        return VisualTransformation { text ->
            val numeros = text.text.take(8)
            val formatado = data(numeros)

            val offsetMapping = object : OffsetMapping {

                override fun originalToTransformed(offset: Int): Int {
                    val posicao = offset.coerceIn(0, numeros.length)

                    return (posicao +
                            (if (posicao > 2) 1 else 0) +
                            (if (posicao > 4) 1 else 0)
                            ).coerceAtMost(formatado.length)
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return (offset -
                            (if (offset > 2) 1 else 0) -
                            (if (offset > 5) 1 else 0)
                            ).coerceIn(0, numeros.length)
                }
            }

            TransformedText(
                AnnotatedString(formatado),
                offsetMapping
            )
        }
    }

    fun cpf(value: String): String {
        val numeros = value.filter { it.isDigit() }.take(11)

        return buildString {
            numeros.forEachIndexed { index, char ->
                if (index == 3 || index == 6) append('.')
                if (index == 9) append('-')
                append(char)
            }
        }
    }


    fun cpfVisualTransformation(): VisualTransformation {
        return VisualTransformation { text ->
            val numeros = text.text.take(11)
            val formatado = cpf(numeros)

            val offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    val posicao = offset.coerceIn(0, numeros.length)

                    return (posicao +
                            (if (posicao > 3) 1 else 0) +
                            (if (posicao > 6) 1 else 0) +
                            (if (posicao > 9) 1 else 0)
                            ).coerceAtMost(formatado.length)
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return (offset -
                            (if (offset > 3) 1 else 0) -
                            (if (offset > 7) 1 else 0) -
                            (if (offset > 11) 1 else 0)
                            ).coerceIn(0, numeros.length)
                }
            }

            TransformedText(
                AnnotatedString(formatado),
                offsetMapping
            )
        }
    }


    fun telefone(value: String): String {
        val numeros = value.filter { it.isDigit() }.take(11)

        return buildString {
            numeros.forEachIndexed { index, char ->
                when {
                    index == 0 -> append('(')
                    index == 2 -> append(") ")
                    index == 6 && numeros.length <= 10 -> append('-')
                    index == 7 && numeros.length == 11 -> append('-')
                }

                append(char)
            }
        }
    }

    fun dataParaExibicao(value: String): String {
        val partes = value.take(10).split("-")

        if (partes.size != 3 ||
            partes[0].length != 4 ||
            partes[1].length != 2 ||
            partes[2].length != 2
        ) {
            return value
        }

        return "${partes[2]}/${partes[1]}/${partes[0]}"
    }

    fun dataParaApi(value: String): String? {
        val partes = value.split("/")

        if (partes.size != 3 ||
            partes[0].length != 2 ||
            partes[1].length != 2 ||
            partes[2].length != 4
        ) {
            return null
        }

        return "${partes[2]}-${partes[1]}-${partes[0]}"
    }
}
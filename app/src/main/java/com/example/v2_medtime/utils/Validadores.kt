package com.example.v2_medtime.utils

import android.util.Patterns
import java.time.DateTimeException
import java.time.LocalDate

object Validadores {

    fun validarNome(nome: String): Boolean {
        return nome.trim().length >= 2
    }

    fun validarEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS
            .matcher(email.trim())
            .matches()
    }

    fun validarCpf(cpf: String): Boolean {
        if (cpf.isBlank()) return true

        val numeros = cpf.filter { it.isDigit() }

        if (numeros.length != 11) return false
        if (numeros.all { it == numeros[0] }) return false

        for (tamanho in 9..10) {
            val soma = (0 until tamanho).sumOf { indice ->
                numeros[indice].digitToInt() * (tamanho + 1 - indice)
            }

            val resto = (soma * 10) % 11
            val digito = if (resto == 10) 0 else resto

            if (digito != numeros[tamanho].digitToInt()) {
                return false
            }
        }

        return true
    }

    fun validarTelefone(telefone: String): Boolean {
        if (telefone.isBlank()) return true

        val numeros = telefone.filter { it.isDigit() }

        return numeros.length == 10 || numeros.length == 11
    }

    fun validarDataNascimento(data: String): Boolean {
        if (data.length != 8 || !data.all { it.isDigit() }) {
            return false
        }

        return try {
            val dia = data.substring(0, 2).toInt()
            val mes = data.substring(2, 4).toInt()
            val ano = data.substring(4, 8).toInt()

            val nascimento = LocalDate.of(ano, mes, dia)

            !nascimento.isAfter(LocalDate.now()) &&
                    nascimento.isAfter(LocalDate.of(1900, 1, 1))
        } catch (error: DateTimeException) {
            false
        }
    }
}
package com.example.v2_medtime.data.local

import android.content.Context

class EstoqueReferenciaStorage(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(
        "medtime_estoque_referencia",
        Context.MODE_PRIVATE
    )

    fun registrarTotal(idReceita: Long, quantidade: Int) {
        if (idReceita <= 0L || quantidade <= 0) return
        prefs.edit().putInt("total_$idReceita", quantidade).apply()
    }

    fun obterTotalOuSincronizar(idReceita: Long, quantidadeAtual: Int): Int {
        if (idReceita <= 0L) return quantidadeAtual.coerceAtLeast(1)
        val chave = "total_$idReceita"
        val salvo = prefs.getInt(chave, 0)
        val total = maxOf(salvo, quantidadeAtual).coerceAtLeast(1)
        if (total != salvo) {
            prefs.edit().putInt(chave, total).apply()
        }
        return total
    }

    fun remover(idReceita: Long) {
        prefs.edit().remove("total_$idReceita").apply()
    }
}
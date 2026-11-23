package com.example.v2_medtime.data.local

import android.content.Context

class NotificacaoStorage(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(
        "medtime_notificacoes",
        Context.MODE_PRIVATE
    )

    fun jaNotificado(chave: String): Boolean =
        preferences.getBoolean("enviada_$chave", false)

    fun marcarNotificado(chave: String) {
        preferences.edit()
            .putBoolean("enviada_$chave", true)
            .apply()
    }

    fun salvarIdsAlarmesAgendados(ids: Set<String>) {
        preferences.edit()
            .putStringSet("alarmes_agendados", ids)
            .apply()
    }

    fun obterIdsAlarmesAgendados(): Set<String> =
        preferences.getStringSet("alarmes_agendados", emptySet()).orEmpty()

    fun obterVinculosConhecidos(idUsuario: Long): Set<String>? {
        val chave = "vinculos_$idUsuario"
        if (!preferences.contains(chave)) return null
        return preferences.getStringSet(chave, emptySet()).orEmpty()
    }

    fun salvarVinculosConhecidos(idUsuario: Long, ids: Set<String>) {
        preferences.edit()
            .putStringSet("vinculos_$idUsuario", ids)
            .apply()
    }

    fun limparTudo() {
        preferences.edit().clear().apply()
    }
}
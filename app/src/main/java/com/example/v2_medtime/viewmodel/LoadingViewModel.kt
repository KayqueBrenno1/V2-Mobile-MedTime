package com.example.v2_medtime.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoadingViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var mensagem by mutableStateOf("Carregando...")
        private set

    fun mostrar(mensagem: String = "Carregando...") {
        this.mensagem = mensagem
        isLoading = true
    }

    fun ocultar() {
        isLoading = false
    }
}
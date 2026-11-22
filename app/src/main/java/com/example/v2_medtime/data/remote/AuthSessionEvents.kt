package com.example.v2_medtime.data.remote

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AuthSessionEvents {
    private val _sessionEnded = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionEnded = _sessionEnded.asSharedFlow()

    fun notifySessionEnded() {
        _sessionEnded.tryEmit(Unit)
    }
}

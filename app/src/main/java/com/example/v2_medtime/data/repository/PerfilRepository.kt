// data/repository/PerfilRepository.kt
package com.example.v2_medtime.data.repository

import android.net.Uri
import com.example.v2_medtime.data.model.PerfilUsuario
import java.io.File

interface PerfilRepository {
    suspend fun obterPerfil(): Result<PerfilUsuario>
    suspend fun atualizarPerfil(
        perfil: PerfilUsuario,
        novaFotoFile: File?,
        novaFotoUri: Uri?
    ): Result<PerfilUsuario>
    suspend fun encerrarSessao(): Result<Unit>
}

class FakePerfilRepository : PerfilRepository {
    private var perfilAtual = PerfilUsuario()

    override suspend fun obterPerfil(): Result<PerfilUsuario> {
        return Result.success(perfilAtual)
    }

    override suspend fun atualizarPerfil(
        perfil: PerfilUsuario,
        novaFotoFile: File?,
        novaFotoUri: Uri?
    ): Result<PerfilUsuario> {
        perfilAtual = perfil.copy(
            fotoUriLocal = novaFotoUri ?: perfilAtual.fotoUriLocal
        )
        return Result.success(perfilAtual)
    }

    override suspend fun encerrarSessao(): Result<Unit> {
        return Result.success(Unit)
    }
}
package com.example.v2_medtime.notifications

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.v2_medtime.data.local.TokenManager
import com.example.v2_medtime.data.model.TipoAlertaNotificacao
import com.example.v2_medtime.data.remote.RetrofitClient
import com.example.v2_medtime.data.repository.ApiNotificacaoRepository
import java.util.concurrent.TimeUnit

class VerificacaoPeriodicaWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        RetrofitClient.inicializar(context)
        NotificacaoHelper.criarCanais(context)

        val sessao = TokenManager(context).obterSessao() ?: return Result.success()
        if (sessao.idUsuario <= 0L) return Result.success()

        val repository = ApiNotificacaoRepository(context)

        // 1. Mantém os alarmes exatos das próximas doses sincronizados
        AgendadorAlarmesMedicamento.sincronizarAlarmes(context, repository)

        // 2. Verifica doses atrasadas, estoque baixo e novos vínculos
        repository.verificarAlertasSegundoPlano()
            .onSuccess { alertas ->
                alertas.forEach { alerta ->
                    val canalId = when (alerta.tipo) {
                        TipoAlertaNotificacao.DOSE_ATRASADA -> NotificacaoHelper.CANAL_LEMBRETES
                        TipoAlertaNotificacao.ESTOQUE_BAIXO -> NotificacaoHelper.CANAL_ESTOQUE
                        TipoAlertaNotificacao.NOVO_VINCULO -> NotificacaoHelper.CANAL_VINCULOS
                    }

                    NotificacaoHelper.exibirAlertaSimples(
                        context = context,
                        canalId = canalId,
                        notificacaoId = alerta.notificacaoId,
                        titulo = alerta.titulo,
                        mensagem = alerta.mensagem,
                        prioridadeAlta = alerta.prioridadeAlta
                    )

                    repository.marcarAlertaComoEnviado(alerta.chaveUnica)
                }
            }

        return Result.success()
    }

    companion object {
        private const val NOME_TRABALHO_PERIODICO = "medtime_verificacao_periodica"
        private const val NOME_TRABALHO_IMEDIATO = "medtime_verificacao_imediata"

        fun agendarPeriodico(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<VerificacaoPeriodicaWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
                NOME_TRABALHO_PERIODICO,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun executarAgora(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<VerificacaoPeriodicaWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
                NOME_TRABALHO_IMEDIATO,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }

        fun cancelar(context: Context) {
            val workManager = WorkManager.getInstance(context.applicationContext)
            workManager.cancelUniqueWork(NOME_TRABALHO_PERIODICO)
            workManager.cancelUniqueWork(NOME_TRABALHO_IMEDIATO)
        }
    }
}
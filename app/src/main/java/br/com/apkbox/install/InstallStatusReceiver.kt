package br.com.apkbox.install

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller

class InstallStatusReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, Int.MIN_VALUE)
        val pacote = intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME)

        when (status) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                // O sistema devolve o Intent do diálogo de confirmação; nós só o disparamos.
                @Suppress("DEPRECATION")
                val confirmacao = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
                if (confirmacao != null) {
                    confirmacao.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    runCatching { context.startActivity(confirmacao) }
                        .onFailure {
                            InstallEvents.publicar(
                                InstallEvent.Falha("Não foi possível abrir o diálogo de instalação: ${it.message}")
                            )
                        }
                } else {
                    InstallEvents.publicar(InstallEvent.Falha("Sistema não devolveu o diálogo de instalação."))
                }
            }

            PackageInstaller.STATUS_SUCCESS ->
                InstallEvents.publicar(InstallEvent.Sucesso(pacote))

            else -> {
                val detalhe = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
                InstallEvents.publicar(InstallEvent.Falha(descrever(status, detalhe)))
            }
        }
    }

    private fun descrever(status: Int, detalhe: String?): String {
        val base = when (status) {
            PackageInstaller.STATUS_FAILURE_ABORTED -> "Instalação cancelada."
            PackageInstaller.STATUS_FAILURE_BLOCKED -> "Instalação bloqueada pelo sistema."
            PackageInstaller.STATUS_FAILURE_CONFLICT ->
                "Conflito: já existe uma versão instalada com assinatura diferente. Desinstale a atual antes."
            PackageInstaller.STATUS_FAILURE_INCOMPATIBLE ->
                "APK incompatível com esta central (Android antigo demais ou outra arquitetura)."
            PackageInstaller.STATUS_FAILURE_INVALID -> "APK inválido ou corrompido."
            PackageInstaller.STATUS_FAILURE_STORAGE -> "Espaço insuficiente na central."
            else -> "Falha na instalação."
        }
        return if (detalhe.isNullOrBlank()) base else "$base ($detalhe)"
    }
}

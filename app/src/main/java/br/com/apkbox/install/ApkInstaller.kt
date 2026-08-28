package br.com.apkbox.install

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ApkInstaller(private val context: Context) {

    /**
     * Tenta o caminho bom (PackageInstaller, que devolve o resultado pelo receiver).
     * Se a ROM da central recusar, cai para o ACTION_VIEW clássico — instala, mas
     * sem callback: nesse caso a UI só consegue dizer "diálogo aberto".
     */
    suspend fun instalar(apk: File, packageName: String?): InstallLaunch = withContext(Dispatchers.IO) {
        try {
            instalarViaSession(apk, packageName)
            InstallLaunch.ComRetorno
        } catch (e: Exception) {
            try {
                instalarViaIntent(apk)
                InstallLaunch.SemRetorno(e.message ?: e.javaClass.simpleName)
            } catch (e2: Exception) {
                throw IllegalStateException(
                    "Não foi possível iniciar a instalação. Sessão: ${e.message}. Intent: ${e2.message}"
                )
            }
        }
    }

    private fun instalarViaSession(apk: File, packageName: String?) {
        val installer = context.packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL).apply {
            packageName?.let { setAppPackageName(it) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setSize(apk.length())
            }
        }

        val sessionId = installer.createSession(params)
        installer.openSession(sessionId).use { session ->
            session.openWrite("base.apk", 0, apk.length()).use { output ->
                apk.inputStream().use { input -> input.copyTo(output, 64 * 1024) }
                session.fsync(output)
            }
            session.commit(statusIntentSender(sessionId))
        }
    }

    private fun statusIntentSender(sessionId: Int): android.content.IntentSender {
        val intent = Intent(context, InstallStatusReceiver::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getBroadcast(context, sessionId, intent, flags).intentSender
    }

    private fun instalarViaIntent(apk: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apk,
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }

    fun intentDesinstalar(packageName: String): Intent =
        Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.parse("package:$packageName")).apply {
            putExtra(Intent.EXTRA_RETURN_RESULT, true)
        }
}

sealed interface InstallLaunch {
    /** Sessão aceita: o resultado chega em [InstallEvents]. */
    data object ComRetorno : InstallLaunch

    /** Caiu no fallback: o diálogo abriu, mas não há callback de resultado. */
    data class SemRetorno(val motivo: String) : InstallLaunch
}

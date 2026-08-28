package br.com.hugolumazzini.havalapkstore.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.security.MessageDigest
import kotlin.coroutines.coroutineContext

class Downloader(
    private val context: Context,
    private val http: OkHttpClient,
) {

    private val dir: File
        get() = File(context.cacheDir, "apks").apply { mkdirs() }

    /**
     * Baixa para o cache interno. [onProgress] recebe 0f..1f, ou -1f quando o servidor
     * não informa Content-Length.
     */
    suspend fun download(
        url: String,
        fileName: String,
        onProgress: (Float) -> Unit,
    ): File = withContext(Dispatchers.IO) {
        val destino = File(dir, sanitize(fileName))
        if (destino.exists()) destino.delete()

        val request = Request.Builder().url(url).build()
        http.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IllegalStateException("HTTP ${response.code} ao baixar")
            val body = response.body ?: throw IllegalStateException("Resposta sem conteúdo")

            val total = body.contentLength()
            var lidos = 0L
            val buffer = ByteArray(64 * 1024)

            body.byteStream().use { input ->
                destino.outputStream().use { output ->
                    while (true) {
                        coroutineContext.ensureActive()
                        val n = input.read(buffer)
                        if (n == -1) break
                        output.write(buffer, 0, n)
                        lidos += n
                        onProgress(if (total > 0) lidos.toFloat() / total else -1f)
                    }
                }
            }
        }

        if (destino.length() == 0L) {
            destino.delete()
            throw IllegalStateException("Arquivo baixado veio vazio")
        }
        destino
    }

    suspend fun sha256(file: File): String = withContext(Dispatchers.IO) {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val n = input.read(buffer)
                if (n == -1) break
                digest.update(buffer, 0, n)
            }
        }
        digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun limparCache() {
        dir.listFiles()?.forEach { it.delete() }
    }

    private fun sanitize(nome: String): String {
        val limpo = nome.substringAfterLast('/').replace(Regex("[^A-Za-z0-9._-]"), "_")
        return if (limpo.endsWith(".apk", ignoreCase = true)) limpo else "$limpo.apk"
    }
}

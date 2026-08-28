package br.com.hugolumazzini.havalapkstore.data

import android.content.Context
import br.com.hugolumazzini.havalapkstore.data.model.Catalog
import br.com.hugolumazzini.havalapkstore.data.model.CatalogOrigin
import br.com.hugolumazzini.havalapkstore.data.model.CatalogResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

/**
 * Ordem de tentativa: URL remota -> última cópia baixada -> assets/catalog.json.
 * A central pode estar sem internet, então nunca deixamos a grid vazia por isso.
 */
class CatalogRepository(
    private val context: Context,
    private val http: OkHttpClient,
) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val cacheFile: File get() = File(context.filesDir, "catalog.json")

    suspend fun load(): CatalogResult = withContext(Dispatchers.IO) {
        val remoteError = try {
            val request = Request.Builder()
                .url(CATALOG_URL)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build()
            http.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IllegalStateException("HTTP ${response.code}")
                val body = response.body?.string().orEmpty()
                val catalog = json.decodeFromString<Catalog>(body)
                cacheFile.writeText(body)
                return@withContext CatalogResult(catalog.apps, CatalogOrigin.REMOTO)
            }
        } catch (e: Exception) {
            e.message ?: e.javaClass.simpleName
        }

        readFile(cacheFile)?.let {
            return@withContext CatalogResult(
                it.apps,
                CatalogOrigin.CACHE,
                "Sem resposta da URL do catálogo ($remoteError). Mostrando a última versão baixada.",
            )
        }

        val embedded = try {
            context.assets.open("catalog.json").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            null
        }

        val apps = embedded?.let { runCatching { json.decodeFromString<Catalog>(it).apps }.getOrNull() }

        CatalogResult(
            apps.orEmpty(),
            CatalogOrigin.EMBUTIDO,
            "Sem resposta da URL do catálogo ($remoteError). Mostrando a lista embutida no app.",
        )
    }

    private fun readFile(file: File): Catalog? = runCatching {
        if (!file.exists()) return null
        json.decodeFromString<Catalog>(file.readText())
    }.getOrNull()

    companion object {
        /**
         * Fonte oficial do catálogo. Fixa de propósito: quem usa o app não tem
         * por que apontá-lo para outra lista, e um endereço trocado por engano
         * transformaria a loja num instalador de qualquer coisa.
         */
        const val CATALOG_URL =
            "https://raw.githubusercontent.com/hugolumazzini/haval-apk-store/main/catalog.json"
    }
}

package br.com.hugolumazzini.havalapkstore.data

import android.util.Log
import br.com.hugolumazzini.havalapkstore.data.model.HavalTripRelease
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Locale

class HavalTripRepository(private val httpClient: OkHttpClient) {

    companion object {
        private const val RELEASES_URL = "https://api.github.com/repos/hugolumazzini/haval-trip/releases"
        private const val TAG = "HavalTripRepository"

        // Versões conhecidas como funcionais (baseado nas notas de release)
        private val WORKING_VERSIONS = setOf(
            "v1.0.21", // REVERTIDO para ad223a0 (SIMPLES e FUNCIONA)
            "v1.0.19", // CAPTURA RESTAURADA
        )
    }

    suspend fun loadReleases(): List<HavalTripRelease> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(RELEASES_URL)
                .header("Accept", "application/vnd.github.v3+json")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.e(TAG, "Falha ao buscar releases: ${response.code}")
                return@withContext emptyList()
            }

            val body = response.body?.string() ?: return@withContext emptyList()
            parseReleases(body)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar releases do Haval Trip", e)
            emptyList()
        }
    }

    private fun parseReleases(json: String): List<HavalTripRelease> {
        val releases = mutableListOf<HavalTripRelease>()
        val array = JSONArray(json)

        for (i in 0 until array.length()) {
            val release = array.getJSONObject(i)
            val tagName = release.getString("tag_name")
            val name = release.optString("name", tagName)
            val publishedAt = release.getString("published_at")
            val assets = release.getJSONArray("assets")

            // Procura o APK nos assets
            for (j in 0 until assets.length()) {
                val asset = assets.getJSONObject(j)
                val assetName = asset.getString("name")

                if (assetName.endsWith(".apk", ignoreCase = true)) {
                    val apkUrl = asset.getString("browser_download_url")
                    val apkSize = asset.getLong("size")

                    releases.add(
                        HavalTripRelease(
                            tagName = tagName,
                            name = name,
                            publishedAt = formatDate(publishedAt),
                            apkUrl = apkUrl,
                            apkSize = apkSize,
                            isWorking = WORKING_VERSIONS.contains(tagName)
                        )
                    )
                    break // Só pega o primeiro APK de cada release
                }
            }
        }

        return releases
    }

    private fun formatDate(isoDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
            val date = inputFormat.parse(isoDate)
            date?.let { outputFormat.format(it) } ?: isoDate
        } catch (e: Exception) {
            isoDate
        }
    }
}

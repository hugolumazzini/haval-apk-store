package br.com.hugolumazzini.havalapkstore.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

private val cache = object : LruCache<String, Bitmap>(6 * 1024 * 1024) {
    override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
}

/**
 * Carregador mínimo de ícone remoto — evita puxar uma lib de imagem só para isto.
 * Sem URL (ou em falha) mostra a inicial do nome.
 */
@Composable
fun RemoteIcon(
    url: String?,
    fallbackText: String,
    tamanho: Dp,
    http: OkHttpClient,
    modifier: Modifier = Modifier,
) {
    var bitmap by remember(url) { mutableStateOf(url?.let { cache.get(it) }) }

    LaunchedEffect(url) {
        if (url.isNullOrBlank() || bitmap != null) return@LaunchedEffect
        val carregado = withContext(Dispatchers.IO) {
            runCatching {
                http.newCall(Request.Builder().url(url).build()).execute().use { response ->
                    if (!response.isSuccessful) return@use null
                    response.body?.byteStream()?.let { BitmapFactory.decodeStream(it) }
                }
            }.getOrNull()
        }
        if (carregado != null) {
            cache.put(url, carregado)
            bitmap = carregado
        }
    }

    val atual = bitmap
    Box(modifier.size(tamanho), contentAlignment = Alignment.Center) {
        if (atual != null) {
            Image(
                bitmap = atual.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(tamanho),
            )
        } else {
            Text(
                text = fallbackText.trim().take(1).uppercase().ifBlank { "?" },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

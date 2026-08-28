package br.com.apkbox.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Catalog(
    val version: Int = 1,
    val apps: List<CatalogApp> = emptyList(),
)

@Serializable
data class CatalogApp(
    val packageName: String,
    val name: String,
    val apkUrl: String,
    val description: String = "",
    val category: String = "",
    val versionName: String = "",
    val versionCode: Long = 0,
    val sizeBytes: Long = 0,
    val iconUrl: String? = null,
    val sha256: String? = null,
)

/** De onde o catálogo exibido veio — a UI avisa quando não é o remoto. */
enum class CatalogOrigin { REMOTO, CACHE, EMBUTIDO }

data class CatalogResult(
    val apps: List<CatalogApp>,
    val origin: CatalogOrigin,
    /** Motivo da queda para cache/embutido, quando houver. */
    val warning: String? = null,
)

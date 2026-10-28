package br.com.hugolumazzini.havalapkstore.data.model

data class HavalTripRelease(
    val tagName: String,
    val name: String,
    val publishedAt: String,
    val apkUrl: String,
    val apkSize: Long,
    val isWorking: Boolean = false,
)

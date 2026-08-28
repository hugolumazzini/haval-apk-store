package br.com.hugolumazzini.havalapkstore.data

import android.content.Context

class Prefs(context: Context) {

    private val sp = context.getSharedPreferences("havalapkstore", Context.MODE_PRIVATE)

    var catalogUrl: String
        get() = sp.getString(KEY_CATALOG_URL, DEFAULT_CATALOG_URL).orEmpty()
            .ifBlank { DEFAULT_CATALOG_URL }
        set(value) {
            sp.edit().putString(KEY_CATALOG_URL, value.trim()).apply()
        }

    companion object {
        /** Catálogo oficial. Dá para apontar para outro direto na tela de Ajustes. */
        const val DEFAULT_CATALOG_URL =
            "https://raw.githubusercontent.com/hugolumazzini/haval-apk-store/main/catalog.json"

        private const val KEY_CATALOG_URL = "catalog_url"
    }
}

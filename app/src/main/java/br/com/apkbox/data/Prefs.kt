package br.com.apkbox.data

import android.content.Context

class Prefs(context: Context) {

    private val sp = context.getSharedPreferences("apkbox", Context.MODE_PRIVATE)

    var catalogUrl: String
        get() = sp.getString(KEY_CATALOG_URL, DEFAULT_CATALOG_URL).orEmpty()
            .ifBlank { DEFAULT_CATALOG_URL }
        set(value) {
            sp.edit().putString(KEY_CATALOG_URL, value.trim()).apply()
        }

    companion object {
        /** Troque pela URL do seu JSON — dá para editar direto na tela de Ajustes. */
        const val DEFAULT_CATALOG_URL =
            "https://raw.githubusercontent.com/SEU-USUARIO/SEU-REPO/main/catalog.json"

        private const val KEY_CATALOG_URL = "catalog_url"
    }
}

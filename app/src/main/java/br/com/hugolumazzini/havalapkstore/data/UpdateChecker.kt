package br.com.hugolumazzini.havalapkstore.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class UpdateChecker(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("update_check", Context.MODE_PRIVATE)

    suspend fun verificarPermissaoCheckDuplicado(): Boolean = withContext(Dispatchers.IO) {
        val ultimoCheck = prefs.getLong("ultima_verificacao", 0)
        val agora = System.currentTimeMillis()
        // Verifica a cada 6 horas ou mais
        val intervalo = 6 * 60 * 60 * 1000L
        val pode = (agora - ultimoCheck) > intervalo
        if (pode) {
            prefs.edit().putLong("ultima_verificacao", agora).apply()
        }
        pode
    }
}

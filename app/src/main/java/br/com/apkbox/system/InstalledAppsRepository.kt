package br.com.apkbox.system

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledApp(
    val packageName: String,
    val label: String,
    val versionName: String,
    val versionCode: Long,
    val icon: Drawable?,
    val podeAbrir: Boolean,
)

class InstalledAppsRepository(private val context: Context) {

    private val pm: PackageManager get() = context.packageManager

    suspend fun listar(): List<InstalledApp> = withContext(Dispatchers.IO) {
        @Suppress("DEPRECATION")
        pm.getInstalledPackages(0)
            .asSequence()
            .filter { it.applicationInfo != null }
            .filter { (it.applicationInfo!!.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
            .filter { it.packageName != context.packageName }
            .map { info ->
                val app = info.applicationInfo!!
                InstalledApp(
                    packageName = info.packageName,
                    label = pm.getApplicationLabel(app).toString(),
                    versionName = info.versionName.orEmpty(),
                    versionCode = versionCodeDe(info),
                    icon = runCatching { pm.getApplicationIcon(app) }.getOrNull(),
                    podeAbrir = pm.getLaunchIntentForPackage(info.packageName) != null,
                )
            }
            .sortedBy { it.label.lowercase() }
            .toList()
    }

    /** Mapa pacote -> versionCode, para o catálogo decidir Instalar / Atualizar / Instalado. */
    suspend fun versoesInstaladas(): Map<String, Long> = withContext(Dispatchers.IO) {
        @Suppress("DEPRECATION")
        pm.getInstalledPackages(0).associate { it.packageName to versionCodeDe(it) }
    }

    fun intentAbrir(packageName: String): Intent? = pm.getLaunchIntentForPackage(packageName)

    private fun versionCodeDe(info: PackageInfo): Long =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            info.versionCode.toLong()
        }
}

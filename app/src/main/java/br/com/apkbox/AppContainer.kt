package br.com.apkbox

import android.content.Context
import br.com.apkbox.data.CatalogRepository
import br.com.apkbox.data.Downloader
import br.com.apkbox.data.Prefs
import br.com.apkbox.install.ApkInstaller
import br.com.apkbox.system.InstalledAppsRepository
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {

    private val app = context.applicationContext

    private val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    val prefs = Prefs(app)
    val catalogRepository = CatalogRepository(app, http, prefs)
    val downloader = Downloader(app, http)
    val installer = ApkInstaller(app)
    val installedApps = InstalledAppsRepository(app)
    val httpClient: OkHttpClient get() = http
}

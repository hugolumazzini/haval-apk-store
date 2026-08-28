package br.com.hugolumazzini.havalapkstore

import android.content.Context
import br.com.hugolumazzini.havalapkstore.data.CatalogRepository
import br.com.hugolumazzini.havalapkstore.data.Downloader
import br.com.hugolumazzini.havalapkstore.install.ApkInstaller
import br.com.hugolumazzini.havalapkstore.system.InstalledAppsRepository
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

    val catalogRepository = CatalogRepository(app, http)
    val downloader = Downloader(app, http)
    val installer = ApkInstaller(app)
    val installedApps = InstalledAppsRepository(app)
    val httpClient: OkHttpClient get() = http
}

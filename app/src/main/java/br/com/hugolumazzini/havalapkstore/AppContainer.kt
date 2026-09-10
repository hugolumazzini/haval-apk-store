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
        // Encurtador que redirecione de https para http fica pelo caminho: aceitar
        // a queda seria baixar um APK por um canal que qualquer um na rede reescreve.
        .followSslRedirects(false)
        .build()

    val catalogRepository = CatalogRepository(app, http)
    val downloader = Downloader(app, http)
    val installer = ApkInstaller(app)
    val installedApps = InstalledAppsRepository(app)
    val httpClient: OkHttpClient get() = http
}

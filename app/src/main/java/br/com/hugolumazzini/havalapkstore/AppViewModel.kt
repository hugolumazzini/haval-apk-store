package br.com.hugolumazzini.havalapkstore

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.hugolumazzini.havalapkstore.data.Prefs
import br.com.hugolumazzini.havalapkstore.data.model.CatalogApp
import br.com.hugolumazzini.havalapkstore.data.model.CatalogOrigin
import br.com.hugolumazzini.havalapkstore.install.InstallEvent
import br.com.hugolumazzini.havalapkstore.install.InstallEvents
import br.com.hugolumazzini.havalapkstore.install.InstallLaunch
import br.com.hugolumazzini.havalapkstore.system.InstalledApp
import br.com.hugolumazzini.havalapkstore.system.UnknownSources
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

enum class Fase { BAIXANDO, VERIFICANDO, INSTALANDO }

data class Tarefa(
    val titulo: String,
    val fase: Fase,
    /** 0f..1f, ou -1f quando o tamanho é desconhecido. */
    val progresso: Float = -1f,
)

data class UiState(
    val catalogo: List<CatalogApp> = emptyList(),
    val origem: CatalogOrigin? = null,
    val carregandoCatalogo: Boolean = false,
    val avisoCatalogo: String? = null,
    val instalados: List<InstalledApp> = emptyList(),
    val versoes: Map<String, Long> = emptyMap(),
    val carregandoInstalados: Boolean = false,
    val tarefa: Tarefa? = null,
    val mensagem: String? = null,
    val erro: Boolean = false,
    val catalogUrl: String = "",
    val fontesLiberadas: Boolean = true,
)

enum class EstadoApp { NAO_INSTALADO, ATUALIZAVEL, INSTALADO }

class AppViewModel(app: Application) : AndroidViewModel(app) {

    val container = AppContainer(app)

    private val _state = MutableStateFlow(
        UiState(
            catalogUrl = container.prefs.catalogUrl,
            fontesLiberadas = UnknownSources.permitido(app),
        )
    )
    val state = _state.asStateFlow()

    private var jobTarefa: Job? = null
    private var apkEmVoo: File? = null

    init {
        viewModelScope.launch {
            InstallEvents.events.collect { trataEvento(it) }
        }
        recarregarCatalogo()
        recarregarInstalados()
    }

    // ---------- catálogo ----------

    fun recarregarCatalogo() {
        viewModelScope.launch {
            _state.update { it.copy(carregandoCatalogo = true) }
            val resultado = container.catalogRepository.load()
            _state.update {
                it.copy(
                    catalogo = resultado.apps,
                    origem = resultado.origin,
                    avisoCatalogo = resultado.warning,
                    carregandoCatalogo = false,
                )
            }
        }
    }

    fun salvarCatalogUrl(url: String) {
        container.prefs.catalogUrl = url
        _state.update { it.copy(catalogUrl = container.prefs.catalogUrl) }
        recarregarCatalogo()
    }

    fun restaurarCatalogUrl() = salvarCatalogUrl(Prefs.DEFAULT_CATALOG_URL)

    // ---------- instalados ----------

    fun recarregarInstalados() {
        viewModelScope.launch {
            _state.update { it.copy(carregandoInstalados = true) }
            val lista = container.installedApps.listar()
            val versoes = container.installedApps.versoesInstaladas()
            _state.update {
                it.copy(
                    instalados = lista,
                    versoes = versoes,
                    carregandoInstalados = false,
                    fontesLiberadas = UnknownSources.permitido(getApplication()),
                )
            }
        }
    }

    fun estadoDe(app: CatalogApp): EstadoApp {
        val instalada = _state.value.versoes[app.packageName] ?: return EstadoApp.NAO_INSTALADO
        return if (app.versionCode > 0 && app.versionCode > instalada) {
            EstadoApp.ATUALIZAVEL
        } else {
            EstadoApp.INSTALADO
        }
    }

    fun intentAbrir(packageName: String): Intent? = container.installedApps.intentAbrir(packageName)

    fun intentDesinstalar(packageName: String): Intent = container.installer.intentDesinstalar(packageName)

    fun intentFontesDesconhecidas(): Intent = UnknownSources.intentConfiguracao(getApplication())

    fun revalidarPermissao() {
        _state.update { it.copy(fontesLiberadas = UnknownSources.permitido(getApplication())) }
    }

    // ---------- instalação ----------

    fun instalarDoCatalogo(app: CatalogApp) = iniciar(
        titulo = app.name,
        url = app.apkUrl,
        nomeArquivo = "${app.packageName}.apk",
        packageName = app.packageName,
        sha256 = app.sha256,
    )

    fun instalarPorUrl(url: String) {
        val limpa = url.trim()
        if (!limpa.startsWith("http://") && !limpa.startsWith("https://")) {
            mensagem("Informe uma URL começando com http:// ou https://", erro = true)
            return
        }
        iniciar(
            titulo = limpa.substringAfterLast('/').ifBlank { "APK" },
            url = limpa,
            nomeArquivo = limpa.substringAfterLast('/').ifBlank { "download.apk" },
            packageName = null,
            sha256 = null,
        )
    }

    private fun iniciar(
        titulo: String,
        url: String,
        nomeArquivo: String,
        packageName: String?,
        sha256: String?,
    ) {
        if (_state.value.tarefa != null) {
            mensagem("Aguarde a instalação em andamento terminar.", erro = true)
            return
        }
        if (!UnknownSources.permitido(getApplication())) {
            _state.update { it.copy(fontesLiberadas = false) }
            mensagem("Libere \"instalar apps desconhecidos\" em Ajustes antes de continuar.", erro = true)
            return
        }

        jobTarefa = viewModelScope.launch {
            _state.update { it.copy(tarefa = Tarefa(titulo, Fase.BAIXANDO, 0f), mensagem = null, erro = false) }
            try {
                val apk = container.downloader.download(url, nomeArquivo) { p ->
                    _state.update { s ->
                        s.tarefa?.let { s.copy(tarefa = it.copy(progresso = p)) } ?: s
                    }
                }
                apkEmVoo = apk

                if (!sha256.isNullOrBlank()) {
                    _state.update { it.copy(tarefa = Tarefa(titulo, Fase.VERIFICANDO)) }
                    val calculado = container.downloader.sha256(apk)
                    if (!calculado.equals(sha256.trim(), ignoreCase = true)) {
                        apk.delete()
                        apkEmVoo = null
                        _state.update { it.copy(tarefa = null) }
                        mensagem(
                            "SHA-256 não confere — download descartado. Esperado ${sha256.take(12)}…, obtido ${calculado.take(12)}…",
                            erro = true,
                        )
                        return@launch
                    }
                }

                _state.update { it.copy(tarefa = Tarefa(titulo, Fase.INSTALANDO)) }
                when (val r = container.installer.instalar(apk, packageName)) {
                    is InstallLaunch.ComRetorno -> Unit // resultado chega em InstallEvents
                    is InstallLaunch.SemRetorno -> {
                        _state.update { it.copy(tarefa = null) }
                        mensagem("Diálogo de instalação aberto (modo alternativo: ${r.motivo}).")
                    }
                }
            } catch (e: Exception) {
                apkEmVoo?.delete()
                apkEmVoo = null
                _state.update { it.copy(tarefa = null) }
                mensagem("Falhou: ${e.message ?: e.javaClass.simpleName}", erro = true)
            }
        }
    }

    fun cancelarTarefa() {
        jobTarefa?.cancel()
        jobTarefa = null
        apkEmVoo?.delete()
        apkEmVoo = null
        _state.update { it.copy(tarefa = null) }
        mensagem("Cancelado.")
    }

    private fun trataEvento(event: InstallEvent) {
        apkEmVoo?.delete()
        apkEmVoo = null
        _state.update { it.copy(tarefa = null) }
        when (event) {
            is InstallEvent.Sucesso -> {
                mensagem("Instalado com sucesso${event.packageName?.let { " ($it)" }.orEmpty()}.")
                recarregarInstalados()
            }
            is InstallEvent.Falha -> mensagem(event.mensagem, erro = true)
        }
    }

    fun aoVoltarDaDesinstalacao(removido: Boolean) {
        mensagem(if (removido) "App desinstalado." else "Desinstalação cancelada.", erro = !removido)
        recarregarInstalados()
    }

    // ---------- mensagens ----------

    fun mensagem(texto: String, erro: Boolean = false) {
        _state.update { it.copy(mensagem = texto, erro = erro) }
    }

    fun limparMensagem() {
        _state.update { it.copy(mensagem = null, erro = false) }
    }
}

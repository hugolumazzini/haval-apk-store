package br.com.hugolumazzini.havalapkstore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.hugolumazzini.havalapkstore.AppViewModel
import br.com.hugolumazzini.havalapkstore.EstadoApp
import br.com.hugolumazzini.havalapkstore.UiState
import br.com.hugolumazzini.havalapkstore.data.model.CatalogApp
import br.com.hugolumazzini.havalapkstore.data.model.CatalogOrigin
import br.com.hugolumazzini.havalapkstore.ui.RemoteIcon
import br.com.hugolumazzini.havalapkstore.ui.theme.Cores

@Composable
fun CatalogScreen(vm: AppViewModel, state: UiState) {
    val context = LocalContext.current

    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.Top) {
            Box(Modifier.weight(1f)) {
                val quantos = state.catalogo.size.let {
                    if (it == 1) "1 app" else "$it apps"
                }
                Titulo(
                    "Catálogo",
                    when (state.origem) {
                        CatalogOrigin.REMOTO -> "$quantos · lista remota"
                        CatalogOrigin.CACHE -> "$quantos · última lista baixada"
                        CatalogOrigin.EMBUTIDO -> "$quantos · lista embutida no app"
                        null -> "Carregando…"
                    },
                )
            }
            TextButton(onClick = { vm.recarregarCatalogo() }) {
                Text(
                    "Recarregar",
                    style = MaterialTheme.typography.labelLarge,
                    color = Cores.Destaque,
                )
            }
        }

        if (state.carregandoCatalogo) {
            LinearProgressIndicator(
                Modifier.fillMaxWidth().height(3.dp).padding(bottom = 8.dp),
                color = Cores.Destaque,
                trackColor = Cores.Campo,
            )
        }
        state.avisoCatalogo?.let { Aviso(it) }
        if (!state.fontesLiberadas) {
            Aviso("Instalação bloqueada: libere \"instalar apps desconhecidos\" na aba Ajustes.")
        }
        state.tarefa?.let { BarraTarefa(it, onCancelar = vm::cancelarTarefa) }

        if (state.catalogo.isEmpty() && !state.carregandoCatalogo) {
            Aviso("Nenhum app no catálogo. Confira a URL do JSON na aba Ajustes.")
            return@Column
        }

        LazyVerticalGrid(
            // Largura mínima alta de propósito: numa central de 1080 dá uma coluna
            // só, com os cartões largos do Impulse. Em telas maiores, quebra em duas.
            columns = GridCells.Adaptive(minSize = 520.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(state.catalogo, key = { it.packageName + it.apkUrl }) { app ->
                CardApp(
                    app = app,
                    estado = vm.estadoDe(app),
                    ocupado = state.tarefa != null,
                    onInstalar = { vm.instalarDoCatalogo(app) },
                    onAbrir = {
                        val intent = vm.intentAbrir(app.packageName)
                        if (intent != null) context.startActivity(intent)
                        else vm.mensagem("Este app não tem tela para abrir.", erro = true)
                    },
                    http = vm.container.httpClient,
                )
            }
        }
    }
}

@Composable
private fun CardApp(
    app: CatalogApp,
    estado: EstadoApp,
    ocupado: Boolean,
    onInstalar: () -> Unit,
    onAbrir: () -> Unit,
    http: okhttp3.OkHttpClient,
) {
    Bloco(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // O Impulse assenta o ícone num disco claro — dá contraste com o fundo escuro.
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(Cores.Campo),
                contentAlignment = Alignment.Center,
            ) {
                RemoteIcon(
                    url = app.iconUrl,
                    fallbackText = app.name,
                    tamanho = 40.dp,
                    http = http,
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    app.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Cores.Texto,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                val detalhe = listOfNotNull(
                    app.versionName.ifBlank { null },
                    formatarTamanho(app.sizeBytes).ifBlank { null },
                    app.category.ifBlank { null },
                ).joinToString(" · ")
                if (detalhe.isNotBlank() || app.description.isNotBlank()) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        app.description.ifBlank { detalhe },
                        style = MaterialTheme.typography.bodySmall,
                        color = Cores.TextoApoio,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    when (estado) {
                        EstadoApp.NAO_INSTALADO -> "Status: Não instalado"
                        EstadoApp.ATUALIZAVEL -> "Status: Atualização disponível"
                        EstadoApp.INSTALADO -> "Status: Instalado"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = when (estado) {
                        EstadoApp.ATUALIZAVEL -> Cores.Destaque
                        EstadoApp.INSTALADO -> Cores.Confirmacao
                        EstadoApp.NAO_INSTALADO -> Cores.TextoApoio
                    },
                )
            }

            Spacer(Modifier.width(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                when (estado) {
                    EstadoApp.NAO_INSTALADO ->
                        BotaoPrimario("Instalar", onInstalar, enabled = !ocupado)

                    EstadoApp.ATUALIZAVEL -> {
                        BotaoPrimario(
                            "Atualizar",
                            onInstalar,
                            enabled = !ocupado,
                            cor = Cores.Confirmacao,
                        )
                        BotaoSecundario("Abrir", onAbrir)
                    }

                    EstadoApp.INSTALADO -> {
                        BotaoSecundario("Abrir", onAbrir)
                        TextButton(onClick = onInstalar, enabled = !ocupado) {
                            Text(
                                "Reinstalar",
                                style = MaterialTheme.typography.labelMedium,
                                color = Cores.TextoApoio,
                            )
                        }
                    }
                }
            }
        }
    }
}

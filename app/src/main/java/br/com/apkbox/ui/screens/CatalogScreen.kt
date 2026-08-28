package br.com.apkbox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.apkbox.AppViewModel
import br.com.apkbox.EstadoApp
import br.com.apkbox.UiState
import br.com.apkbox.data.model.CatalogApp
import br.com.apkbox.data.model.CatalogOrigin
import br.com.apkbox.ui.RemoteIcon

@Composable
fun CatalogScreen(vm: AppViewModel, state: UiState) {
    val context = LocalContext.current

    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) {
                Titulo(
                    "Catálogo",
                    when (state.origem) {
                        CatalogOrigin.REMOTO -> "${state.catalogo.size} apps · lista remota"
                        CatalogOrigin.CACHE -> "${state.catalogo.size} apps · última lista baixada"
                        CatalogOrigin.EMBUTIDO -> "${state.catalogo.size} apps · lista embutida no app"
                        null -> "Carregando…"
                    },
                )
            }
            TextButton(onClick = { vm.recarregarCatalogo() }) { Text("Recarregar") }
        }

        if (state.carregandoCatalogo) {
            LinearProgressIndicator(Modifier.fillMaxWidth().padding(bottom = 8.dp))
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
            columns = GridCells.Adaptive(minSize = 260.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
    Card {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RemoteIcon(
                    url = app.iconUrl,
                    fallbackText = app.name,
                    tamanho = 48.dp,
                    http = http,
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        app.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        listOfNotNull(
                            app.versionName.ifBlank { null },
                            formatarTamanho(app.sizeBytes).ifBlank { null },
                            app.category.ifBlank { null },
                        ).joinToString(" · "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            if (app.description.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    app.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                when (estado) {
                    EstadoApp.NAO_INSTALADO -> Button(
                        onClick = onInstalar,
                        enabled = !ocupado,
                        modifier = Modifier.height(48.dp),
                    ) { Text("Instalar") }

                    EstadoApp.ATUALIZAVEL -> {
                        Button(
                            onClick = onInstalar,
                            enabled = !ocupado,
                            modifier = Modifier.height(48.dp),
                        ) { Text("Atualizar") }
                        OutlinedButton(onClick = onAbrir, modifier = Modifier.height(48.dp)) {
                            Text("Abrir")
                        }
                    }

                    EstadoApp.INSTALADO -> {
                        OutlinedButton(onClick = onAbrir, modifier = Modifier.height(48.dp)) {
                            Text("Abrir")
                        }
                        TextButton(
                            onClick = onInstalar,
                            enabled = !ocupado,
                            modifier = Modifier.height(48.dp),
                        ) { Text("Reinstalar") }
                    }
                }
            }
        }
    }
}

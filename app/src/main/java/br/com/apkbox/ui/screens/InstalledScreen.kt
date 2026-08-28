package br.com.apkbox.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import br.com.apkbox.AppViewModel
import br.com.apkbox.UiState
import br.com.apkbox.system.InstalledApp
import br.com.apkbox.ui.theme.Cores

@Composable
fun InstalledScreen(
    vm: AppViewModel,
    state: UiState,
    onDesinstalar: (String) -> Unit,
) {
    val context = LocalContext.current

    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.Top) {
            Box(Modifier.weight(1f)) {
                Titulo("Instalados", "${state.instalados.size} apps de terceiros nesta central")
            }
            TextButton(onClick = { vm.recarregarInstalados() }) {
                Text(
                    "Recarregar",
                    style = MaterialTheme.typography.labelLarge,
                    color = Cores.Destaque,
                )
            }
        }

        if (state.carregandoInstalados) {
            LinearProgressIndicator(
                Modifier.fillMaxWidth().height(3.dp).padding(bottom = 8.dp),
                color = Cores.Destaque,
                trackColor = Cores.Campo,
            )
        }

        if (state.instalados.isEmpty() && !state.carregandoInstalados) {
            Aviso("Nenhum app de terceiros instalado — apps do sistema não são listados.")
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.instalados, key = { it.packageName }) { app ->
                LinhaApp(
                    app = app,
                    onAbrir = {
                        val intent = vm.intentAbrir(app.packageName)
                        if (intent != null) context.startActivity(intent)
                        else vm.mensagem("Este app não tem tela para abrir.", erro = true)
                    },
                    onDesinstalar = { onDesinstalar(app.packageName) },
                )
            }
        }
    }
}

@Composable
private fun LinhaApp(app: InstalledApp, onAbrir: () -> Unit, onDesinstalar: () -> Unit) {
    Bloco(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconeApp(app)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    app.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = Cores.Texto,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    listOfNotNull(
                        app.packageName,
                        app.versionName.ifBlank { null }?.let { "v$it" },
                    ).joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = Cores.TextoApoio,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(16.dp))
            if (app.podeAbrir) {
                BotaoSecundario("Abrir", onAbrir)
                Spacer(Modifier.width(8.dp))
            }
            TextButton(onClick = onDesinstalar) {
                Text(
                    "Desinstalar",
                    style = MaterialTheme.typography.labelLarge,
                    color = Cores.Erro,
                )
            }
        }
    }
}

@Composable
private fun IconeApp(app: InstalledApp) {
    val drawable = app.icon
    val bitmap = remember(app.packageName) {
        drawable?.let { runCatching { it.toBitmap(96, 96) }.getOrNull() }
    }
    Box(
        Modifier.size(46.dp).clip(CircleShape).background(Cores.Campo),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(38.dp),
            )
        } else {
            Text(
                app.label.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = Cores.TextoApoio,
            )
        }
    }
}

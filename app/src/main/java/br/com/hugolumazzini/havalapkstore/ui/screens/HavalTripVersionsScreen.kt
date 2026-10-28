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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import br.com.hugolumazzini.havalapkstore.ui.IconeDownload
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.hugolumazzini.havalapkstore.AppViewModel
import br.com.hugolumazzini.havalapkstore.UiState
import br.com.hugolumazzini.havalapkstore.data.model.HavalTripRelease
import br.com.hugolumazzini.havalapkstore.ui.theme.Cores

@Composable
fun HavalTripVersionsScreen(vm: AppViewModel, state: UiState) {
    LaunchedEffect(Unit) {
        if (state.havalTripReleases.isEmpty() && !state.carregandoHavalTrip) {
            vm.recarregarHavalTripReleases()
        }
    }

    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.Top) {
            Box(Modifier.weight(1f)) {
                Titulo(
                    "Haval Trip",
                    "${state.havalTripReleases.size} versões disponíveis • Teste e descubra a que funciona melhor"
                )
            }
            TextButton(onClick = { vm.recarregarHavalTripReleases() }) {
                Text(
                    "Recarregar",
                    style = MaterialTheme.typography.labelLarge,
                    color = Cores.Destaque,
                )
            }
        }

        if (state.carregandoHavalTrip) {
            LinearProgressIndicator(
                Modifier.fillMaxWidth().height(3.dp).padding(bottom = 8.dp),
                color = Cores.Destaque,
                trackColor = Cores.Campo,
            )
        }

        if (state.havalTripReleases.isEmpty() && !state.carregandoHavalTrip) {
            Aviso("Não foi possível carregar as versões do GitHub. Verifique sua conexão.")
            return@Column
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.havalTripReleases, key = { it.tagName }) { release ->
                ReleaseCard(
                    release = release,
                    onInstall = { vm.instalarHavalTrip(release) }
                )
            }
        }
    }
}

@Composable
private fun ReleaseCard(
    release: HavalTripRelease,
    onInstall: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Cores.Campo)
            .padding(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coluna principal: versão e nome
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        release.tagName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Cores.Texto,
                    )

                    if (release.isWorking) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Versão funcional",
                            tint = Cores.Destaque,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    release.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Cores.Texto.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(8.dp))

                Row {
                    Text(
                        release.publishedAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = Cores.Texto.copy(alpha = 0.6f),
                    )
                    Text(
                        " • ${formatSize(release.apkSize)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Cores.Texto.copy(alpha = 0.6f),
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Botão de instalação
            TextButton(onClick = onInstall) {
                Icon(
                    IconeDownload,
                    contentDescription = "Instalar",
                    tint = Cores.Destaque,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Instalar",
                    style = MaterialTheme.typography.labelLarge,
                    color = Cores.Destaque,
                )
            }
        }
    }
}

private fun formatSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
        else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    }
}

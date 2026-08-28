package br.com.apkbox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.com.apkbox.AppViewModel
import br.com.apkbox.UiState
import br.com.apkbox.data.model.CatalogOrigin

@Composable
fun SettingsScreen(vm: AppViewModel, state: UiState) {
    val context = LocalContext.current
    var url by remember(state.catalogUrl) { mutableStateOf(state.catalogUrl) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Titulo("Ajustes")

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("URL do catálogo", style = MaterialTheme.typography.titleMedium)
                Text(
                    "JSON com a lista de apps. Vale qualquer host — GitHub raw serve bem.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { vm.salvarCatalogUrl(url) },
                        enabled = url.isNotBlank() && url != state.catalogUrl,
                        modifier = Modifier.height(48.dp),
                    ) { Text("Salvar e recarregar") }
                    OutlinedButton(
                        onClick = { vm.recarregarCatalogo() },
                        modifier = Modifier.height(48.dp),
                    ) { Text("Recarregar") }
                    TextButton(
                        onClick = { vm.restaurarCatalogUrl() },
                        modifier = Modifier.height(48.dp),
                    ) { Text("Restaurar padrão") }
                }
                Spacer(Modifier.height(8.dp))
                LinhaInfo(
                    "Origem atual:",
                    when (state.origem) {
                        CatalogOrigin.REMOTO -> "URL remota"
                        CatalogOrigin.CACHE -> "cache em disco (remoto indisponível)"
                        CatalogOrigin.EMBUTIDO -> "lista embutida no APK"
                        null -> "—"
                    },
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Instalar apps desconhecidos", style = MaterialTheme.typography.titleMedium)
                Text(
                    if (state.fontesLiberadas) {
                        "Liberado — o APK Box pode abrir o instalador do sistema."
                    } else {
                        "Bloqueado. Sem essa permissão o Android recusa qualquer instalação " +
                            "iniciada por este app."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (state.fontesLiberadas) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { context.startActivity(vm.intentFontesDesconhecidas()) },
                    modifier = Modifier.height(48.dp),
                ) { Text(if (state.fontesLiberadas) "Abrir configuração" else "Liberar agora") }
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Como funciona", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Cada instalação e desinstalação passa pelo diálogo do Android — sem root " +
                        "ou ADB não existe instalação silenciosa. O que o app resolve é o resto: " +
                        "baixar o APK, conferir o SHA-256 quando o catálogo declara, avisar quando " +
                        "há versão nova e deixar tudo a um toque.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

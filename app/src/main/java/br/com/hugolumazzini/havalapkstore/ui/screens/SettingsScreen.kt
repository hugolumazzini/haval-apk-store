package br.com.hugolumazzini.havalapkstore.ui.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.com.hugolumazzini.havalapkstore.AppViewModel
import br.com.hugolumazzini.havalapkstore.UiState
import br.com.hugolumazzini.havalapkstore.data.model.CatalogOrigin
import br.com.hugolumazzini.havalapkstore.ui.theme.Cores

@Composable
fun SettingsScreen(vm: AppViewModel, state: UiState) {
    val context = LocalContext.current
    val atualizacao = state.atualizacaoDaLoja

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Titulo("Ajustes")

        Bloco(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                TituloBloco("Haval APK Store")
                Spacer(Modifier.height(6.dp))
                Text(
                    "Versão ${state.versaoDoApp.ifBlank { "—" }}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Cores.Texto,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    when {
                        state.carregandoCatalogo -> "Procurando versão nova…"
                        atualizacao != null ->
                            "Versão ${atualizacao.versionName.ifBlank { "nova" }} disponível."
                        !state.versaoConferida -> "Ainda não foi possível conferir."
                        state.origem != CatalogOrigin.REMOTO ->
                            "Sem conexão com a lista oficial — não dá para saber se há versão nova."
                        else -> "Você está na versão mais recente."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (atualizacao != null) Cores.Destaque else Cores.TextoApoio,
                )
                Spacer(Modifier.height(14.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (atualizacao != null) {
                        BotaoPrimario("Atualizar agora", onClick = { vm.atualizarLoja() })
                    }
                    BotaoSecundario(
                        "Procurar atualização",
                        onClick = { vm.recarregarCatalogo() },
                        enabled = !state.carregandoCatalogo,
                    )
                }
                Spacer(Modifier.height(10.dp))
                LinhaInfo(
                    "Lista de apps:",
                    when (state.origem) {
                        CatalogOrigin.REMOTO -> "lista oficial, recém-baixada"
                        CatalogOrigin.CACHE -> "última cópia baixada (sem conexão agora)"
                        CatalogOrigin.EMBUTIDO -> "lista embutida no app (sem conexão agora)"
                        null -> "—"
                    },
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Bloco(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                TituloBloco("Instalar apps desconhecidos")
                Spacer(Modifier.height(4.dp))
                Text(
                    if (state.fontesLiberadas) {
                        "Liberado — a loja pode abrir o instalador do sistema."
                    } else {
                        "Bloqueado. Sem essa permissão o Android recusa qualquer instalação " +
                            "iniciada por este app."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (state.fontesLiberadas) Cores.TextoApoio else Cores.Erro,
                )
                Spacer(Modifier.height(14.dp))
                BotaoSecundario(
                    if (state.fontesLiberadas) "Abrir configuração" else "Liberar agora",
                    onClick = { context.startActivity(vm.intentFontesDesconhecidas()) },
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Bloco(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                TituloBloco("Como funciona")
                Spacer(Modifier.height(6.dp))
                Text(
                    "Cada instalação e desinstalação passa pelo diálogo do Android — sem root " +
                        "ou ADB não existe instalação silenciosa. O que o app resolve é o resto: " +
                        "baixar o APK, conferir o SHA-256 quando o catálogo declara, avisar quando " +
                        "há versão nova e deixar tudo a um toque.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Cores.TextoApoio,
                )
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun TituloBloco(texto: String, apoio: String? = null) {
    Text(texto, style = MaterialTheme.typography.titleMedium, color = Cores.Texto)
    if (apoio != null) {
        Spacer(Modifier.height(3.dp))
        Text(apoio, style = MaterialTheme.typography.bodyMedium, color = Cores.TextoApoio)
    }
}

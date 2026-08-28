package br.com.hugolumazzini.havalapkstore.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.hugolumazzini.havalapkstore.AppViewModel
import br.com.hugolumazzini.havalapkstore.UiState
import br.com.hugolumazzini.havalapkstore.ui.theme.Cores

@Composable
fun UrlInstallScreen(vm: AppViewModel, state: UiState) {
    var url by rememberSaveable { mutableStateOf("") }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Titulo("Instalar por URL", "Cole o link direto do arquivo .apk")

        if (!state.fontesLiberadas) {
            Aviso("Instalação bloqueada: libere \"instalar apps desconhecidos\" na aba Ajustes.")
        }
        state.tarefa?.let { BarraTarefa(it, onCancelar = vm::cancelarTarefa) }

        // Campo e ação lado a lado, como na faixa "Instalar via URL" do Impulse.
        Row(verticalAlignment = Alignment.CenterVertically) {
            CampoTexto(
                valor = url,
                onValorMudou = { url = it },
                dica = "URL do APK",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Go,
                ),
            )
            Spacer(Modifier.width(12.dp))
            BotaoPrimario(
                "Instalar via URL",
                onClick = { vm.instalarPorUrl(url) },
                enabled = state.tarefa == null && url.isNotBlank(),
                modifier = Modifier.height(56.dp),
            )
        }

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { url = "" }, enabled = url.isNotBlank()) {
                Text(
                    "Limpar",
                    style = MaterialTheme.typography.labelLarge,
                    color = Cores.TextoApoio,
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Aviso(
            "O link precisa apontar direto para o .apk. Páginas de download que exigem clique " +
                "não funcionam aqui. Downloads por URL não têm SHA-256 declarado, então não são " +
                "verificados — a confirmação final é sempre do sistema."
        )
    }
}

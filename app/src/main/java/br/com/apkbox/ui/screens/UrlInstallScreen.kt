package br.com.apkbox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.apkbox.AppViewModel
import br.com.apkbox.UiState

@Composable
fun UrlInstallScreen(vm: AppViewModel, state: UiState) {
    var url by rememberSaveable { mutableStateOf("") }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Titulo("Instalar por URL", "Cole o link direto do arquivo .apk")

        if (!state.fontesLiberadas) {
            Aviso("Instalação bloqueada: libere \"instalar apps desconhecidos\" na aba Ajustes.")
        }
        state.tarefa?.let { BarraTarefa(it, onCancelar = vm::cancelarTarefa) }

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("https://…/app.apk") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { vm.instalarPorUrl(url) },
                enabled = state.tarefa == null && url.isNotBlank(),
                modifier = Modifier.height(52.dp),
            ) { Text("Baixar e instalar") }
            TextButton(
                onClick = { url = "" },
                enabled = url.isNotBlank(),
                modifier = Modifier.height(52.dp),
            ) { Text("Limpar") }
        }

        Spacer(Modifier.height(16.dp))
        Aviso(
            "O link precisa apontar direto para o .apk. Páginas de download que exigem clique " +
                "não funcionam aqui. Downloads por URL não têm SHA-256 declarado, então não são " +
                "verificados — a confirmação final é sempre do sistema."
        )
    }
}

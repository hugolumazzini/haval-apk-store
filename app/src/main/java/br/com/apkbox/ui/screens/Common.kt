package br.com.apkbox.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.apkbox.Fase
import br.com.apkbox.Tarefa

@Composable
fun Titulo(texto: String, apoio: String? = null) {
    Column(Modifier.padding(bottom = 12.dp)) {
        Text(texto, style = MaterialTheme.typography.headlineSmall)
        if (apoio != null) {
            Text(
                apoio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun Aviso(texto: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Text(
            texto,
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun BarraTarefa(tarefa: Tarefa, onCancelar: () -> Unit, modifier: Modifier = Modifier) {
    val rotulo = when (tarefa.fase) {
        Fase.BAIXANDO -> "Baixando"
        Fase.VERIFICANDO -> "Verificando SHA-256"
        Fase.INSTALANDO -> "Instalando"
    }
    Card(modifier = modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(tarefa.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (tarefa.fase == Fase.BAIXANDO && tarefa.progresso >= 0f) {
                            "$rotulo — ${(tarefa.progresso * 100).toInt()}%"
                        } else {
                            rotulo
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                TextButton(onClick = onCancelar) { Text("Cancelar") }
            }
            Spacer(Modifier.height(8.dp))
            if (tarefa.fase == Fase.BAIXANDO && tarefa.progresso >= 0f) {
                LinearProgressIndicator(
                    progress = { tarefa.progresso },
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun LinhaInfo(rotulo: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(
            rotulo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(6.dp))
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

fun formatarTamanho(bytes: Long): String = when {
    bytes <= 0 -> ""
    bytes >= 1_000_000_000 -> "%.1f GB".format(bytes / 1_000_000_000.0)
    bytes >= 1_000_000 -> "%.0f MB".format(bytes / 1_000_000.0)
    else -> "%.0f KB".format(bytes / 1_000.0)
}

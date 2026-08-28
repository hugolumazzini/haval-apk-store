package br.com.apkbox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.apkbox.Fase
import br.com.apkbox.Tarefa
import br.com.apkbox.ui.theme.Cores
import br.com.apkbox.ui.theme.EstiloSecao

/** Raio usado por todo cartão e botão retangular da interface. */
val FormaCartao = RoundedCornerShape(12.dp)

/** Botões são pílulas, como no Impulse. */
val FormaBotao = RoundedCornerShape(8.dp)

/**
 * Cabeçalho de seção: maiúsculas espaçadas em Michroma, com o texto de apoio
 * numa linha discreta abaixo.
 */
@Composable
fun Titulo(texto: String, apoio: String? = null) {
    Column(Modifier.padding(bottom = 16.dp)) {
        Text(texto.uppercase(), style = EstiloSecao)
        if (apoio != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                apoio,
                style = MaterialTheme.typography.bodyMedium,
                color = Cores.TextoApoio,
            )
        }
    }
}

/** Cartão-base: fundo de superfície, sem sombra, cantos de 12. */
@Composable
fun Bloco(
    modifier: Modifier = Modifier,
    cor: Color = Cores.Superficie,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = FormaCartao,
        colors = CardDefaults.cardColors(containerColor = cor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) { content() }
}

@Composable
fun Aviso(texto: String, modifier: Modifier = Modifier) {
    Bloco(modifier = modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Cores.TextoApoio,
        )
    }
}

/** Ação primária: pílula azul cheia. */
@Composable
fun BotaoPrimario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cor: Color = Cores.Destaque,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = FormaBotao,
        modifier = modifier.height(46.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = cor,
            contentColor = Cores.Fundo,
            disabledContainerColor = Cores.Campo,
            disabledContentColor = Cores.TextoApoio,
        ),
    ) { Text(texto, style = MaterialTheme.typography.labelLarge) }
}

/** Ação secundária: só contorno. */
@Composable
fun BotaoSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = FormaBotao,
        modifier = modifier.height(46.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Cores.TextoCorrido),
    ) { Text(texto, style = MaterialTheme.typography.labelLarge) }
}

/** Campo de entrada: retângulo cinza com sublinhado que acende em azul no foco. */
@Composable
fun CampoTexto(
    valor: String,
    onValorMudou: (String) -> Unit,
    modifier: Modifier = Modifier,
    dica: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    TextField(
        value = valor,
        onValueChange = onValorMudou,
        singleLine = true,
        modifier = modifier,
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = keyboardOptions,
        placeholder = dica?.let {
            {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Cores.TextoApoio,
                )
            }
        },
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Cores.Campo,
            unfocusedContainerColor = Cores.Campo,
            disabledContainerColor = Cores.Campo,
            focusedTextColor = Cores.Texto,
            unfocusedTextColor = Cores.TextoCorrido,
            cursorColor = Cores.Destaque,
            focusedIndicatorColor = Cores.Destaque,
            unfocusedIndicatorColor = Cores.Contorno,
        ),
    )
}

@Composable
fun BarraTarefa(tarefa: Tarefa, onCancelar: () -> Unit, modifier: Modifier = Modifier) {
    val rotulo = when (tarefa.fase) {
        Fase.BAIXANDO -> "Baixando"
        Fase.VERIFICANDO -> "Verificando SHA-256"
        Fase.INSTALANDO -> "Instalando"
    }
    Bloco(modifier = modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        tarefa.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = Cores.Texto,
                    )
                    Text(
                        if (tarefa.fase == Fase.BAIXANDO && tarefa.progresso >= 0f) {
                            "$rotulo — ${(tarefa.progresso * 100).toInt()}%"
                        } else {
                            rotulo
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Cores.TextoApoio,
                    )
                }
                TextButton(onClick = onCancelar) {
                    Text(
                        "Cancelar",
                        style = MaterialTheme.typography.labelLarge,
                        color = Cores.Destaque,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            val barra = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
            if (tarefa.fase == Fase.BAIXANDO && tarefa.progresso >= 0f) {
                LinearProgressIndicator(
                    progress = { tarefa.progresso },
                    modifier = barra,
                    color = Cores.Destaque,
                    trackColor = Cores.Campo,
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                )
            } else {
                LinearProgressIndicator(
                    modifier = barra,
                    color = Cores.Destaque,
                    trackColor = Cores.Campo,
                )
            }
        }
    }
}

@Composable
fun LinhaInfo(rotulo: String, valor: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(rotulo, style = MaterialTheme.typography.bodyMedium, color = Cores.TextoApoio)
        Text(
            valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Cores.TextoCorrido,
        )
    }
}

fun formatarTamanho(bytes: Long): String = when {
    bytes <= 0 -> ""
    bytes >= 1_000_000_000 -> "%.1f GB".format(bytes / 1_000_000_000.0)
    bytes >= 1_000_000 -> "%.0f MB".format(bytes / 1_000_000.0)
    else -> "%.0f KB".format(bytes / 1_000.0)
}

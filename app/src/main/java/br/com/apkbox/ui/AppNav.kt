package br.com.apkbox.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.apkbox.AppViewModel
import br.com.apkbox.ui.screens.CatalogScreen
import br.com.apkbox.ui.screens.InstalledScreen
import br.com.apkbox.ui.screens.SettingsScreen
import br.com.apkbox.ui.screens.UrlInstallScreen
import br.com.apkbox.ui.theme.Cores
import br.com.apkbox.ui.theme.EstiloMarca

private enum class Aba(val titulo: String, val icone: ImageVector) {
    CATALOGO("Catálogo", Icons.Filled.ShoppingCart),
    URL("Por URL", IconeDownload),
    INSTALADOS("Instalados", Icons.Filled.List),
    AJUSTES("Ajustes", Icons.Filled.Settings),
}

private val LarguraLateral = 232.dp

@Composable
fun AppNav(vm: AppViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    var aba by rememberSaveable { mutableStateOf(Aba.CATALOGO) }
    val snackbar = remember { SnackbarHostState() }

    val desinstalar = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        vm.aoVoltarDaDesinstalacao(resultado.resultCode == Activity.RESULT_OK)
    }

    LaunchedEffect(state.mensagem) {
        val msg = state.mensagem ?: return@LaunchedEffect
        snackbar.showSnackbar(msg, duration = SnackbarDuration.Long)
        vm.limparMensagem()
    }

    Scaffold(
        containerColor = Cores.Fundo,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = {
            SnackbarHost(snackbar) { dados ->
                Snackbar(
                    shape = RoundedCornerShape(12.dp),
                    containerColor = if (state.erro) Cores.Erro else Cores.Campo,
                    contentColor = Cores.Texto,
                ) { Text(dados.visuals.message, style = MaterialTheme.typography.bodyLarge) }
            }
        },
    ) { insets ->
        Row(Modifier.fillMaxSize().padding(insets)) {
            BarraLateral(
                abaAtual = aba,
                onSelecionar = { aba = it },
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(PaddingValues(start = 28.dp, end = 28.dp, top = 18.dp, bottom = 12.dp))
            ) {
                when (aba) {
                    Aba.CATALOGO -> CatalogScreen(vm, state)
                    Aba.URL -> UrlInstallScreen(vm, state)
                    Aba.INSTALADOS -> InstalledScreen(
                        vm = vm,
                        state = state,
                        onDesinstalar = { desinstalar.launch(vm.intentDesinstalar(it)) },
                    )
                    Aba.AJUSTES -> SettingsScreen(vm, state)
                }
            }
        }
    }
}

@Composable
private fun BarraLateral(abaAtual: Aba, onSelecionar: (Aba) -> Unit) {
    Column(
        Modifier
            .width(LarguraLateral)
            .fillMaxHeight()
            .background(Cores.Lateral)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            "APK BOX",
            style = EstiloMarca,
            modifier = Modifier.padding(start = 10.dp, top = 26.dp, bottom = 22.dp),
        )

        Aba.entries.forEach { item ->
            ItemLateral(
                item = item,
                selecionado = abaAtual == item,
                onClick = { onSelecionar(item) },
            )
        }
    }
}

@Composable
private fun ItemLateral(item: Aba, selecionado: Boolean, onClick: () -> Unit) {
    val cor = if (selecionado) Cores.Destaque else Cores.Texto
    val forma = RoundedCornerShape(10.dp)

    Box(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(forma)
            .background(if (selecionado) Cores.SuperficieSelecionada else Cores.Lateral)
            .clickable(onClick = onClick),
    ) {
        // Barra de acento à esquerda, só no item ativo.
        if (selecionado) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(3.dp)
                    .align(Alignment.CenterStart)
                    .background(Cores.Destaque),
            )
        }

        Row(
            Modifier.fillMaxSize().padding(start = 14.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(item.icone, contentDescription = null, tint = cor, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(14.dp))
            Text(
                item.titulo,
                style = MaterialTheme.typography.bodyLarge,
                color = cor,
            )
        }
    }
}

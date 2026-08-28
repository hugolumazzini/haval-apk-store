package br.com.apkbox.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.apkbox.AppViewModel
import br.com.apkbox.ui.screens.CatalogScreen
import br.com.apkbox.ui.screens.InstalledScreen
import br.com.apkbox.ui.screens.SettingsScreen
import br.com.apkbox.ui.screens.UrlInstallScreen

private enum class Aba(val titulo: String, val glifo: String) {
    CATALOGO("Catálogo", "▦"),
    URL("Por URL", "⤓"),
    INSTALADOS("Instalados", "☰"),
    AJUSTES("Ajustes", "⚙"),
}

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
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = {
            SnackbarHost(snackbar) { dados ->
                Snackbar(
                    containerColor = if (state.erro) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.inverseSurface
                    },
                    contentColor = if (state.erro) {
                        MaterialTheme.colorScheme.onError
                    } else {
                        MaterialTheme.colorScheme.inverseOnSurface
                    },
                ) { Text(dados.visuals.message) }
            }
        },
    ) { insets ->
        Row(Modifier.fillMaxSize().padding(insets)) {
            NavigationRail(containerColor = MaterialTheme.colorScheme.surface) {
                Aba.entries.forEach { item ->
                    NavigationRailItem(
                        selected = aba == item,
                        onClick = { aba = item },
                        icon = { Text(item.glifo, style = MaterialTheme.typography.titleLarge) },
                        label = { Text(item.titulo) },
                        alwaysShowLabel = true,
                    )
                }
            }

            Box(Modifier.fillMaxSize().padding(PaddingValues(horizontal = 20.dp, vertical = 12.dp))) {
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

package br.com.apkbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import br.com.apkbox.ui.AppNav
import br.com.apkbox.ui.theme.ApkBoxTheme

class MainActivity : ComponentActivity() {

    private val vm: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ApkBoxTheme {
                AppNav(vm)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Volta da tela de "fontes desconhecidas" ou de uma instalação: reflete o estado real.
        vm.revalidarPermissao()
        vm.recarregarInstalados()
    }
}

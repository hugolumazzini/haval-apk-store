package br.com.hugolumazzini.havalapkstore.install

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface InstallEvent {
    data class Sucesso(val packageName: String?) : InstallEvent
    data class Falha(val mensagem: String) : InstallEvent
}

/**
 * Ponte entre o BroadcastReceiver declarado no manifest e a ViewModel.
 * Ambos vivem no mesmo processo, então um SharedFlow global resolve.
 */
object InstallEvents {
    private val _events = MutableSharedFlow<InstallEvent>(extraBufferCapacity = 16)
    val events = _events.asSharedFlow()

    fun publicar(event: InstallEvent) {
        _events.tryEmit(event)
    }
}

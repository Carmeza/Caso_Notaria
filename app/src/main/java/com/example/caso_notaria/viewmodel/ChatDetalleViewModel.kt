package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.network.RetrofitClient
import com.example.caso_notaria.network.llamarApi
import com.example.caso_notaria.repository.AUTOR_SISTEMA
import com.example.caso_notaria.repository.ChatRepository
import com.example.caso_notaria.repository.EventoRepository
import com.example.caso_notaria.repository.SesionManager
import com.example.caso_notaria.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Un mensaje como se muestra en el chat
data class MensajeUi(
    val id: Int,
    val texto: String,
    val hora: String,
    val esMio: Boolean,
    val esSistema: Boolean,
    val emisor: String
)

// Estado del chat de un evento
data class ChatDetalleUiState(
    val titulo: String = "",
    val mensajes: List<MensajeUi> = emptyList(),
    val texto: String = ""
)

class ChatDetalleViewModel(application: Application) : AndroidViewModel(application) {

    private val sesion = SesionManager(application)
    private val chatRepository = ChatRepository(RetrofitClient.getApi(sesion))
    private val eventoRepository = EventoRepository(RetrofitClient.getApi(sesion))
    private val usuarioRepository = UsuarioRepository(RetrofitClient.getApi(sesion), sesion)

    private val _uiState = MutableStateFlow(ChatDetalleUiState())
    val uiState: StateFlow<ChatDetalleUiState> = _uiState.asStateFlow()

    private var eventoId: Int? = null
    private var usuarioId: Int? = null

    // El titulo del chat es el nombre del evento
    fun cargar(eventoId: Int) {
        this.eventoId = eventoId
        usuarioId = usuarioRepository.obtenerUsuarioActivo()?.id
        viewModelScope.launch {
            llamarApi { eventoRepository.obtenerEvento(eventoId) }.onSuccess { evento ->
                _uiState.update { it.copy(titulo = evento?.nombre ?: "") }
            }
            actualizarMensajes()
        }
    }

    fun onTextoChange(valor: String) {
        _uiState.update { it.copy(texto = valor) }
    }

    // Envia el mensaje a Xano y recarga la conversacion. Si falla, el texto queda escrito para reintentar
    fun enviar() {
        val evento = eventoId ?: return
        if (usuarioId == null) return
        val texto = _uiState.value.texto
        if (texto.isBlank()) return
        viewModelScope.launch {
            llamarApi { chatRepository.enviarMensaje(evento, texto) }.onSuccess {
                _uiState.update { it.copy(texto = "") }
                actualizarMensajes()
            }
        }
    }

    private suspend fun actualizarMensajes() {
        val evento = eventoId ?: return
        llamarApi { chatRepository.obtenerMensajes(evento) }.onSuccess { lista ->
            val mensajes = lista.map { mensaje ->
                val emisor = when (mensaje.autorId) {
                    AUTOR_SISTEMA -> "Mygos"
                    usuarioId -> "Tú"
                    else -> mensaje.autorAlias ?: "Jugador"
                }
                MensajeUi(
                    id = mensaje.id,
                    texto = mensaje.texto,
                    hora = formatearHora(mensaje.hora),
                    esMio = mensaje.autorId == usuarioId,
                    esSistema = mensaje.autorId == AUTOR_SISTEMA,
                    emisor = emisor
                )
            }
            _uiState.update { it.copy(mensajes = mensajes) }
        }
    }
}
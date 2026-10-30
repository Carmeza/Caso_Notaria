package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.model.AppDatabase
import com.example.caso_notaria.repository.AUTOR_SISTEMA
import com.example.caso_notaria.repository.ChatRepository
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

    private val db = AppDatabase.getDatabase(application)
    private val chatRepository = ChatRepository(db.grupoChatDao(), db.mensajeDao())
    private val usuarioRepository = UsuarioRepository(db.usuarioDao())

    private val _uiState = MutableStateFlow(ChatDetalleUiState())
    val uiState: StateFlow<ChatDetalleUiState> = _uiState.asStateFlow()

    private var grupoId: Int? = null
    private var usuarioId: Int? = null

    // Busca el grupo del evento y carga sus mensajes guardados
    fun cargar(eventoId: Int) {
        viewModelScope.launch {
            usuarioId = usuarioRepository.obtenerUsuarioActivo()?.id
            val grupo = chatRepository.obtenerGrupoDeEvento(eventoId) ?: return@launch
            grupoId = grupo.id
            _uiState.update { it.copy(titulo = grupo.nombre) }
            actualizarMensajes()
        }
    }

    fun onTextoChange(valor: String) {
        _uiState.update { it.copy(texto = valor) }
    }

    // Guarda el mensaje en Room y recarga la conversacion
    fun enviar() {
        val grupo = grupoId ?: return
        val autor = usuarioId ?: return
        val texto = _uiState.value.texto
        if (texto.isBlank()) return
        viewModelScope.launch {
            chatRepository.enviarMensaje(grupo, autor, texto)
            _uiState.update { it.copy(texto = "") }
            actualizarMensajes()
        }
    }

    private suspend fun actualizarMensajes() {
        val grupo = grupoId ?: return
        val mensajes = chatRepository.obtenerMensajes(grupo).map { mensaje ->
            val emisor = when (mensaje.autorId) {
                AUTOR_SISTEMA -> "Mygos"
                usuarioId -> "Tú"
                else -> usuarioRepository.obtenerUsuario(mensaje.autorId)?.alias ?: "Jugador"
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
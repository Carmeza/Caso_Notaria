package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.model.AppDatabase
import com.example.caso_notaria.repository.ChatRepository
import com.example.caso_notaria.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Un grupo como se muestra en la lista de chats
data class ChatPreview(
    val eventoId: Int,
    val titulo: String,
    val ultimoMensaje: String,
    val hora: String,
    val integrantes: Int,
    val fechaEvento: String
)

// Estado de la lista de chats
data class ChatsUiState(
    val cargando: Boolean = true,
    val chats: List<ChatPreview> = emptyList()
)

class ChatsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val chatRepository = ChatRepository(db.grupoChatDao(), db.mensajeDao())
    private val usuarioRepository = UsuarioRepository(db.usuarioDao())

    private val _uiState = MutableStateFlow(ChatsUiState())
    val uiState: StateFlow<ChatsUiState> = _uiState.asStateFlow()

    // Carga los grupos del usuario con la sesion iniciada
    fun cargarChats() {
        viewModelScope.launch {
            val usuario = usuarioRepository.obtenerUsuarioActivo()
            val chats = if (usuario == null) emptyList() else {
                chatRepository.obtenerChatsDelUsuario(usuario.id).map { chat ->
                    ChatPreview(
                        eventoId = chat.eventoId,
                        titulo = chat.titulo,
                        ultimoMensaje = when {
                            chat.ultimoMensaje == null -> "Sin mensajes aún"
                            chat.emisorUltimoMensaje == null -> chat.ultimoMensaje
                            else -> "${chat.emisorUltimoMensaje}: ${chat.ultimoMensaje}"
                        },
                        hora = chat.horaMs?.let { formatearHora(it) } ?: "",
                        integrantes = chat.integrantes,
                        fechaEvento = chat.fechaEvento
                    )
                }
            }
            _uiState.update { it.copy(cargando = false, chats = chats) }
        }
    }
}

// Convierte los milisegundos en "HH:mm"
fun formatearHora(horaMs: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(horaMs))
package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.network.RetrofitClient
import com.example.caso_notaria.network.llamarApi
import com.example.caso_notaria.repository.ChatRepository
import com.example.caso_notaria.repository.SesionManager
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

    private val sesion = SesionManager(application)
    private val chatRepository = ChatRepository(RetrofitClient.getApi(sesion))
    private val usuarioRepository = UsuarioRepository(RetrofitClient.getApi(sesion), sesion)

    private val _uiState = MutableStateFlow(ChatsUiState())
    val uiState: StateFlow<ChatsUiState> = _uiState.asStateFlow()

    // Carga los chats del usuario con la sesion iniciada. Si falla la conexion queda la lista vacia
    fun cargarChats() {
        viewModelScope.launch {
            val usuario = usuarioRepository.obtenerUsuarioActivo()
            if (usuario == null) {
                _uiState.update { it.copy(cargando = false, chats = emptyList()) }
                return@launch
            }
            val chats = llamarApi { chatRepository.obtenerChatsDelUsuario(usuario.id) }.getOrDefault(emptyList())
            _uiState.update {
                it.copy(
                    cargando = false,
                    chats = chats.map { chat ->
                        val mensaje = chat.ultimoMensaje
                        ChatPreview(
                            eventoId = chat.eventoId,
                            titulo = chat.titulo,
                            ultimoMensaje = when {
                                mensaje == null -> "Sin mensajes aún"
                                mensaje.autorAlias == null -> mensaje.texto
                                else -> "${mensaje.autorAlias}: ${mensaje.texto}"
                            },
                            hora = mensaje?.let { m -> formatearHora(m.hora) } ?: "",
                            integrantes = chat.integrantes,
                            fechaEvento = chat.fechaEvento
                        )
                    }
                )
            }
        }
    }
}

// Convierte los milisegundos en "HH:mm"
fun formatearHora(horaMs: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(horaMs))
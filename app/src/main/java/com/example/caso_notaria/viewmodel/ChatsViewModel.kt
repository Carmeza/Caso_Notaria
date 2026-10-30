package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.model.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatPreview(
    val id: Int,
    val titulo: String,
    val ultimoMensaje: String,
    val emisorUltimoMensaje: String,
    val hora: String,
    val mensajesSinLeer: Int,
    val integrantes: Int,
    val fechaEvento: String
)

// Usamos AndroidViewModel para que reciba el application y no rompa tus pantallas
class ChatsViewModel(application: Application) : AndroidViewModel(application) {

    // Conexión directa a tu base de datos Room
    private val dao = AppDatabase.getDatabase(application).grupoChatDao()

    private val _chats = MutableStateFlow<List<ChatPreview>>(emptyList())
    val chats: StateFlow<List<ChatPreview>> = _chats.asStateFlow()

    // Solo le pasas el ID del usuario que inicia sesión
    fun cargarChatsRegistrados(usuarioId: Int) {
        viewModelScope.launch {
            try {
                val chatsBd = dao.obtenerChatsDeUsuario(usuarioId)
                _chats.value = chatsBd.map { chat ->
                    ChatPreview(
                        id = chat.id,
                        titulo = chat.titulo,
                        ultimoMensaje = chat.ultimoMensaje ?: "Sin mensajes aún",
                        emisorUltimoMensaje = chat.emisorUltimoMensaje ?: "",
                        hora = formatearHora(chat.horaMs),
                        mensajesSinLeer = 0,
                        integrantes = chat.integrantes,
                        fechaEvento = chat.fechaEvento
                    )
                }
            } catch (e: Exception) {
                _chats.value = emptyList()
            }
        }
    }

    fun limpiarChats() {
        _chats.value = emptyList()
    }

    fun marcarComoLeido(chatId: Int) {
        _chats.value = _chats.value.map { chat ->
            if (chat.id == chatId) chat.copy(mensajesSinLeer = 0) else chat
        }
    }

    fun crearChatVacio(torneoId: Int, nombreTorneo: String, fecha: String) {
        if (_chats.value.none { it.id == torneoId }) {
            val nuevoChat = ChatPreview(
                id = torneoId,
                titulo = nombreTorneo,
                ultimoMensaje = "",
                emisorUltimoMensaje = "",
                hora = "Ahora",
                mensajesSinLeer = 0,
                integrantes = 1,
                fechaEvento = fecha
            )
            _chats.value = listOf(nuevoChat) + _chats.value
        }
    }

    private fun formatearHora(horaMs: Long?): String {
        if (horaMs == null) return ""
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(horaMs))
    }
}
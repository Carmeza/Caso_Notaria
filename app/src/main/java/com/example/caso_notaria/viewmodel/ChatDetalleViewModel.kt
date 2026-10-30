package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.model.AppDatabase
import com.example.caso_notaria.model.Mensaje
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MensajeUi(
    val id: Int = 0,
    val texto: String,
    val hora: String,
    val esMio: Boolean,
    val emisor: String = "",
    val iniciales: String = ""
)

class ChatDetalleViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val mensajeDao = db.mensajeDao()
    private val usuarioDao = db.usuarioDao()
    private val eventoDao = db.eventoDao()
    private val grupoChatDao = db.grupoChatDao()

    private val _mensajes = MutableStateFlow<List<MensajeUi>>(emptyList())
    val mensajes: StateFlow<List<MensajeUi>> = _mensajes.asStateFlow()

    private val _tituloChat = MutableStateFlow("Chat del Torneo")
    val tituloChat: StateFlow<String> = _tituloChat.asStateFlow()

    private var chatActualId: Int = 0
    private var miUsuarioId: Int = 1

    fun cargarMensajes(chatId: Int, usuarioActualId: Int = 1) {
        this.chatActualId = chatId
        this.miUsuarioId = usuarioActualId

        viewModelScope.launch {
            try {
                // Buscamos el nombre del evento asociado a este grupo
                val evento = eventoDao.getById(chatId)
                if (evento != null) {
                    _tituloChat.value = evento.nombre
                }

                val mensajesBd = mensajeDao.getByGrupoId(chatId)

                val mensajesMapeados = mensajesBd.map { mensaje ->
                    val esMio = mensaje.autorId == miUsuarioId

                    val usuarioBd = usuarioDao.getById(mensaje.autorId)
                    val nombreEmisor = if (esMio) {
                        "Tú"
                    } else {
                        usuarioBd?.alias ?: usuarioBd?.correo ?: "Usuario ${mensaje.autorId}"
                    }

                    val inicial = if (nombreEmisor.isNotBlank()) nombreEmisor.take(1).uppercase(Locale.getDefault()) else "U"

                    MensajeUi(
                        id = mensaje.id,
                        texto = mensaje.texto,
                        hora = formatearHora(mensaje.hora),
                        esMio = esMio,
                        emisor = nombreEmisor,
                        iniciales = inicial
                    )
                }

                _mensajes.value = mensajesMapeados

            } catch (e: Exception) {
                _mensajes.value = emptyList()
            }
        }
    }

    fun enviarMensaje(texto: String) {
        if (texto.isNotBlank()) {
            val tiempoActualMs = System.currentTimeMillis()

            val nuevoMensajeBd = Mensaje(
                grupoId = chatActualId,
                autorId = miUsuarioId,
                texto = texto.trim(),
                hora = tiempoActualMs
            )

            viewModelScope.launch {
                mensajeDao.insert(nuevoMensajeBd)
                cargarMensajes(chatActualId, miUsuarioId)
            }
        }
    }

    private fun formatearHora(horaMs: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(horaMs))
    }
}
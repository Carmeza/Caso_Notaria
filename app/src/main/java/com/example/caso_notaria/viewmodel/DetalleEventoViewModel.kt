package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.model.AppDatabase
import com.example.caso_notaria.model.Evento
import com.example.caso_notaria.model.Usuario
import com.example.caso_notaria.repository.EventoRepository
import com.example.caso_notaria.repository.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la pantalla de detalle del evento
data class DetalleEventoUiState(
    val cargando: Boolean = true,
    val evento: Evento? = null,
    val organizador: String = "",
    val inscritos: Int = 0,
    val participantes: List<Usuario> = emptyList(),
    val usuarioId: Int? = null,
    val estaInscrito: Boolean = false,
    val inscribiendo: Boolean = false,
    val mostrarConfirmacion: Boolean = false,
    val mensaje: String? = null
) {
    val cuposLibres: Int get() = (evento?.cupos ?: 0) - inscritos
    val esOrganizador: Boolean get() = evento != null && evento.organizadorId == usuarioId
}

class DetalleEventoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val eventoRepository = EventoRepository(db.eventoDao(), db.inscripcionDao(), db.grupoChatDao(), db.usuarioDao())
    private val usuarioRepository = UsuarioRepository(db.usuarioDao())

    private val _uiState = MutableStateFlow(DetalleEventoUiState())
    val uiState: StateFlow<DetalleEventoUiState> = _uiState.asStateFlow()

    fun cargar(eventoId: Int) {
        viewModelScope.launch {
            val evento = eventoRepository.obtenerEvento(eventoId)
            if (evento == null) {
                _uiState.update { it.copy(cargando = false, mensaje = "No encontramos este evento") }
                return@launch
            }
            val usuario = usuarioRepository.obtenerUsuarioActivo()
            val organizador = usuarioRepository.obtenerUsuario(evento.organizadorId)
            _uiState.update {
                it.copy(
                    cargando = false,
                    evento = evento,
                    organizador = organizador?.alias ?: "Organizador",
                    usuarioId = usuario?.id,
                    estaInscrito = usuario != null && eventoRepository.estaInscrito(eventoId, usuario.id)
                )
            }
            actualizarParticipantes(eventoId)
        }
    }

        // Muestra la carga, inscribe con el repositorio y abre la confirmacion
        fun inscribirse() {
        val estado = _uiState.value
        val evento = estado.evento ?: return
        val usuarioId = estado.usuarioId
        if (usuarioId == null) {
            _uiState.update { it.copy(mensaje = "Inicia sesión para inscribirte") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(inscribiendo = true, mensaje = null) }
            delay(800) // espera breve para mostrar la carga en el boton
            val inscrito = eventoRepository.inscribirse(evento.id, usuarioId)
            if (inscrito) {
                actualizarParticipantes(evento.id)
                _uiState.update { it.copy(inscribiendo = false, estaInscrito = true, mostrarConfirmacion = true) }
            } else {
                _uiState.update { it.copy(inscribiendo = false, mensaje = "No quedan cupos en este evento") }
            }
        }
    }

    fun cerrarConfirmacion() {
        _uiState.update { it.copy(mostrarConfirmacion = false) }
    }

    private suspend fun actualizarParticipantes(eventoId: Int) {
        val participantes = eventoRepository.obtenerParticipantes(eventoId)
        _uiState.update { it.copy(participantes = participantes, inscritos = participantes.size) }
    }
}
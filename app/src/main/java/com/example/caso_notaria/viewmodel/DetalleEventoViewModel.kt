package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.model.Evento
import com.example.caso_notaria.model.Usuario
import com.example.caso_notaria.network.RetrofitClient
import com.example.caso_notaria.network.llamarApi
import com.example.caso_notaria.network.mensajeDeError
import com.example.caso_notaria.repository.EventoRepository
import com.example.caso_notaria.repository.SesionManager
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

    private val sesion = SesionManager(application)
    private val eventoRepository = EventoRepository(RetrofitClient.getApi(sesion))
    private val usuarioRepository = UsuarioRepository(RetrofitClient.getApi(sesion), sesion)

    private val _uiState = MutableStateFlow(DetalleEventoUiState())
    val uiState: StateFlow<DetalleEventoUiState> = _uiState.asStateFlow()

    // Pide el evento y sus participantes. Con los participantes se sabe si el usuario ya esta inscrito
    fun cargar(eventoId: Int) {
        viewModelScope.launch {
            val usuario = usuarioRepository.obtenerUsuarioActivo()
            val resultado = llamarApi {
                val evento = eventoRepository.obtenerEvento(eventoId)
                val participantes = if (evento == null) emptyList() else eventoRepository.obtenerParticipantes(eventoId)
                evento to participantes
            }
            resultado.onFailure { error ->
                _uiState.update { it.copy(cargando = false, mensaje = mensajeDeError(error)) }
                return@launch
            }
            val (evento, participantes) = resultado.getOrThrow()
            if (evento == null) {
                _uiState.update { it.copy(cargando = false, mensaje = "No encontramos este evento") }
                return@launch
            }
            _uiState.update {
                it.copy(
                    cargando = false,
                    evento = evento,
                    organizador = evento.organizadorAlias.ifBlank { "Organizador" },
                    usuarioId = usuario?.id,
                    estaInscrito = participantes.any { participante -> participante.id == usuario?.id },
                    participantes = participantes,
                    inscritos = participantes.size
                )
            }
        }
    }

    // Muestra la carga, pide la inscripcion a Xano y abre la confirmacion
    fun inscribirse() {
        val estado = _uiState.value
        val evento = estado.evento ?: return
        if (estado.usuarioId == null) {
            _uiState.update { it.copy(mensaje = "Inicia sesión para inscribirte") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(inscribiendo = true, mensaje = null) }
            delay(800)
            llamarApi { eventoRepository.inscribirse(evento.id) }
                .onSuccess { motivo ->
                    if (motivo == null) {
                        actualizarParticipantes(evento.id)
                        _uiState.update { it.copy(inscribiendo = false, estaInscrito = true, mostrarConfirmacion = true) }
                    } else {
                        _uiState.update { it.copy(inscribiendo = false, mensaje = motivo) }
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(inscribiendo = false, mensaje = mensajeDeError(error)) }
                }
        }
    }

    fun cerrarConfirmacion() {
        _uiState.update { it.copy(mostrarConfirmacion = false) }
    }

    private suspend fun actualizarParticipantes(eventoId: Int) {
        llamarApi { eventoRepository.obtenerParticipantes(eventoId) }.onSuccess { participantes ->
            _uiState.update { it.copy(participantes = participantes, inscritos = participantes.size) }
        }
    }
}
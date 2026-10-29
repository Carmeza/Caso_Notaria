package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.model.AppDatabase
import com.example.caso_notaria.model.Usuario
import com.example.caso_notaria.repository.EventoRepository
import com.example.caso_notaria.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la lista de participantes de un evento
data class ParticipantesUiState(
    val cargando: Boolean = true,
    val nombreEvento: String = "",
    val participantes: List<Usuario> = emptyList(),
    val usuarioId: Int? = null
)

class ParticipantesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val eventoRepository = EventoRepository(db.eventoDao(), db.inscripcionDao(), db.grupoChatDao(), db.usuarioDao())
    private val usuarioRepository = UsuarioRepository(db.usuarioDao())

    private val _uiState = MutableStateFlow(ParticipantesUiState())
    val uiState: StateFlow<ParticipantesUiState> = _uiState.asStateFlow()

    fun cargar(eventoId: Int) {
        viewModelScope.launch {
            val evento = eventoRepository.obtenerEvento(eventoId)
            val participantes = eventoRepository.obtenerParticipantes(eventoId)
            val usuario = usuarioRepository.obtenerUsuarioActivo()
            _uiState.update {
                it.copy(
                    cargando = false,
                    nombreEvento = evento?.nombre ?: "",
                    participantes = participantes,
                    usuarioId = usuario?.id
                )
            }
        }
    }
}
package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.model.AppDatabase
import com.example.caso_notaria.repository.ChatRepository
import com.example.caso_notaria.repository.EventoRepository
import com.example.caso_notaria.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Un evento inscrito como se muestra en el perfil
data class EventoInscritoUi(
    val id: Int,
    val dia: String,
    val mes: String,
    val titulo: String,
    val detalle: String
)

// Estado de la pantalla de perfil
data class PerfilUiState(
    val cargando: Boolean = true,
    val alias: String = "",
    val nivel: String = "",
    val correo: String = "",
    val juegoPreferido: String = "",
    val plataforma: String = "",
    val eventosInscritos: List<EventoInscritoUi> = emptyList(),
    val eventosOrganizados: Int = 0,
    val chats: Int = 0,
    val mostrarConfirmacionCierre: Boolean = false
)

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val usuarioRepository = UsuarioRepository(db.usuarioDao())
    private val eventoRepository = EventoRepository(
        db.eventoDao(),
        db.inscripcionDao(),
        db.grupoChatDao(),
        db.usuarioDao(),
        db.mensajeDao()
    )
    private val chatRepository = ChatRepository(db.grupoChatDao(), db.mensajeDao())

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    // Carga los datos del usuario con la sesion iniciada
    fun cargarPerfil() {
        viewModelScope.launch {
            val usuario = usuarioRepository.obtenerUsuarioActivo()
            if (usuario == null) {
                _uiState.update { it.copy(cargando = false) }
                return@launch
            }
            val inscritos = eventoRepository.obtenerEventosInscritos(usuario.id).map { evento ->
                // La fecha viene como "Sáb 7 nov". se separa el dia y el mes
                val partes = evento.fecha.split(" ")
                EventoInscritoUi(
                    id = evento.id,
                    dia = partes.getOrNull(1) ?: "",
                    mes = partes.getOrNull(2)?.uppercase() ?: "",
                    titulo = evento.nombre,
                    detalle = "${evento.hora} · ${if (evento.modalidad == "Online") "Online" else evento.lugar}"
                )
            }
            _uiState.update {
                it.copy(
                    cargando = false,
                    alias = usuario.alias,
                    nivel = usuario.nivel,
                    correo = usuario.correo,
                    juegoPreferido = usuario.juegoPreferido.ifBlank { "Sin definir" },
                    plataforma = usuario.plataforma.ifBlank { "Sin definir" },
                    eventosInscritos = inscritos,
                    eventosOrganizados = eventoRepository.obtenerEventosOrganizados(usuario.id).size,
                    chats = chatRepository.obtenerGruposDelUsuario(usuario.id).size
                )
            }
        }
    }

    fun pedirCerrarSesion() {
        _uiState.update { it.copy(mostrarConfirmacionCierre = true) }
    }

    fun cancelarCerrarSesion() {
        _uiState.update { it.copy(mostrarConfirmacionCierre = false) }
    }

    // Deja la sesion cerrada en Room y despues avisa a la pantalla
    fun cerrarSesion(onListo: () -> Unit) {
        viewModelScope.launch {
            usuarioRepository.cerrarSesion()
            _uiState.update { it.copy(mostrarConfirmacionCierre = false) }
            onListo()
        }
    }
}
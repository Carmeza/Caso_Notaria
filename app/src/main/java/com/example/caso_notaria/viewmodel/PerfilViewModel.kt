package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.network.RetrofitClient
import com.example.caso_notaria.network.llamarApi
import com.example.caso_notaria.repository.EventoRepository
import com.example.caso_notaria.repository.SesionManager
import com.example.caso_notaria.repository.UsuarioRepository
import com.example.caso_notaria.repository.eventosDelUsuario
import com.example.caso_notaria.repository.eventosInscritos
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

    private val sesion = SesionManager(application)
    private val usuarioRepository = UsuarioRepository(RetrofitClient.getApi(sesion), sesion)
    private val eventoRepository = EventoRepository(RetrofitClient.getApi(sesion))

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    // Los datos del usuario vienen de la sesion guardada y los eventos de Xano
    fun cargarPerfil() {
        viewModelScope.launch {
            val usuario = usuarioRepository.obtenerUsuarioActivo()
            if (usuario == null) {
                _uiState.update { it.copy(cargando = false) }
                return@launch
            }
            _uiState.update {
                it.copy(
                    alias = usuario.alias,
                    nivel = usuario.nivel,
                    correo = usuario.correo,
                    juegoPreferido = usuario.juegoPreferido.ifBlank { "Sin definir" },
                    plataforma = usuario.plataforma.ifBlank { "Sin definir" }
                )
            }
            llamarApi {
                eventoRepository.obtenerEventos() to eventoRepository.obtenerInscripciones()
            }.onSuccess { (eventos, inscripciones) ->
                val inscritos = eventosInscritos(eventos, inscripciones, usuario.id).map { evento ->
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
                        eventosInscritos = inscritos,
                        eventosOrganizados = eventos.count { evento -> evento.organizadorId == usuario.id },
                        chats = eventosDelUsuario(eventos, inscripciones, usuario.id).size
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    fun pedirCerrarSesion() {
        _uiState.update { it.copy(mostrarConfirmacionCierre = true) }
    }

    fun cancelarCerrarSesion() {
        _uiState.update { it.copy(mostrarConfirmacionCierre = false) }
    }

    // Borra el token del telefono y despues avisa a la pantalla
    fun cerrarSesion(onListo: () -> Unit) {
        usuarioRepository.cerrarSesion()
        _uiState.update { it.copy(mostrarConfirmacionCierre = false) }
        onListo()
    }
}
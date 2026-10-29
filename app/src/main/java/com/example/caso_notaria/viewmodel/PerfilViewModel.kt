package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.model.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventoInscritoUi(
    val id: Int,
    val dia: String,
    val mes: String,
    val titulo: String,
    val detalle: String
)

data class PerfilUiState(
    val iniciales: String = "Cargando...",
    val usuario: String = "Cargando...",
    val categoria: String = "Intermedio",
    val torneosJugados: Int = 0,
    val torneosActuales: Int = 0,
    val torneosOrganizados: Int = 0,
    val correo: String = "",
    val juegoPreferido: String = "",
    val plataforma: String = "",
    val eventosInscritos: List<EventoInscritoUi> = emptyList(),
    val cargando: Boolean = true
)

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val usuarioDao = db.usuarioDao()
    private val eventoDao = db.eventoDao()

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    fun cargarPerfilUsuario(usuarioId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cargando = true)
            try {
                // Consultas directas a través de tus DAOs
                val usuario = usuarioDao.getById(usuarioId)
                val eventosInscritos = eventoDao.obtenerInscritosPorUsuario(usuarioId)
                val eventosOrganizados = eventoDao.obtenerOrganizadosPorUsuario(usuarioId)

                if (usuario != null) {
                    val listaEventosUi = eventosInscritos.map { evento ->
                        val partesFecha = evento.fecha.split(" ")
                        val dia = partesFecha.getOrNull(1) ?: "01"
                        val mes = partesFecha.getOrNull(2)?.uppercase() ?: "NOV"

                        EventoInscritoUi(
                            id = evento.id,
                            dia = dia,
                            mes = mes,
                            titulo = evento.nombre,
                            detalle = "${evento.hora} · ${if (evento.modalidad == "Online") "Online" else evento.lugar}"
                        )
                    }

                    val iniciales = if (usuario.alias.length >= 2) {
                        usuario.alias.take(2).uppercase()
                    } else "US"

                    _uiState.value = PerfilUiState(
                        iniciales = iniciales,
                        usuario = usuario.alias,
                        categoria = usuario.nivel,
                        torneosJugados = eventosInscritos.size,
                        torneosActuales = eventosInscritos.size,
                        torneosOrganizados = eventosOrganizados.size,
                        correo = usuario.correo,
                        juegoPreferido = usuario.juegoPreferido,
                        plataforma = usuario.plataforma,
                        eventosInscritos = listaEventosUi,
                        cargando = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(cargando = false)
            }
        }
    }

    fun cerrarSesion(onLogout: () -> Unit) {
        onLogout()
    }
}
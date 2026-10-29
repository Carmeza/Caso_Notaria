package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.model.AppDatabase
import com.example.caso_notaria.model.Evento
import com.example.caso_notaria.repository.EventoRepository
import com.example.caso_notaria.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Normalizer

// Un evento con la cantidad de inscritos para la tarjeta
data class EventoConInscritos(val evento: Evento, val inscritos: Int)

// Estado de la pantalla de eventos
data class EventosUiState(
    val cargando: Boolean = true,
    val aliasUsuario: String = "",
    val eventos: List<EventoConInscritos> = emptyList(),
    val eventosFiltrados: List<EventoConInscritos> = emptyList(),
    val busqueda: String = "",
    val tipoSeleccionado: String = "Todos"
)

class EventosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val eventoRepository = EventoRepository(db.eventoDao(), db.inscripcionDao(), db.grupoChatDao(), db.usuarioDao())
    private val usuarioRepository = UsuarioRepository(db.usuarioDao())

    private val _uiState = MutableStateFlow(EventosUiState())
    val uiState: StateFlow<EventosUiState> = _uiState.asStateFlow()

    // Se llama cada vez que se abre la pantalla, asi aparecen los eventos nuevos
    fun cargarEventos() {
        viewModelScope.launch {
            val usuario = usuarioRepository.obtenerUsuarioActivo()
            val lista = eventoRepository.obtenerEventos().map { evento ->
                EventoConInscritos(evento, eventoRepository.contarInscritos(evento.id))
            }
            _uiState.update {
                it.copy(
                    cargando = false,
                    aliasUsuario = usuario?.alias ?: "",
                    eventos = lista,
                    eventosFiltrados = filtrar(lista, it.busqueda, it.tipoSeleccionado)
                )
            }
        }
    }

    fun onBusquedaChange(texto: String) {
        _uiState.update {
            it.copy(busqueda = texto, eventosFiltrados = filtrar(it.eventos, texto, it.tipoSeleccionado))
        }
    }

    fun onTipoChange(tipo: String) {
        _uiState.update {
            it.copy(tipoSeleccionado = tipo, eventosFiltrados = filtrar(it.eventos, it.busqueda, tipo))
        }
    }

    // Deja los eventos del tipo elegido depende de lo que contiene la busqueda
    private fun filtrar(eventos: List<EventoConInscritos>, busqueda: String, tipo: String): List<EventoConInscritos> {
        val texto = normalizar(busqueda)
        return eventos.filter { item ->
            val coincideTipo = tipo == "Todos" || item.evento.tipo == tipo
            val coincideTexto = texto.isBlank() ||
                    normalizar(item.evento.nombre).contains(texto) ||
                    normalizar(item.evento.juego).contains(texto) ||
                    normalizar(item.evento.lugar).contains(texto)
            coincideTipo && coincideTexto
        }
    }

    // Quita tildes y mayusculas para buscar "lan" y encontrar "LAN"
    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .lowercase()
}
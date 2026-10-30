package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.model.AppDatabase
import com.example.caso_notaria.model.Evento
import com.example.caso_notaria.repository.EventoRepository
import com.example.caso_notaria.repository.UsuarioRepository
import kotlinx.coroutines.launch
import com.example.caso_notaria.model.ValidacionesEvento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.TimeZone

class PublicarEventoViewModel(application: Application) : AndroidViewModel(application) {
    private val db by lazy { AppDatabase.getDatabase(getApplication<Application>()) }
    private val eventoRepository by lazy {
        EventoRepository(
            db.eventoDao(),
            db.inscripcionDao(),
            db.grupoChatDao(),
            db.usuarioDao(),
            db.mensajeDao()
        )
    }
    private val usuarioRepository by lazy { UsuarioRepository(db.usuarioDao()) }
    private val _uiState = MutableStateFlow(PublicarEventoUiState())
    val uiState: StateFlow<PublicarEventoUiState> = _uiState.asStateFlow()

    // Cada cambio guarda el valor y revisa solo ese campo
    fun onNombreChange(valor: String) {
        _uiState.update {
            it.copy(nombre = valor, errores = it.errores.copy(nombre = ValidacionesEvento.validarNombre(valor)))
        }
    }

    fun onTipoChange(valor: String) {
        _uiState.update { it.copy(tipo = valor, errores = it.errores.copy(tipo = null)) }
    }

    fun onJuegoChange(valor: String) {
        _uiState.update {
            it.copy(juego = valor, errores = it.errores.copy(juego = ValidacionesEvento.validarJuego(valor)))
        }
    }

    fun onFormatoChange(valor: String) {
        _uiState.update {
            it.copy(formato = valor, errores = it.errores.copy(formato = ValidacionesEvento.validarFormato(valor)))
        }
    }

    fun onCuposChange(valor: String) {
        _uiState.update {
            it.copy(cupos = valor, errores = it.errores.copy(cupos = ValidacionesEvento.validarCupos(valor)))
        }
    }

    fun onPlataformaChange(valor: String) {
        _uiState.update { it.copy(plataforma = valor, errores = it.errores.copy(plataforma = null)) }
    }

    // El DatePicker entrega la fecha en milisegundos (UTC)
    fun onFechaChange(millis: Long) {
        _uiState.update {
            it.copy(
                fechaMillis = millis,
                fechaTexto = formatearFecha(millis),
                errores = it.errores.copy(fecha = ValidacionesEvento.validarFecha(millis, inicioDeHoy()))
            )
        }
    }

    fun onHoraChange(hora: Int, minuto: Int) {
        _uiState.update {
            it.copy(hora = "%02d:%02d".format(hora, minuto), errores = it.errores.copy(hora = null))
        }
    }

    // Si pasa a online, el lugar deja de ser obligatorio
    fun onModalidadChange(valor: String) {
        _uiState.update {
            it.copy(modalidad = valor, errores = it.errores.copy(lugar = ValidacionesEvento.validarLugar(it.lugar, valor)))
        }
    }

    fun onLugarChange(valor: String) {
        _uiState.update {
            it.copy(lugar = valor, errores = it.errores.copy(lugar = ValidacionesEvento.validarLugar(valor, it.modalidad)))
        }
    }

    fun onDescripcionChange(valor: String) {
        _uiState.update {
            it.copy(descripcion = valor, errores = it.errores.copy(descripcion = ValidacionesEvento.validarDescripcion(valor)))
        }
    }

    //la foto es opcional asi que no se valida
    fun onFotoChange(uri: String) {
        _uiState.update { it.copy(fotoUri = uri) }
    }

    // Revisa todos los campos al apretar "Publicar evento". Devuelve true si no hay errores
    fun validarFormulario(): Boolean {
        val estado = _uiState.value
        val errores = PublicarEventoErrores(
            nombre = ValidacionesEvento.validarNombre(estado.nombre),
            tipo = ValidacionesEvento.validarTipo(estado.tipo),
            juego = ValidacionesEvento.validarJuego(estado.juego),
            formato = ValidacionesEvento.validarFormato(estado.formato),
            cupos = ValidacionesEvento.validarCupos(estado.cupos),
            plataforma = ValidacionesEvento.validarPlataforma(estado.plataforma),
            fecha = ValidacionesEvento.validarFecha(estado.fechaMillis, inicioDeHoy()),
            hora = ValidacionesEvento.validarHora(estado.hora),
            lugar = ValidacionesEvento.validarLugar(estado.lugar, estado.modalidad),
            descripcion = ValidacionesEvento.validarDescripcion(estado.descripcion)
        )
        _uiState.update { it.copy(errores = errores) }
        return !errores.hayErrores()
    }

    //guarda el evento en room con la foto y el usuario activo como organizador
    //eventoRepository tasmbien le crea su grupo en el chat
    fun publicarEvento() {
        if (_uiState.value.publicando) return
        if (!validarFormulario()) return
        val estado = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(publicando = true) }
            val usuario = usuarioRepository.obtenerUsuarioActivo()
            if (usuario == null) {
                _uiState.update { it.copy(publicando = false) }
                return@launch
            }
            eventoRepository.publicarEvento(
                Evento(
                    nombre = estado.nombre.trim(),
                    tipo = estado.tipo,
                    juego = estado.juego.trim(),
                    formato = estado.formato.trim(),
                    plataforma = estado.plataforma,
                    fecha = estado.fechaTexto,
                    hora = estado.hora,
                    modalidad = estado.modalidad,
                    lugar = if (estado.modalidad == "Presencial") estado.lugar.trim() else "",
                    fotoUri = estado.fotoUri,
                    cupos = estado.cupos.toInt(),
                    descripcion = estado.descripcion.trim(),
                    organizadorId = usuario.id
                )
            )
            _uiState.update { it.copy(publicando = false, publicado = true) }
        }
    }
    // Se llama al cerrar el aviso de evento publicado
    fun limpiarFormulario() {
        _uiState.value = PublicarEventoUiState()
    }

    // Hoy a las 00:00 en UTC, igual que las fechas del DatePicker
    private fun inicioDeHoy(): Long {
        val local = Calendar.getInstance()
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utc.clear()
        utc.set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
        return utc.timeInMillis
    }

    // Convierte los milisegundos en un texto como "Sáb 28 nov"
    private fun formatearFecha(millis: Long): String {
        val dias = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
        val meses = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")
        val calendario = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendario.timeInMillis = millis
        val dia = dias[calendario.get(Calendar.DAY_OF_WEEK) - 1]
        val mes = meses[calendario.get(Calendar.MONTH)]
        return "$dia ${calendario.get(Calendar.DAY_OF_MONTH)} $mes"
    }
}
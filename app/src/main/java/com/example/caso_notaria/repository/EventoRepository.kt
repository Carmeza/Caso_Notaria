package com.example.caso_notaria.repository

import com.example.caso_notaria.model.Evento
import com.example.caso_notaria.model.Inscripcion
import com.example.caso_notaria.model.Usuario
import com.example.caso_notaria.network.ApiService
import com.example.caso_notaria.network.aModelo
import com.example.caso_notaria.network.aRequest
import com.example.caso_notaria.network.mensajeDeXano
import retrofit2.HttpException

// Eventos, inscripciones y participantes desde la API de Xano
class EventoRepository(private val api: ApiService) {

    suspend fun obtenerEventos(): List<Evento> = api.obtenerEventos().map { it.aModelo() }

    // null si el evento no existe
    suspend fun obtenerEvento(id: Int): Evento? {
        val respuesta = api.obtenerEvento(id)
        if (respuesta.code() == 404) return null
        return respuesta.body()?.aModelo() ?: throw HttpException(respuesta)
    }

    // Todas las inscripciones. Con estas se cuentan los cupos y se ven los eventos de cada usuario
    suspend fun obtenerInscripciones(): List<Inscripcion> = api.obtenerInscripciones().map { it.aModelo() }

    suspend fun obtenerParticipantes(eventoId: Int): List<Usuario> =
        api.obtenerParticipantes(eventoId).map { it.aModelo() }


    suspend fun inscribirse(eventoId: Int): String? {
        val respuesta = api.inscribirse(eventoId)
        if (respuesta.isSuccessful) return null
        if (respuesta.code() == 401 || respuesta.code() >= 500) throw HttpException(respuesta)
        return respuesta.mensajeDeXano() ?: "No pudimos inscribirte en este evento"
    }

    // El organizador es el usuario del token. Devuelve el id del evento nuevo
    suspend fun publicarEvento(evento: Evento): Int = api.publicarEvento(evento.aRequest()).id
}

// Calculos que no necesitan internet

// Cantidad de inscritos de cada evento
fun contarInscritosPorEvento(inscripciones: List<Inscripcion>): Map<Int, Int> =
    inscripciones.groupingBy { it.eventoId }.eachCount()

// Eventos donde el usuario esta inscrito
fun eventosInscritos(eventos: List<Evento>, inscripciones: List<Inscripcion>, usuarioId: Int): List<Evento> {
    val ids = inscripciones.filter { it.usuarioId == usuarioId }.map { it.eventoId }.toSet()
    return eventos.filter { it.id in ids }
}

// Eventos con chat para el usuario
fun eventosDelUsuario(eventos: List<Evento>, inscripciones: List<Inscripcion>, usuarioId: Int): List<Evento> {
    val inscritos = eventosInscritos(eventos, inscripciones, usuarioId)
    return eventos.filter { it.organizadorId == usuarioId || it in inscritos }
}
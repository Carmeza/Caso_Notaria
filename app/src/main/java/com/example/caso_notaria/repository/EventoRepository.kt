package com.example.caso_notaria.repository

import com.example.caso_notaria.model.Evento
import com.example.caso_notaria.model.EventoDao
import com.example.caso_notaria.model.GrupoChat
import com.example.caso_notaria.model.GrupoChatDao
import com.example.caso_notaria.model.Inscripcion
import com.example.caso_notaria.model.InscripcionDao
import com.example.caso_notaria.model.Usuario
import com.example.caso_notaria.model.UsuarioDao

// Eventos, inscripciones y participantes
class EventoRepository(
    private val eventoDao: EventoDao,
    private val inscripcionDao: InscripcionDao,
    private val grupoChatDao: GrupoChatDao,
    private val usuarioDao: UsuarioDao
) {

    suspend fun obtenerEventos(): List<Evento> = eventoDao.getAll()

    suspend fun obtenerEvento(id: Int): Evento? = eventoDao.getById(id)

    suspend fun contarInscritos(eventoId: Int): Int = inscripcionDao.contarPorEvento(eventoId)

    suspend fun obtenerParticipantes(eventoId: Int): List<Usuario> = usuarioDao.obtenerParticipantes(eventoId)

    suspend fun estaInscrito(eventoId: Int, usuarioId: Int): Boolean =
        inscripcionDao.estaInscrito(eventoId, usuarioId)

    // Al quedar inscrito, el usuario entra al grupo de chat del evento.
    // Devuelve false si ya estaba inscrito, si es el organizador o si no quedan cupos
    suspend fun inscribirse(eventoId: Int, usuarioId: Int): Boolean {
        val evento = eventoDao.getById(eventoId) ?: return false
        if (evento.organizadorId == usuarioId) return false
        if (inscripcionDao.estaInscrito(eventoId, usuarioId)) return false
        if (inscripcionDao.contarPorEvento(eventoId) >= evento.cupos) return false
        inscripcionDao.insert(Inscripcion(eventoId = eventoId, usuarioId = usuarioId))
        return true
    }

    // Guarda el evento y le crea su grupo de chat. Devuelve el id del evento
    suspend fun publicarEvento(evento: Evento): Int {
        val id = eventoDao.insert(evento).toInt()
        grupoChatDao.insert(GrupoChat(eventoId = id, nombre = evento.nombre))
        return id
    }

    suspend fun obtenerEventosInscritos(usuarioId: Int): List<Evento> =
        eventoDao.obtenerInscritosPorUsuario(usuarioId)

    suspend fun obtenerEventosOrganizados(usuarioId: Int): List<Evento> =
        eventoDao.obtenerOrganizadosPorUsuario(usuarioId)
}
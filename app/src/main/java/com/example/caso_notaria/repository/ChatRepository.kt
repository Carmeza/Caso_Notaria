package com.example.caso_notaria.repository

import com.example.caso_notaria.model.Mensaje
import com.example.caso_notaria.network.ApiService
import com.example.caso_notaria.network.MensajeRequest
import com.example.caso_notaria.network.aModelo
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

// autorId de los mensajes automaticos del chat
const val AUTOR_SISTEMA = 0

// Una fila de la lista de chats: el evento con su ultimo mensaje
data class ChatResumen(
    val eventoId: Int,
    val titulo: String,
    val fechaEvento: String,
    val integrantes: Int,
    val ultimoMensaje: Mensaje?
)

// Chat de cada evento desde la API de Xano. Cada evento tiene un solo chat
class ChatRepository(private val api: ApiService) {

    suspend fun obtenerMensajes(eventoId: Int): List<Mensaje> = api.obtenerMensajes(eventoId).map { it.aModelo() }

    // No envia mensajes vacios
    suspend fun enviarMensaje(eventoId: Int, texto: String) {
        if (texto.isBlank()) return
        api.enviarMensaje(eventoId, MensajeRequest(texto.trim()))
    }

    // Chats de los eventos que el usuario organiza o donde esta inscrito, el mas reciente primero.
    // Con async se piden los mensajes de todos los eventos al mismo tiempo
    suspend fun obtenerChatsDelUsuario(usuarioId: Int): List<ChatResumen> = coroutineScope {
        val eventos = api.obtenerEventos().map { it.aModelo() }
        val inscripciones = api.obtenerInscripciones().map { it.aModelo() }
        val inscritos = contarInscritosPorEvento(inscripciones)

        eventosDelUsuario(eventos, inscripciones, usuarioId).map { evento ->
            async {
                ChatResumen(
                    eventoId = evento.id,
                    titulo = evento.nombre,
                    fechaEvento = evento.fecha,
                    integrantes = (inscritos[evento.id] ?: 0) + 1, // inscritos mas el organizador
                    ultimoMensaje = obtenerMensajes(evento.id).lastOrNull()
                )
            }
        }.awaitAll().sortedByDescending { it.ultimoMensaje?.hora ?: 0L }
    }
}
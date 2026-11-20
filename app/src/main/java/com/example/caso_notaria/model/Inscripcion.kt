package com.example.caso_notaria.model

// Un usuario inscrito en un evento
data class Inscripcion(
    val id: Int = 0,
    val eventoId: Int,
    val usuarioId: Int
)
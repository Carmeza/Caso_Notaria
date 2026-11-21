package com.example.caso_notaria.model

// Mensaje del chat de un evento. autorAlias es null en los mensajes del sistema
data class Mensaje(
    val id: Int = 0,
    val eventoId: Int,
    val autorId: Int,
    val autorAlias: String? = null,
    val texto: String,
    val hora: Long = System.currentTimeMillis()
)
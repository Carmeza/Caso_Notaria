package com.example.caso_notaria.model

data class Evento(
    val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val juego: String,
    val formato: String,
    val plataforma: String,
    val fecha: String,
    val hora: String,
    val modalidad: String,
    val lugar: String,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val fotoUri: String? = null,
    val cupos: Int,
    val descripcion: String,
    val organizadorId: Int,
    val organizadorAlias: String = "" // lo manda Xano en el detalle del evento
)
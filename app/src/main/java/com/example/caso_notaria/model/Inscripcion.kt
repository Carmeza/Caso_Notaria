package com.example.caso_notaria.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inscripciones")
data class Inscripcion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventoId: Int,
    val usuarioId: Int
)
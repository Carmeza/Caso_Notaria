package com.example.caso_notaria.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val alias: String,
    val correo: String,
    val contrasena: String,
    val nivel: String,
    val juegoPreferido: String = "",
    val plataforma: String = "",
    val sesionActiva: Boolean = false
)

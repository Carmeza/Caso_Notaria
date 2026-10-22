package com.example.caso_notaria.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mensajes")
data class Mensaje(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val grupoId: Int,
    val autorId: Int,
    val texto: String,
    val hora: Long = System.currentTimeMillis()
)
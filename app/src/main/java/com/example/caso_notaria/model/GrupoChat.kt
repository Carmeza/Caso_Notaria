package com.example.caso_notaria.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grupos_chat")
data class GrupoChat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventoId: Int,
    val nombre: String
)
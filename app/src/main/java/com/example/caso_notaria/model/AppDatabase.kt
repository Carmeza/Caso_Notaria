package com.example.caso_notaria.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Usuario::class, Evento::class, Inscripcion::class, GrupoChat::class, Mensaje::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase()
package com.example.caso_notaria.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Usuario::class, Evento::class, Inscripcion::class, GrupoChat::class, Mensaje::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun eventoDao(): EventoDao
    abstract fun grupoChatDao(): GrupoChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mygos_database"
                )
                    .addCallback(AppDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val usuarioDao = database.usuarioDao()
                        val eventoDao = database.eventoDao()
                        val grupoChatDao = database.grupoChatDao()

                        val usuariosIniciales = listOf(
                            Usuario(alias = "kuma_07", correo = "kuma07@correo.cl", contrasena = "12345678", nivel = "Intermedio", juegoPreferido = "Minecraft", plataforma = "PC"),
                            Usuario(alias = "millaray.exe", correo = "millaray@correo.cl", contrasena = "12345678", nivel = "Competitivo", juegoPreferido = "Free Fire", plataforma = "Consola"),
                            Usuario(alias = "tomi_xc", correo = "tomi@correo.cl", contrasena = "12345678", nivel = "Intermedio", juegoPreferido = "Minecraft", plataforma = "Consola"),
                            Usuario(alias = "rache02k", correo = "rache@correo.cl", contrasena = "12345678", nivel = "Casual", juegoPreferido = "Super Smash Bros Ultimate", plataforma = "PC"),
                            Usuario(alias = "pau_rift", correo = "pau@correo.cl", contrasena = "12345678", nivel = "Competitivo", juegoPreferido = "Call of Duty Modern Warfare", plataforma = "PC"),
                            Usuario(alias = "lu.byte", correo = "lu@correo.cl", contrasena = "12345678", nivel = "Intermedio", juegoPreferido = "Call of Duty Modern Warfare", plataforma = "Consola"),
                            Usuario(alias = "Gabo_88", correo = "gabo@correo.cl", contrasena = "12345678", nivel = "Casual", juegoPreferido = "Super Smash Bros Ultimate", plataforma = "PC")
                        )

                        for (usuario in usuariosIniciales) {
                            usuarioDao.insert(usuario)
                        }

                        val eventosIniciales = listOf(
                            Evento(nombre = "Torneo Minecraft", tipo = "Competitivo", juego = "Minecraft", formato = "Equipos", plataforma = "PC", fecha = "2026-11-15", hora = "15:00", modalidad = "Online", lugar = "Servidor Privado", cupos = 50, descripcion = "Torneo de supervivencia", organizadorId = 1),
                            Evento(nombre = "Free Fire Casual", tipo = "Amistoso", juego = "Free Fire", formato = "Solo", plataforma = "Consola", fecha = "2026-11-20", hora = "18:00", modalidad = "Online", lugar = "Lobby Público", cupos = 100, descripcion = "Partidas amistosas", organizadorId = 2)
                        )

                        for (evento in eventosIniciales) {
                            eventoDao.insert(evento)
                        }

                        val chatsIniciales = listOf(
                            GrupoChat(eventoId = 1, nombre = "Chat Torneo Minecraft"),
                            GrupoChat(eventoId = 2, nombre = "Chat Free Fire Casual")
                        )

                        for (chat in chatsIniciales) {
                            grupoChatDao.insert(chat)
                        }
                    }
                }
            }
        }
    }
}
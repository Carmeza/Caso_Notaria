package com.example.caso_notaria.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Usuario::class, Evento::class, Inscripcion::class, GrupoChat::class, Mensaje::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun eventoDao(): EventoDao
    abstract fun grupoChatDao(): GrupoChatDao
    abstract fun inscripcionDao(): InscripcionDao
    abstract fun mensajeDao(): MensajeDao

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
                    .fallbackToDestructiveMigration() // Evita que la app se cierre por cambios de esquema
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Se inserta directo en la base que se esta creando, antes de la primera consulta.
                // Asi la primera pantalla ya encuentra los datos
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
                    insertar(db, "usuarios", contentValuesOf(
                        "alias" to usuario.alias, "correo" to usuario.correo, "contrasena" to usuario.contrasena,
                        "nivel" to usuario.nivel, "juegoPreferido" to usuario.juegoPreferido,
                        "plataforma" to usuario.plataforma, "sesionActiva" to 0
                    ))
                }

                // Los tipos y el formato de fecha deben ser los mismos que en publicar evento
                val eventosIniciales = listOf(
                    Evento(nombre = "Torneo Regional Smash", tipo = "Torneo", juego = "Super Smash Bros Ultimate", formato = "1v1", plataforma = "Consola", fecha = "Sáb 7 nov", hora = "15:00", modalidad = "Presencial", lugar = "Arcade Central", cupos = 64, descripcion = "Llega media hora antes para registrarte. Habrá zona de práctica libre y transmisión del cuadro final.", organizadorId = 7),
                    Evento(nombre = "LAN Party Primavera", tipo = "LAN", juego = "Call of Duty Modern Warfare", formato = "5v5", plataforma = "PC", fecha = "Vie 13 nov", hora = "19:00", modalidad = "Presencial", lugar = "Club Gamer San Joaquín", cupos = 32, descripcion = "Trae tu equipo y audífonos. Hay mesas, enchufes y red cableada para todos.", organizadorId = 5),
                    Evento(nombre = "Lanzamiento nueva temporada Free Fire", tipo = "Lanzamiento", juego = "Free Fire", formato = "Solo", plataforma = "Consola", fecha = "Mié 18 nov", hora = "18:30", modalidad = "Online", lugar = "", cupos = 100, descripcion = "Jugamos juntos las primeras partidas de la temporada y comentamos las novedades.", organizadorId = 2),
                    Evento(nombre = "Quedada Minecraft", tipo = "Casual", juego = "Minecraft", formato = "Equipos", plataforma = "PC", fecha = "Dom 22 nov", hora = "16:00", modalidad = "Presencial", lugar = "Arcade Central", cupos = 20, descripcion = "Tarde tranquila para construir en equipo. No importa el nivel.", organizadorId = 3)
                )
                for (evento in eventosIniciales) {
                    insertar(db, "eventos", contentValuesOf(
                        "nombre" to evento.nombre, "tipo" to evento.tipo, "juego" to evento.juego,
                        "formato" to evento.formato, "plataforma" to evento.plataforma, "fecha" to evento.fecha,
                        "hora" to evento.hora, "modalidad" to evento.modalidad, "lugar" to evento.lugar,
                        "cupos" to evento.cupos, "descripcion" to evento.descripcion, "organizadorId" to evento.organizadorId
                    ))
                }

                // Un grupo de chat por evento
                val chatsIniciales = listOf(
                    GrupoChat(eventoId = 1, nombre = "Torneo Regional Smash"),
                    GrupoChat(eventoId = 2, nombre = "LAN Party Primavera"),
                    GrupoChat(eventoId = 3, nombre = "Lanzamiento nueva temporada Free Fire"),
                    GrupoChat(eventoId = 4, nombre = "Quedada Minecraft")
                )
                for (chat in chatsIniciales) {
                    insertar(db, "grupos_chat", contentValuesOf("eventoId" to chat.eventoId, "nombre" to chat.nombre))
                }

                // kuma_07 (id 1) es la cuenta de demo: esta inscrita en 3 eventos.
                // El organizador no se inscribe en su propio evento
                val inscripcionesIniciales = listOf(
                    Inscripcion(eventoId = 1, usuarioId = 1),
                    Inscripcion(eventoId = 1, usuarioId = 3),
                    Inscripcion(eventoId = 1, usuarioId = 4),
                    Inscripcion(eventoId = 1, usuarioId = 5),
                    Inscripcion(eventoId = 1, usuarioId = 6),
                    Inscripcion(eventoId = 2, usuarioId = 1),
                    Inscripcion(eventoId = 2, usuarioId = 2),
                    Inscripcion(eventoId = 2, usuarioId = 6),
                    Inscripcion(eventoId = 2, usuarioId = 7),
                    Inscripcion(eventoId = 3, usuarioId = 4),
                    Inscripcion(eventoId = 3, usuarioId = 5),
                    Inscripcion(eventoId = 3, usuarioId = 6),
                    Inscripcion(eventoId = 4, usuarioId = 1),
                    Inscripcion(eventoId = 4, usuarioId = 5),
                    Inscripcion(eventoId = 4, usuarioId = 6),
                    Inscripcion(eventoId = 4, usuarioId = 7)
                )
                for (inscripcion in inscripcionesIniciales) {
                    insertar(db, "inscripciones", contentValuesOf(
                        "eventoId" to inscripcion.eventoId, "usuarioId" to inscripcion.usuarioId
                    ))
                }

                // Cada mensaje tiene un minuto distinto para que salgan en orden
                val ahora = System.currentTimeMillis()
                val minuto = 60_000L
                val mensajesIniciales = listOf(
                    Mensaje(grupoId = 1, autorId = 3, texto = "¡Hola! ¿Quiénes van al torneo del sábado?", hora = ahora - 50 * minuto),
                    Mensaje(grupoId = 1, autorId = 5, texto = "Yo voy, pero todavía no sé cómo llegar.", hora = ahora - 45 * minuto),
                    Mensaje(grupoId = 1, autorId = 1, texto = "Yo también voy. ¿Nos juntamos en el metro?", hora = ahora - 40 * minuto),
                    Mensaje(grupoId = 1, autorId = 3, texto = "Dale, llego 14:30 a la entrada.", hora = ahora - 35 * minuto),
                    Mensaje(grupoId = 2, autorId = 6, texto = "¿Alguien lleva un switch de red extra?", hora = ahora - 30 * minuto),
                    Mensaje(grupoId = 2, autorId = 2, texto = "Yo llevo uno, no se preocupen.", hora = ahora - 25 * minuto),
                    Mensaje(grupoId = 3, autorId = 4, texto = "¿A qué hora se conectan?", hora = ahora - 20 * minuto),
                    Mensaje(grupoId = 4, autorId = 7, texto = "¿Armamos equipos de tres?", hora = ahora - 15 * minuto),
                    Mensaje(grupoId = 4, autorId = 1, texto = "Me sumo, prefiero construir.", hora = ahora - 10 * minuto)
                )
                for (mensaje in mensajesIniciales) {
                    insertar(db, "mensajes", contentValuesOf(
                        "grupoId" to mensaje.grupoId, "autorId" to mensaje.autorId,
                        "texto" to mensaje.texto, "hora" to mensaje.hora
                    ))
                }
            }

            private fun insertar(db: SupportSQLiteDatabase, tabla: String, valores: ContentValues) {
                db.insert(tabla, SQLiteDatabase.CONFLICT_ABORT, valores)
            }
        }
    }
}
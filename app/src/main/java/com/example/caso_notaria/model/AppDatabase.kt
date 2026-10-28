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
                        val dao = database.usuarioDao()

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
                            dao.insert(usuario)
                        }
                    }
                }
            }
        }
    }
}
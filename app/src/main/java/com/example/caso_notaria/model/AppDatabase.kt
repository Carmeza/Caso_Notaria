package com.example.caso_notaria.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Usuario::class], version = 1)
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
                    .addCallback(AppDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = database.usuarioDao()

                        val usuario1 = Usuario(
                            alias = "kuma_07",
                            correo = "kuma07@correo.cl",
                            contrasena = "12345678",
                            nivel = "Intermedio",
                            juegoPreferido = "Neon Rift",
                            plataforma = "PC"
                        )

                        val usuario2 = Usuario(
                            alias = "vale_exe",
                            correo = "vale@correo.cl",
                            contrasena = "12345678",
                            nivel = "Competitivo",
                            juegoPreferido = "Neon Rift",
                            plataforma = "PC"
                        )

                        dao.insert(usuario1)
                        dao.insert(usuario2)
                    }
                }
            }
        }
    }
}
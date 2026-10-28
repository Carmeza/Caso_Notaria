package com.example.caso_notaria

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.caso_notaria.model.AppDatabase
import com.example.caso_notaria.navigation.AppNavigation
import com.example.caso_notaria.ui.theme.Caso_notariaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            val usuarios = db.usuarioDao().getAll()
            Log.d("TEST_ROOM", "Total usuarios en BD: ${usuarios.size}")
            usuarios.forEach { user ->
                Log.d("TEST_ROOM", "Usuario: ${user.alias} | Correo: ${user.correo}")
            }
        }

        setContent {
            Caso_notariaTheme {
                AppNavigation()
            }
        }
    }
}
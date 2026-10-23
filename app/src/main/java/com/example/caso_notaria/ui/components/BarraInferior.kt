package com.example.caso_notaria.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.caso_notaria.navigation.Screen

// Una pestana de la barra: a donde lleva, su texto y su icono
data class PestanaInferior(val screen: Screen, val texto: String, val icono: ImageVector)

val pestanasInferiores = listOf(
    PestanaInferior(Screen.Eventos, "Eventos", Icons.Filled.Event),
    PestanaInferior(Screen.Publicar, "Publicar", Icons.Filled.AddCircle),
    PestanaInferior(Screen.Chats, "Chats", Icons.AutoMirrored.Filled.Chat),
    PestanaInferior(Screen.Perfil, "Perfil", Icons.Filled.Person)
)

// Barra inferior de 4 pestanas
@Composable
fun BarraInferior(
    rutaActual: String?,
    onPestanaClick: (Screen) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        pestanasInferiores.forEach { pestana ->
            NavigationBarItem(
                selected = rutaActual == pestana.screen.route,
                onClick = { onPestanaClick(pestana.screen) },
                icon = { Icon(pestana.icono, contentDescription = pestana.texto) },
                label = { Text(pestana.texto) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
package com.example.caso_notaria.navigation

sealed class Screen(val route: String) {

    // Acceso
    data object Bienvenida : Screen("bienvenida")
    data object IniciarSesion : Screen("iniciar_sesion")
    data object CrearCuenta : Screen("crear_cuenta")

    // Pestanas de la barra inferior
    data object Eventos : Screen("eventos")
    data object Publicar : Screen("publicar")
    data object Chats : Screen("chats")
    data object Perfil : Screen("perfil")

    object ChatDetalle : Screen("chat_detalle/{chatId}") {
        fun createRoute(chatId: Int) = "chat_detalle/$chatId"
    }

    // Rutas que llevan el id del evento. RUTA es el patron que usa el NavHost
    data class DetalleEvento(val eventoId: Int) : Screen("detalle_evento/$eventoId") {
        companion object {
            const val RUTA = "detalle_evento/{eventoId}"
        }
    }

    data class Participantes(val eventoId: Int) : Screen("participantes/$eventoId") {
        companion object {
            const val RUTA = "participantes/{eventoId}"
        }
    }
}
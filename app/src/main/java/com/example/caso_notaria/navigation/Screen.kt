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
}
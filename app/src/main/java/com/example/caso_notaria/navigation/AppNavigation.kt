package com.example.caso_notaria.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.caso_notaria.ui.components.BarraInferior
import com.example.caso_notaria.ui.components.pestanasInferiores
import com.example.caso_notaria.ui.screens.PantallaBienvenida
import com.example.caso_notaria.ui.screens.PantallaCrearCuenta
import com.example.caso_notaria.ui.screens.PantallaIniciarSesion
import com.example.caso_notaria.ui.screens.PantallaTemporal
import com.example.caso_notaria.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppNavigation(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collectLatest { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route.route) {
                        event.popUpToRoute?.let {
                            popUpTo(it.route) { inclusive = event.inclusive }
                        }
                        launchSingleTop = event.singleTop
                    }
                }
                NavigationEvent.PopBackStack -> navController.popBackStack()
                NavigationEvent.NavigateUp -> navController.navigateUp()
            }
        }
    }

    // La barra inferior solo aparece en las 4 pestañas
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val mostrarBarra = pestanasInferiores.any { it.screen.route == rutaActual }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (mostrarBarra) {
                BarraInferior(
                    rutaActual = rutaActual,
                    onPestanaClick = { screen ->
                        viewModel.navigateTo(screen, popUpToRoute = Screen.Eventos, singleTop = true)
                    }
                )
            }
        }
    ) { innerPadding ->
        // Cada ruta muestra su pantalla. Las pestañas todavia son temporales
        NavHost(
            navController = navController,
            startDestination = Screen.Bienvenida.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Bienvenida.route) {
                PantallaBienvenida(
                    onIniciarSesion = { viewModel.navigateTo(Screen.IniciarSesion) },
                    onCrearCuenta = { viewModel.navigateTo(Screen.CrearCuenta) }
                )
            }
            composable(Screen.IniciarSesion.route) {
                PantallaIniciarSesion(
                    // Al entrar se borran Bienvenida, Iniciar sesión del historial
                    onLoginExitoso = {
                        viewModel.navigateTo(Screen.Eventos, popUpToRoute = Screen.Bienvenida, inclusive = true)
                    },
                    // Cambia a "Crear cuenta" sin acumular pantallas en el historial
                    onIrACrearCuenta = {
                        viewModel.navigateTo(Screen.CrearCuenta, popUpToRoute = Screen.Bienvenida)
                    },
                    onVolver = { viewModel.navigateBack() }
                )
            }
            composable(Screen.CrearCuenta.route) {
                PantallaCrearCuenta(
                    // Despues del modal va directo a Eventos
                    onCuentaCreada = {
                        viewModel.navigateTo(Screen.Eventos, popUpToRoute = Screen.Bienvenida, inclusive = true)
                    },
                    onIrAIniciarSesion = {
                        viewModel.navigateTo(Screen.IniciarSesion, popUpToRoute = Screen.Bienvenida)
                    },
                    onVolver = { viewModel.navigateBack() }
                )
            }
            composable(Screen.Eventos.route) { PantallaTemporal("Eventos") }
            composable(Screen.Publicar.route) { PantallaTemporal("Publicar evento") }
            composable(Screen.Chats.route) { PantallaTemporal("Chats") }
            composable(Screen.Perfil.route) { PantallaTemporal("Perfil") }
        }
    }
}
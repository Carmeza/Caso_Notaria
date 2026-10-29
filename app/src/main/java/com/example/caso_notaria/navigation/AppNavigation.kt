package com.example.caso_notaria.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.caso_notaria.ui.screens.PantallaPerfil
import com.example.caso_notaria.ui.screens.PantallaPublicarEvento
import com.example.caso_notaria.ui.screens.PantallaTemporal
import com.example.caso_notaria.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppNavigation(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()

    // Escucha eventos de navegación emitidos por MainViewModel
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
        NavHost(
            navController = navController,
            startDestination = Screen.Bienvenida.route, // Fuerza que la primera vista al dar RUN sea Bienvenida
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(350))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(350))
            }
        ) {
            // 1. Pantalla Inicial (RUN)
            composable(Screen.Bienvenida.route) {
                PantallaBienvenida(
                    onIniciarSesion = { viewModel.navigateTo(Screen.IniciarSesion) },
                    onCrearCuenta = { viewModel.navigateTo(Screen.CrearCuenta) }
                )
            }

            // 2. Iniciar Sesión
            composable(Screen.IniciarSesion.route) {
                PantallaIniciarSesion(
                    onLoginExitoso = { usuarioId ->
                        viewModel.establecerUsuarioActual(usuarioId)
                        viewModel.navigateTo(Screen.Eventos, popUpToRoute = Screen.Bienvenida, inclusive = true)
                    },
                    onIrACrearCuenta = {
                        viewModel.navigateTo(Screen.CrearCuenta, popUpToRoute = Screen.Bienvenida)
                    },
                    onVolver = { viewModel.navigateBack() }
                )
            }

            // 3. Crear Cuenta
            composable(Screen.CrearCuenta.route) {
                PantallaCrearCuenta(
                    onCuentaCreada = { nuevoUsuarioId ->
                        viewModel.establecerUsuarioActual(nuevoUsuarioId)
                        viewModel.navigateTo(Screen.Eventos, popUpToRoute = Screen.Bienvenida, inclusive = true)
                    },
                    onIrAIniciarSesion = {
                        viewModel.navigateTo(Screen.IniciarSesion, popUpToRoute = Screen.Bienvenida)
                    },
                    onVolver = { viewModel.navigateBack() }
                )
            }

            // 4. Pantallas principales
            composable(Screen.Eventos.route) { PantallaTemporal("Eventos") }

            composable(Screen.Publicar.route) {
                PantallaPublicarEvento(onEventoPublicado = {
                    viewModel.navigateTo(Screen.Eventos, popUpToRoute = Screen.Eventos, singleTop = true)
                })
            }

            composable(Screen.Chats.route) { PantallaTemporal("Chats") }

            composable(Screen.Perfil.route) {
                val usuarioIdActual by viewModel.usuarioActualId.collectAsState()
                PantallaPerfil(
                    usuarioId = usuarioIdActual,
                    onCerrarSesion = {
                        viewModel.establecerUsuarioActual(-1) // Limpia el usuario logueado
                        viewModel.navigateTo(
                            screen = Screen.Bienvenida,
                            popUpToRoute = Screen.Eventos,
                            inclusive = true
                        )
                    }
                )
            }
        }
    }
}
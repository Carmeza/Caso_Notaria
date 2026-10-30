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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.caso_notaria.ui.components.BarraInferior
import com.example.caso_notaria.ui.components.pestanasInferiores
import com.example.caso_notaria.ui.screens.PantallaBienvenida
import com.example.caso_notaria.ui.screens.PantallaChatDetalle
import com.example.caso_notaria.ui.screens.PantallaChats
import com.example.caso_notaria.ui.screens.PantallaCrearCuenta
import com.example.caso_notaria.ui.screens.PantallaDetalleEvento
import com.example.caso_notaria.ui.screens.PantallaEventos
import com.example.caso_notaria.ui.screens.PantallaIniciarSesion
import com.example.caso_notaria.ui.screens.PantallaParticipantes
import com.example.caso_notaria.ui.screens.PantallaPerfil
import com.example.caso_notaria.ui.screens.PantallaPublicarEvento
import com.example.caso_notaria.ui.screens.PantallaTemporal
import com.example.caso_notaria.viewmodel.ChatsViewModel
import com.example.caso_notaria.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppNavigation(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()

    // AQUÍ ESTÁ EL CAMBIO PRINCIPAL: Se instancia un único ChatsViewModel para toda la app
    val chatsViewModel: ChatsViewModel = viewModel()

    // Escucha los eventos de navegacion que emite MainViewModel
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
            startDestination = Screen.Bienvenida.route,
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
            composable(Screen.Chats.route) {
                PantallaChats(
                    onChatClick = { chatId ->
                        navController.navigate(Screen.ChatDetalle.createRoute(chatId))
                    },
                    viewModel = chatsViewModel // Pasamos el ViewModel compartido
                )
            }

            // --- MODIFICACIÓN EN ESTA RUTA ---
            composable(
                route = Screen.ChatDetalle.route,
                arguments = listOf(navArgument("chatId") { type = NavType.IntType })
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getInt("chatId") ?: 0

                // TODO: Obtén el ID real del usuario que inició sesión.
                // Si lo tienes en MainViewModel, podría ser algo como: viewModel.usuarioIdActual
                val usuarioIdActual = 1 // <- REEMPLAZA ESTE NÚMERO POR TU VARIABLE DE SESIÓN REAL

                PantallaChatDetalle(
                    chatId = chatId,
                    usuarioId = usuarioIdActual, // Pasamos el parámetro aquí
                    onVolver = { navController.navigateUp() }
                )
            }
            // ---------------------------------

            // Acceso
            composable(Screen.Bienvenida.route) {
                PantallaBienvenida(
                    onIniciarSesion = { viewModel.navigateTo(Screen.IniciarSesion) },
                    onCrearCuenta = { viewModel.navigateTo(Screen.CrearCuenta) }
                )
            }
            composable(Screen.IniciarSesion.route) {
                PantallaIniciarSesion(
                    onLoginExitoso = {
                        viewModel.navigateTo(Screen.Eventos, popUpToRoute = Screen.Bienvenida, inclusive = true)
                    },
                    onIrACrearCuenta = {
                        viewModel.navigateTo(Screen.CrearCuenta, popUpToRoute = Screen.Bienvenida)
                    },
                    onVolver = { viewModel.navigateBack() },
                    chatsViewModel = chatsViewModel // Pasamos el ViewModel compartido para que cargue los datos
                )
            }
            composable(Screen.CrearCuenta.route) {
                PantallaCrearCuenta(
                    onCuentaCreada = {
                        viewModel.navigateTo(Screen.Eventos, popUpToRoute = Screen.Bienvenida, inclusive = true)
                    },
                    onIrAIniciarSesion = {
                        viewModel.navigateTo(Screen.IniciarSesion, popUpToRoute = Screen.Bienvenida)
                    },
                    onVolver = { viewModel.navigateBack() }
                )
            }

            // Eventos, detalle y participantes
            composable(Screen.Eventos.route) {
                PantallaEventos(onEventoClick = { eventoId ->
                    viewModel.navigateTo(Screen.DetalleEvento(eventoId))
                })
            }
            composable(
                route = Screen.DetalleEvento.RUTA,
                arguments = listOf(navArgument("eventoId") { type = NavType.IntType })
            ) { entrada ->
                val eventoId = entrada.arguments?.getInt("eventoId") ?: 0
                PantallaDetalleEvento(
                    eventoId = eventoId,
                    onVolver = { viewModel.navigateBack() },
                    onVerParticipantes = { viewModel.navigateTo(Screen.Participantes(eventoId)) },
                    onIrAlChat = {
                        navController.navigate(Screen.ChatDetalle.createRoute(eventoId))
                    }
                )
            }
            composable(
                route = Screen.Participantes.RUTA,
                arguments = listOf(navArgument("eventoId") { type = NavType.IntType })
            ) { entrada ->
                PantallaParticipantes(
                    eventoId = entrada.arguments?.getInt("eventoId") ?: 0,
                    onVolver = { viewModel.navigateBack() }
                )
            }

            // Resto de las pestanas
            composable(Screen.Publicar.route) {
                PantallaPublicarEvento(onEventoPublicado = {
                    viewModel.navigateTo(Screen.Eventos, popUpToRoute = Screen.Eventos, singleTop = true)
                })
            }

            composable(Screen.Perfil.route) {
                PantallaPerfil(
                    onCerrarSesion = {
                        chatsViewModel.limpiarChats() // Limpiamos los chats al cerrar sesión
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
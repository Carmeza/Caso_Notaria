package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.model.AppDatabase
import com.example.caso_notaria.navigation.NavigationEvent
import com.example.caso_notaria.navigation.Screen
import com.example.caso_notaria.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioRepository = UsuarioRepository(AppDatabase.getDatabase(application).usuarioDao())

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()

    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    // Pantalla con la que parte la app. Es null mientras se revisa la sesion guardada
    private val _rutaInicial = MutableStateFlow<String?>(null)
    val rutaInicial: StateFlow<String?> = _rutaInicial.asStateFlow()

    init {
        // Si quedo un usuario con sesion activa en Room, entra directo a eventos
        viewModelScope.launch {
            val usuario = usuarioRepository.obtenerUsuarioActivo()
            _rutaInicial.value = if (usuario != null) Screen.Eventos.route else Screen.Bienvenida.route
        }
    }

    fun navigateTo(
        screen: Screen,
        popUpToRoute: Screen? = null,
        inclusive: Boolean = false,
        singleTop: Boolean = false
    ) {
        viewModelScope.launch {
            _navigationEvents.emit(
                NavigationEvent.NavigateTo(screen, popUpToRoute, inclusive, singleTop)
            )
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }

    fun navigateUp() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateUp)
        }
    }
}
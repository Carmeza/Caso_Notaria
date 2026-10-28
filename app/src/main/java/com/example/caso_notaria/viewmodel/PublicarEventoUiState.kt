package com.example.caso_notaria.viewmodel

// Mensaje de error de cada campo. null = sin error
data class PublicarEventoErrores(
    val nombre: String? = null,
    val tipo: String? = null,
    val juego: String? = null,
    val formato: String? = null,
    val cupos: String? = null,
    val plataforma: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val lugar: String? = null,
    val descripcion: String? = null
) {
    fun hayErrores(): Boolean = listOf(
        nombre, tipo, juego, formato, cupos, plataforma, fecha, hora, lugar, descripcion
    ).any { it != null }
}

// Estado de la pantalla de publicar evento
data class PublicarEventoUiState(
    val fotoUri: String? = null, //foto es opcional
    val nombre: String = "",
    val tipo: String = "",
    val juego: String = "",
    val formato: String = "",
    val cupos: String = "",
    val plataforma: String = "",
    val fechaMillis: Long? = null,
    val fechaTexto: String = "",
    val hora: String = "",
    val modalidad: String = "Presencial",
    val lugar: String = "",
    val descripcion: String = "",
    val errores: PublicarEventoErrores = PublicarEventoErrores(),
    val publicado: Boolean = false
)
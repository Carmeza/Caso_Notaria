package com.example.caso_notaria.model

// Reglas del formulario de publicar evento.
object ValidacionesEvento {

    const val MAX_DESCRIPCION = 300

    fun validarNombre(nombre: String): String? = when {
        nombre.isBlank() -> "El nombre es obligatorio"
        nombre.trim().length < 5 -> "Mínimo 5 caracteres"
        nombre.length > 40 -> "Máximo 40 caracteres"
        else -> null
    }

    fun validarTipo(tipo: String): String? =
        if (tipo.isBlank()) "Elige un tipo de evento" else null

    fun validarJuego(juego: String): String? =
        if (juego.isBlank()) "El juego es obligatorio" else null

    fun validarFormato(formato: String): String? =
        if (formato.isBlank()) "El formato es obligatorio" else null

    fun validarCupos(cupos: String): String? {
        val numero = cupos.toIntOrNull()
        return when {
            cupos.isBlank() -> "Ingresa los cupos"
            numero == null -> "Solo números"
            numero < 2 -> "Mínimo 2 cupos"
            numero > 500 -> "Máximo 500 cupos"
            else -> null
        }
    }

    fun validarPlataforma(plataforma: String): String? =
        if (plataforma.isBlank()) "Elige una plataforma" else null

    // fechaMillis y hoyMillis con el inicio del dia
    fun validarFecha(fechaMillis: Long?, hoyMillis: Long): String? = when {
        fechaMillis == null -> "Elige una fecha"
        fechaMillis < hoyMillis -> "La fecha ya pasó"
        else -> null
    }

    fun validarHora(hora: String): String? =
        if (hora.isBlank()) "Elige una hora" else null

    // El lugar solo es obligatorio si el evento es presencial
    fun validarLugar(lugar: String, modalidad: String): String? =
        if (modalidad == "Presencial" && lugar.isBlank()) "El lugar es obligatorio" else null

    fun validarDescripcion(descripcion: String): String? = when {
        descripcion.isBlank() -> "Agrega una descripción"
        descripcion.length > MAX_DESCRIPCION -> "Máximo $MAX_DESCRIPCION caracteres"
        else -> null
    }
}
package com.example.caso_notaria.model

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

// Pruebas de las validaciones del formulario de publicar evento
class ValidacionesEventoTest {

    @Test
    fun `nombre valido no tiene error`() {
        assertNull(ValidacionesEvento.validarNombre("Copa Nocturna Neon Rift"))
    }

    @Test
    fun `nombre vacio, corto o muy largo tiene error`() {
        assertNotNull(ValidacionesEvento.validarNombre(""))
        assertNotNull(ValidacionesEvento.validarNombre("Copa"))
        assertNotNull(ValidacionesEvento.validarNombre("a".repeat(41)))
    }

    @Test
    fun `cupos entre 2 y 500 no tienen error`() {
        assertNull(ValidacionesEvento.validarCupos("2"))
        assertNull(ValidacionesEvento.validarCupos("64"))
        assertNull(ValidacionesEvento.validarCupos("500"))
    }

    @Test
    fun `cupos vacios, con letras o fuera de rango tienen error`() {
        assertNotNull(ValidacionesEvento.validarCupos(""))
        assertNotNull(ValidacionesEvento.validarCupos("diez"))
        assertNotNull(ValidacionesEvento.validarCupos("1"))
        assertNotNull(ValidacionesEvento.validarCupos("501"))
    }

    @Test
    fun `fecha de hoy o futura no tiene error`() {
        val hoy = 1_000_000L
        assertNull(ValidacionesEvento.validarFecha(hoy, hoy))
        assertNull(ValidacionesEvento.validarFecha(hoy + 1, hoy))
    }

    @Test
    fun `fecha pasada o sin elegir tiene error`() {
        val hoy = 1_000_000L
        assertNotNull(ValidacionesEvento.validarFecha(hoy - 1, hoy))
        assertNotNull(ValidacionesEvento.validarFecha(null, hoy))
    }

    @Test
    fun `lugar es obligatorio solo si es presencial`() {
        assertNotNull(ValidacionesEvento.validarLugar("", "Presencial"))
        assertNull(ValidacionesEvento.validarLugar("Arcade Central", "Presencial"))
        assertNull(ValidacionesEvento.validarLugar("", "Online"))
    }

    @Test
    fun `descripcion vacia o muy larga tiene error`() {
        assertNotNull(ValidacionesEvento.validarDescripcion(""))
        assertNotNull(ValidacionesEvento.validarDescripcion("a".repeat(ValidacionesEvento.MAX_DESCRIPCION + 1)))
        assertNull(ValidacionesEvento.validarDescripcion("Eliminacion directa por equipos de cinco."))
    }

    @Test
    fun `campos de seleccion sin elegir tienen error`() {
        assertNotNull(ValidacionesEvento.validarTipo(""))
        assertNotNull(ValidacionesEvento.validarPlataforma(""))
        assertNotNull(ValidacionesEvento.validarHora(""))
        assertNull(ValidacionesEvento.validarTipo("Torneo"))
    }
}
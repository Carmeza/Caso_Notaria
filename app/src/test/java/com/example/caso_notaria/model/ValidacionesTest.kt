package com.example.caso_notaria.model

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

// Pruebas de las validaciones de iniciar sesion y crear cuenta
class ValidacionesTest {

    @Test
    fun `alias valido no tiene error`() {
        assertNull(Validaciones.validarAlias("kuma_07"))
    }

    @Test
    fun `alias vacio, corto o con espacios tiene error`() {
        assertNotNull(Validaciones.validarAlias(""))
        assertNotNull(Validaciones.validarAlias("ku"))
        assertNotNull(Validaciones.validarAlias("kuma 07"))
    }

    @Test
    fun `correo valido no tiene error`() {
        assertNull(Validaciones.validarCorreo("kuma07@correo.cl"))
    }

    @Test
    fun `correo vacio o sin formato tiene error`() {
        assertNotNull(Validaciones.validarCorreo(""))
        assertNotNull(Validaciones.validarCorreo("kuma07@correo"))
        assertNotNull(Validaciones.validarCorreo("kuma07correo.cl"))
    }

    @Test
    fun `contrasena de 8 caracteres o mas no tiene error`() {
        assertNull(Validaciones.validarContrasena("mygos2026"))
    }

    @Test
    fun `contrasena vacia o corta tiene error`() {
        assertNotNull(Validaciones.validarContrasena(""))
        assertNotNull(Validaciones.validarContrasena("1234567"))
    }

    @Test
    fun `confirmacion igual a la contrasena no tiene error`() {
        assertNull(Validaciones.validarConfirmacion("mygos2026", "mygos2026"))
    }

    @Test
    fun `confirmacion vacia o distinta tiene error`() {
        assertNotNull(Validaciones.validarConfirmacion("mygos2026", ""))
        assertNotNull(Validaciones.validarConfirmacion("mygos2026", "mygos2025"))
    }

    @Test
    fun `nivel sin elegir tiene error`() {
        assertNotNull(Validaciones.validarNivel(""))
        assertNull(Validaciones.validarNivel("Casual"))
    }
}
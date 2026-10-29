package com.example.caso_notaria.viewmodel

import android.app.Application

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Pruebas del viewmodel de publicar evento
class PublicarEventoViewModelTest {

    private lateinit var viewModel: PublicarEventoViewModel

    // Una semana despues de hoy, para que la fecha sea valida
    private val fechaFutura = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L

    @Before
    fun crearViewModel() {
        viewModel = PublicarEventoViewModel(Application())    }

    // Llena todos los campos obligatorios con datos validos
    private fun llenarFormulario() {
        viewModel.onNombreChange("Copa Nocturna Neon Rift")
        viewModel.onTipoChange("Torneo")
        viewModel.onJuegoChange("Neon Rift")
        viewModel.onFormatoChange("5v5")
        viewModel.onCuposChange("16")
        viewModel.onPlataformaChange("PC")
        viewModel.onFechaChange(fechaFutura)
        viewModel.onHoraChange(20, 0)
        viewModel.onLugarChange("Arcade Central")
        viewModel.onDescripcionChange("Eliminacion directa por equipos de cinco.")
    }

    @Test
    fun `formulario vacio no es valido y marca los errores`() {
        assertFalse(viewModel.validarFormulario())

        val errores = viewModel.uiState.value.errores
        assertNotNull(errores.nombre)
        assertNotNull(errores.tipo)
        assertNotNull(errores.cupos)
        assertNotNull(errores.fecha)
        assertNotNull(errores.lugar)
    }

    @Test
    fun `formulario completo es valido`() {
        llenarFormulario()

        assertTrue(viewModel.validarFormulario())
        assertFalse(viewModel.uiState.value.errores.hayErrores())
    }

    @Test
    fun `cupos menores a 2 muestran el error al escribir`() {
        viewModel.onCuposChange("1")
        assertNotNull(viewModel.uiState.value.errores.cupos)

        viewModel.onCuposChange("16")
        assertNull(viewModel.uiState.value.errores.cupos)
    }

    @Test
    fun `evento online no necesita lugar`() {
        llenarFormulario()
        viewModel.onLugarChange("")
        viewModel.onModalidadChange("Online")

        assertTrue(viewModel.validarFormulario())
    }

    @Test
    fun `la hora se guarda con dos digitos`() {
        viewModel.onHoraChange(9, 5)
        assertEquals("09:05", viewModel.uiState.value.hora)
    }
//Publicar con datos validos se guarda en room y eso se prueba en el emulador
    @Test
    fun `publicar con errores no publica`() {
        viewModel.publicarEvento()

        assertFalse(viewModel.uiState.value.publicado)
    }

    @Test
    fun `limpiar formulario deja todo vacio`() {
        llenarFormulario()
        viewModel.limpiarFormulario()

        assertEquals(PublicarEventoUiState(), viewModel.uiState.value)
    }
}
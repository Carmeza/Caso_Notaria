package com.example.caso_notaria.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.caso_notaria.model.Evento
import com.example.caso_notaria.ui.theme.Caso_notariaTheme

@Preview(showBackground = true)
@Composable
private fun ComponentesPreview() {
    Caso_notariaTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BotonPrincipal(texto = "Iniciar sesión", onClick = {})
                BotonSecundario(texto = "Crear cuenta", onClick = {})

                CampoTexto(valor = "kuma07@correo.cl", onCambio = {}, etiqueta = "Correo")
                CampoTexto(
                    valor = "123", onCambio = {}, etiqueta = "Contraseña",
                    esContrasena = true, error = "Mínimo 8 caracteres"
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipCategoria(texto = "Torneo", seleccionado = true, onClick = {})
                    ChipCategoria(texto = "Casual", seleccionado = false, onClick = {})
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AvatarIniciales(nombre = "kuma_07")
                    AvatarIniciales(nombre = "vale.exe")
                }

                TarjetaEvento(
                    evento = Evento(
                        nombre = "Torneo Regional Neon Rift", tipo = "Torneo", juego = "Neon Rift",
                        formato = "5v5", plataforma = "PC", fecha = "Sáb 12 oct", hora = "15:00",
                        modalidad = "Presencial", lugar = "Arcade Central", cupos = 64,
                        descripcion = "Clasificatoria regional", organizadorId = 1
                    ),
                    inscritos = 48,
                    onClick = {}
                )
            }
        }
    }
}
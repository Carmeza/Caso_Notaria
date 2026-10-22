package com.example.caso_notaria.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val EsquemaOscuro  = darkColorScheme(
    primary = Violeta,
    onPrimary = Fondo,
    secondary = Turquesa,
    onSecondary = Fondo,
    tertiary = VioletaOscuro,
    background = Fondo,
    onBackground = TextoPrincipal,
    surface = Superficie,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieAlta,
    onSurfaceVariant = TextoSecundario,
    outline = Borde,
    error = RojoError,
    onError = Fondo
)

val Formas = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun Caso_notariaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaOscuro,
        typography = Typography,
        shapes = Formas,
        content = content
    )
}
package com.example.caso_notaria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun ChipCategoria(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = { Text(texto, style = MaterialTheme.typography.labelMedium) },
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    )
}

@Composable
fun Etiqueta(
    texto: String,
    modifier: Modifier = Modifier,
    colorFondo: Color = MaterialTheme.colorScheme.surfaceVariant,
    colorTexto: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelMedium,
        color = colorTexto,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .background(colorFondo, MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}
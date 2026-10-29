package com.example.caso_notaria.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.util.Consumer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

// Centro de Santiago, para cuando todavia no hay punto elegido
private val santiago = LatLng(-33.4489, -70.6693)

// Mapa para elegir el lugar del evento
@Composable
fun MapaSelectorUbicacion(
    latitud: Double?,
    longitud: Double?,
    onUbicacionElegida: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val punto = if (latitud != null && longitud != null) LatLng(latitud, longitud) else null
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(punto ?: santiago, if (punto != null) 15f else 11f)
    }
    var mensaje by remember { mutableStateOf<String?>(null) }

    // Cuando cambia el punto, la camara se mueve hasta el
    LaunchedEffect(punto) {
        punto?.let { camara.animate(CameraUpdateFactory.newLatLngZoom(it, 15f)) }
    }

    // Pide el permiso y despues lee la ubicacion del telefono
    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        if (permisos.values.any { it }) {
            mensaje = null
            leerUbicacionActual(context) { lat, lng ->
                if (lat != null && lng != null) onUbicacionElegida(lat, lng)
                else mensaje = "No pudimos leer tu ubicación. Activa el GPS o toca el mapa."
            }
        } else {
            mensaje = "Sin permiso de ubicación. Puedes tocar el mapa para elegir el lugar."
        }
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        GoogleMap(
            cameraPositionState = camara,
            uiSettings = MapUiSettings(zoomControlsEnabled = false, mapToolbarEnabled = false),
            onMapClick = { onUbicacionElegida(it.latitude, it.longitude) },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            punto?.let { Marker(state = rememberUpdatedMarkerState(position = it), title = "Lugar del evento") }
        }

        Row {
            TextButton(onClick = {
                permisoLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                )
            }) {
                Icon(Icons.Filled.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Text("  Usar mi ubicación", color = MaterialTheme.colorScheme.secondary)
            }
        }

        Text(
            text = mensaje ?: if (punto == null) "Toca el mapa para fijar el lugar" else "Toca el mapa para mover el pin",
            style = MaterialTheme.typography.bodySmall,
            color = if (mensaje != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Mapa fijo con el pin del evento, para el detalle
@Composable
fun MapaEvento(
    latitud: Double,
    longitud: Double,
    titulo: String,
    modifier: Modifier = Modifier
) {
    val punto = LatLng(latitud, longitud)
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(punto, 15f)
    }
    GoogleMap(
        cameraPositionState = camara,
        uiSettings = MapUiSettings(zoomControlsEnabled = false, scrollGesturesEnabled = false, mapToolbarEnabled = true),
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(MaterialTheme.shapes.medium)
    ) {
        Marker(state = rememberUpdatedMarkerState(position = punto), title = titulo)
    }
}

// Lee la ubicacion actual con el GPS (o la red si el GPS esta apagado).
// Solo se llama despues de que el usuario dio el permiso
@SuppressLint("MissingPermission")
private fun leerUbicacionActual(context: Context, onResultado: (Double?, Double?) -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val proveedor = when {
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
        else -> {
            onResultado(null, null)
            return
        }
    }
    LocationManagerCompat.getCurrentLocation(
        locationManager,
        proveedor,
        null as CancellationSignal?,
        ContextCompat.getMainExecutor(context),
        Consumer<Location?> { ubicacion -> onResultado(ubicacion?.latitude, ubicacion?.longitude) }
    )
}
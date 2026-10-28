package com.example.caso_notaria.ui.components

import android.Manifest
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
@Composable
fun CamaraFotoLugar(
    fotoUri: String?,
    onFotoTomada: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current // archivo donde la camara guarda la foto
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    //convierte la foto en imagen para mostrarla
    //solo se vuelve a calcular cuando cambia fotoUri
    val bitmap = remember(fotoUri) {
        fotoUri?.let { MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(it)) }
    }
    //Launcher de la camara
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            onFotoTomada(imageUri.toString())
        }
    }

    //Permiso de la camara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = File.createTempFile("foto_lugar", ".jpg", context.cacheDir)
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file
            )
            imageUri = uri
            takePictureLauncher.launch(uri)
        }
    }
    //popup que se toca para pedir el permiso y abrir la camara
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(MaterialTheme.shapes.medium)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
            .clickable { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
    ) {
        if (bitmap != null) {
            //con foto: la foto llena el recuadro
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto del lugar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Etiqueta(
                texto = "Toca para cambiar",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        } else {
            //sin foto: se deja icono y texto
            Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoCamera,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "Tomar foto del lugar",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Pediremos permiso para usar la cámara",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        }
    }
}
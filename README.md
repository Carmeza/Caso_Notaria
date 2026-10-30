# Mygos

App Android para encontrar eventos gamer (torneos, LAN, lanzamientos y juntas casuales) y no ir solo: al inscribirte entras al chat del evento con los demás participantes.

Caso Mygos.

## Funcionalidades

- Crear cuenta e iniciar sesión con validaciones. La sesión queda guardada en Room.
- Lista de eventos con buscador (sin importar tildes) y filtros por tipo.
- Detalle del evento, inscripción y lista de participantes.
- Chat grupal de cada evento: los mensajes se guardan y se avisa cuando alguien se une.
- Publicar evento con formulario validado, foto del lugar (cámara) y ubicación (GPS y mapa).
- Perfil con eventos inscritos y cierre de sesión con confirmación.

## Tecnologías

- Kotlin, Jetpack Compose y Material 3
- Arquitectura MVVM: `model` (entidades, DAOs y Room), `repository`, `viewmodel` y `ui`
- Room para la persistencia local
- Navigation Compose
- Cámara (`TakePicture` + `FileProvider`), GPS (`LocationManager`) y Google Maps (`maps-compose`)
- Pruebas unitarias con JUnit (validaciones y ViewModel de publicar evento)

## Cómo ejecutar

1. Clonar el repositorio y abrirlo en Android Studio.
2. Para ver el mapa, agregar la clave de Google Maps en `local.properties`:

   ```
   MAPS_API_KEY=tu_clave
   ```

   Sin la clave la app funciona igual, pero el mapa se ve vacío.
3. Ejecutar la configuración `app` en un emulador o teléfono con Android 7.0 o superior.

### Cuenta de prueba

| Correo | Contraseña |
|---|---|
| kuma07@correo.cl | 12345678 |

La primera vez que se abre la app se cargan usuarios, eventos, inscripciones y mensajes de ejemplo. Para volver a los datos iniciales, desinstalar la app.

## Equipo

- Carlos Meza
- Felipe Yefi
- Martin Veyl

## Organización

- Tareas en Trello.
- Una rama por tarea (`feature/...`, `fix/...`) y Pull Request hacia `main`.

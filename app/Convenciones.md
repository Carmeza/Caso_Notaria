# Convenciones del proyecto

Reglas para trabajar en equipo con Git en el proyecto Caso Notaria (Mygos).

## Ramas

### Regla principal

Nadie trabaja directo en `main`. La rama `main` siempre debe compilar y funcionar.

Cada tarjeta de Trello se trabaja en su propia rama.

### Nombres de ramas

Formato:

```
tipo/descripcion-corta
```

Tipos permitidos:

- `feature/` para algo nuevo (pantallas, funciones, modelos)
- `fix/` para corregir un error
- `refactor/` para reorganizar código sin cambiar lo que hace
- `docs/` para documentación

Reglas:

- Todo en minúsculas
- Palabras separadas por guiones
- Sin tildes, sin ñ y sin espacios
- Nombre corto que se relacione con la tarjeta de Trello

Ejemplos:

```
feature/estructura-modelos
feature/repositorios-room
feature/pantalla-login
feature/crear-cuenta
feature/lista-eventos
feature/detalle-evento
feature/publicar-evento
feature/camara
feature/mapa
feature/chats
feature/perfil
fix/validacion-correo
docs/convenciones
```

### Flujo de trabajo

1. Actualizar main antes de empezar:

```
git checkout main
git pull
```

2. Crear la rama de la tarjeta:

```
git checkout -b feature/nombre-tarea
```

3. Trabajar y hacer commits en la rama.

4. Compilar con Ctrl + F9 antes de subir. No se sube código que no compila.

5. Subir la rama:

```
git push -u origin feature/nombre-tarea
```

6. Crear un Pull Request en GitHub hacia `main`.

7. Otro integrante revisa el Pull Request. Si está bien, se hace el merge.

8. Volver a main y actualizar:

```
git checkout main
git pull
```

9. Borrar la rama en GitHub después del merge.

### Reglas del Pull Request

- El título explica en una frase qué se hizo
- La descripción indica qué tarjeta de Trello resuelve
- Se pide revisión a otro integrante del equipo
- No se hace merge si el proyecto no compila
- Si hay conflictos, los resuelve quien creó la rama

## Commits

### Formato

```
tipo: descripcion corta
```

Tipos permitidos:

- `feat` para algo nuevo
- `fix` para corregir un error
- `refactor` para reorganizar código sin cambiar lo que hace
- `style` para cambios visuales o de formato (colores, espacios, tema)
- `docs` para documentación
- `test` para pruebas
- `chore` para configuración, dependencias y tareas de mantenimiento

### Reglas

- Escribir en español
- Usar verbo en infinitivo: agregar, crear, corregir, actualizar, eliminar
- Todo en minúsculas
- Sin punto final
- Máximo una línea de 72 caracteres
- Un commit por cambio lógico, no un commit gigante al final
- No hacer commits con mensajes como "cambios", "arreglo" o "asdf"

Ejemplos correctos:

```
chore: configurar dependencias de room, navigation y viewmodel
feat: crear modelos de datos usuario, evento e inscripcion
feat: agregar pantalla de inicio de sesion
feat: agregar validacion de contraseña en crear cuenta
fix: corregir validacion de correo vacio
style: adaptar colores del tema oscuro al mockup
refactor: mover validaciones al viewmodel
test: agregar pruebas del viewmodel de login
docs: agregar convenciones del proyecto
```

Ejemplos incorrectos:

```
cambios
arreglé cosas
Agregué la pantalla de login y el registro y los colores.
```

## Estructura de paquetes

Cada archivo va en su paquete según su responsabilidad:

- `model` para clases de datos, entidades de Room, DAOs y AppDatabase
- `repository` para el acceso a los datos
- `viewmodel` para el estado y la lógica de cada pantalla
- `ui` para las pantallas y componentes en Compose
- `ui/theme` para colores, tipografía y tema

La pantalla no accede directo al repositorio ni a la base de datos. Siempre pasa por su ViewModel.

## Nombres en el código

- Clases y archivos en PascalCase: `Evento`, `LoginScreen`, `EventosViewModel`
- Funciones y variables en camelCase: `cargarEventos`, `listaEventos`
- Pantallas terminan en `Screen`: `LoginScreen`, `PerfilScreen`
- ViewModels terminan en `ViewModel`: `AuthViewModel`, `ChatViewModel`
- Repositorios terminan en `Repository`: `EventoRepository`
- DAOs terminan en `Dao`: `EventoDao`
- Solo datos ficticios. Nunca datos reales de personas.
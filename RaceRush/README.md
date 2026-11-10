# RaceRush (Avance 2: Pantallas y navegación)

App nativa de Android en **Kotlin** con arquitectura **MVVM** (Single-Activity + Fragments + Navigation Component).
En este avance se implementan todas las pantallas y la navegación. **No se consumen servicios del backend**:
los datos salen de `FakeRaceRepository` (en memoria), que luego se reemplazará por Firebase / REST.

## Cómo ejecutarlo
1. Abrir Android Studio (Ladybug 2024.2 o más reciente) → **File › Open** → seleccionar la carpeta `RaceRush`.
2. Esperar la sincronización de Gradle (descarga Gradle 8.9 y AGP 8.7.3 la primera vez).
3. Crear/usar un emulador con API 26 o superior y presionar **Run ▶**.

> Para probar: cualquier correo con formato válido y una contraseña de 6+ caracteres sirven para entrar.
> El idioma sigue al del dispositivo (español por defecto, inglés en `values-en`).

## Flujo de navegación
Splash → Login ⇄ Registro → Menú principal
- JUGAR → Garaje → Selección de pista → Carrera (HUD) → Resultado → (Campeonato | Correr de nuevo | Menú)
- CAMPEONATO → Clasificación → Formulario de inscripción
- GARAJE → Garaje (guardar auto)
- AJUSTES → Ajustes → Cerrar sesión (vuelve a Login)
- 🔔 → Notificaciones

## Estructura
```
app/src/main/java/com/racerush/app/
├── MainActivity.kt                  # Única Activity (NavHost)
├── data/model/Models.kt             # Car, Track, Standing, RaceResult, AppNotification…
├── data/repository/RaceRepository.kt# Interfaz + FakeRaceRepository (datos simulados)
└── ui/
    ├── GameSessionViewModel.kt      # Estado compartido: auto, pista, último resultado
    ├── splash/  auth/  menu/  garage/  track/
    ├── race/    (RaceViewModel = bucle de juego, RaceTrackView = dibujo en Canvas)
    ├── results/ championship/  notifications/  settings/
res/navigation/nav_graph.xml         # Grafo de navegación
```

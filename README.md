# YTMiniPlayer (ytminimal-desktop)

Reproductor de música **minimalista de escritorio** construido con **Kotlin Multiplatform** y **Compose Multiplatform (Desktop/JVM)**. Permite buscar canciones en YouTube, descargar solo el audio en formato **MP3** y reproducirlo desde una biblioteca local, todo en una ventana compacta y sin bordes que se ancla en la esquina de la pantalla.

- **Package name:** `YTMiniPlayer`
- **Versión de la aplicación:** `1.0.5`
- **Nombre del proyecto Gradle:** `KotlinYTMiniPlayer`
- **Paquete raíz del código:** `com.bws.ytminiplayer`

---

## Características

- **Búsqueda en YouTube** mediante la *YouTube Data API v3*, con caché local de resultados en disco para no gastar cuota de la API en búsquedas repetidas.
- **Descarga de audio** de un vídeo y conversión a MP3 usando binarios embebidos de `yt-dlp` + `ffmpeg` (+ `deno` como runtime de JS para `yt-dlp`).
- **Reproductor** con control de reproducción/pausa, barra de progreso, volumen y modos de repetición (`OFF`, `ALL`, `ONE`), apoyado en **JavaFX Media**.
- **Biblioteca local** que lee automáticamente los MP3 de la carpeta de música al arrancar.
- **Etiquetas ID3 y carátulas** leídas con *jaudiotagger*, con caché de miniaturas recortadas en disco.
- **Notificaciones nativas** del sistema (bandeja / tray) al terminar descargas o ante errores.
- **Ventana sin decoración** (undecorated + transparente), reposicionada en la esquina inferior derecha, con sus propios botones de minimizar/cerrar.
- **Preferencias persistentes** (volumen y modo de repetición) en `~/.ytminiplayer/settings.properties`.

---

## Estructura del proyecto

```
ytminimal-desktop/
├── build.gradle.kts            # Configuración raíz (solo declara plugins)
├── settings.gradle.kts         # Módulos + repositorios + foojay
├── gradle.properties           # Flags de Gradle/Kotlin (JVM args, caché, config-cache)
├── gradle/
│   └── libs.versions.toml       # Version catalog (versiones centralizadas)
├── shared/                     # Lógica y UI compartida (target jvm)
│   └── src/
│       ├── commonMain/kotlin/com/bws/ytminiplayer/
│       │   ├── audio/          # AudioPlayerController (JavaFX), MusicFolder
│       │   ├── config/         # YouTubeConfig (endpoint + API key + parámetros)
│       │   ├── data/           # YouTubeApi, YtModels, SearchCache, ThumbCache, Settings
│       │   ├── helper/         # AppPaths, EmbeddedBinaries, YouTubeAudioDownloader, NotificationHelper
│       │   ├── ui/             # Composables: MusicPlayer, TopBar, Controls, SearchView, LibraryView, etc.
│       │   └── util/           # TimeFormat
│       ├── commonTest/         # Tests comunes
│       ├── jvmMain/            # Platform.jvm.kt (código específico de JVM)
│       └── jvmTest/            # Tests específicos de escritorio
└── desktopApp/                 # Punto de entrada de la app de escritorio
    ├── build.gradle.kts        # Dependencias JavaFX + config de nativeDistributions
    └── src/
        ├── main/kotlin/com/bws/ytminiplayer/main.kt   # función main()
        └── resources/common/
            ├── bin/            # Binarios embebidos: yt-dlp, ffmpeg, deno (.exe en Windows)
            └── img/            # Recursos de imagen
```

> El código de negocio y la UI viven en `:shared`; `:desktopApp` solo aporta el `main()`, las dependencias de JavaFX y la configuración de empaquetado nativo.

---

## Requisitos previos

| Herramienta | Versión | Notas |
|---|---|---|
| **JDK** | **21** | El proyecto fija la toolchain en JDK **21** (vendor Azul) vía foojay. |
| **Gradle** | **9.1.0** | No hace falta instalarlo: usa el *wrapper* incluido (`./gradlew`). |
| **Binarios en `bin/`** | — | `yt-dlp`, `ffmpeg` y `deno` deben existir en `desktopApp/src/resources/common/bin/`. En Windows se buscan los `.exe`; en Linux/macOS los binarios sin extensión. |
| **API Key de YouTube** | — | Definida en `config/YouTubeConfig.kt`. Debe ser válida y con cuota disponible en la *YouTube Data API v3*. |

### Sobre la carpeta `bin/` (binarios embebidos)

La app **no** descarga estos binarios: los espera empaquetados en `src/resources/common/bin`. El repositorio incluye por defecto las variantes de Windows (`yt-dlp.exe`, `ffmpeg.exe`, `deno.exe`, además de `ffplay.exe`/`ffprobe.exe`) y la de Linux (`yt-dlp`). Si vas a compilar para **Linux o macOS**, debes colocar ahí también los binarios nativos de `ffmpeg` y `deno` para esa plataforma.

### Sobre la API Key

Actualmente la API key está **incrustada en el código** (`YouTubeConfig.API_KEY`) solo para desarrollo. Para distribución **no** dejes la key en el código: cárgala desde una variable de entorno o un archivo fuera del control de versiones, y regenérala en Google Cloud Console si ya se expuso públicamente.

### Sobre cookies (opcional)

El descargador pasa `--cookies` apuntando a `yt_cookies.txt` dentro del directorio de recursos de la app. Si YouTube exige verificación para ciertos vídeos, exporta tus cookies a ese archivo.

---

## Cómo ejecutar

Desde la raíz del proyecto:

```bash
# Ejecutar la app de escritorio (modo estándar)
./gradlew :desktopApp:run

# Ejecutar con hot reload (recarga en caliente durante el desarrollo)
./gradlew :desktopApp:hotRun --auto
```

En Windows usa `gradlew.bat` en lugar de `./gradlew`:

```bat
gradlew.bat :desktopApp:run
```

---

## Cómo compilar / empaquetar

Compose Desktop genera instaladores nativos según la plataforma en la que compiles. Los formatos configurados son **Dmg** (macOS), **Msi** y **Exe** (Windows) y **Deb** (Linux).

```bash
# Compilar todo el proyecto
./gradlew build

# Crear la distribución nativa (instalador) para el SO actual
./gradlew :desktopApp:packageDistributionForCurrentOS

# Formatos individuales (según el SO donde compiles):
./gradlew :desktopApp:packageMsi     # Windows (instalador MSI)
./gradlew :desktopApp:packageExe     # Windows (ejecutable)
./gradlew :desktopApp:packageDeb     # Linux
./gradlew :desktopApp:packageDmg     # macOS

# Empaquetado sin instalador (carpeta con el runtime + app)
./gradlew :desktopApp:createDistributable

# Ejecutar el distribuible ya empaquetado (útil para probar rutas de recursos)
./gradlew :desktopApp:runDistributable
```

> **Nota:** solo puedes generar el instalador del sistema operativo en el que estés compilando (p. ej. el `.msi` únicamente desde Windows). En Windows se usa el icono `src/resources/app_icon.ico`.

---

## Cómo ejecutar los tests

```bash
# Tests del módulo shared (JVM/escritorio)
./gradlew :shared:jvmTest

# Todos los tests del proyecto
./gradlew test
```

---

## Librerías utilizadas y para qué sirven

### Kotlin, Compose y ciclo de vida
| Librería / Plugin | Versión | ¿Para qué se usa? |
|---|---|---|
| **Kotlin** | `2.4.10` | Lenguaje base; también define la versión del plugin de compilación de Compose y del plugin Multiplatform/JVM. |
| **Compose Multiplatform** | `1.11.1` | Framework de UI declarativa (runtime, foundation, ui, resources, tooling-preview) y empaquetado de escritorio (`compose.desktop.currentOs`). |
| **Compose Material 3** | `1.11.0-alpha07` | Componentes visuales Material Design 3 de la interfaz. |
| **material-icons-extended** | `1.7.3` | Set extendido de iconos Material para los controles del reproductor. |
| **androidx-lifecycle (viewmodel-compose y runtime-compose)** | `2.11.0-beta01` | Integración de ViewModel / ciclo de vida con Compose. |

### Concurrencia
| Librería | Versión | ¿Para qué se usa? |
|---|---|---|
| **kotlinx-coroutines-swing** | `1.11.0` | Dispatcher para el hilo de UI (Swing/AWT) en escritorio. |
| **kotlinx-coroutines-core** | `1.8.1` | Corrutinas para operaciones asíncronas (red, descargas, E/S de disco). |

### Serialización y red
| Librería | Versión | ¿Para qué se usa? |
|---|---|---|
| **kotlinx-serialization-json** | `1.7.3` | Parseo del JSON de la YouTube Data API a modelos (`YtModels`). |
| **plugin de serialización de Kotlin** | `2.1.0` | Habilita la generación de serializadores en el módulo `shared`. |
| **Ktor Client (core + CIO)** | `2.3.7` | Cliente HTTP (motor CIO) para peticiones de red. |

> Nota: la búsqueda en `YouTubeApi` también usa `HttpURLConnection` de Java estándar; Ktor queda disponible como cliente HTTP del proyecto.

### Audio y metadatos
| Librería | Versión | ¿Para qué se usa? |
|---|---|---|
| **JavaFX** (base, graphics, media, swing) | `21.0.5` | Motor de reproducción de audio (`javafx.scene.media.MediaPlayer`) e integración con Swing. Se resuelve el clasificador del SO (`win`/`mac`/`linux`) automáticamente. |
| **jaudiotagger** (`net.jthink`) | `3.0.1` | Lectura de etiquetas ID3 (título, artista, duración) y carátulas embebidas en los MP3. |

### Binarios externos embebidos (no son dependencias Gradle)
| Binario | ¿Para qué se usa? |
|---|---|
| **yt-dlp** | Descarga del audio del vídeo de YouTube. |
| **ffmpeg** | Conversión/extracción del audio a MP3. |
| **deno** | Runtime de JavaScript que `yt-dlp` usa (`--js-runtimes deno:...`). |

### Logging y tests
| Librería | Versión | ¿Para qué se usa? |
|---|---|---|
| **slf4j-simple** | `2.0.9` | Implementación simple de logging para SLF4J. |
| **JUnit** | `4.13.2` | Framework de pruebas. |
| **kotlin-test / kotlin-test-junit** | `2.4.10` (ref. Kotlin) | Aserciones de test integradas con JUnit. |

---

## Versionamiento (resumen de todo lo usado)

**Build / toolchain**
- Gradle Wrapper: **9.1.0**
- JDK (toolchain, vendor Azul): **21**
- foojay-resolver-convention: **1.0.0**
- Versión de la app (packageVersion): **1.0.5**

**Kotlin y plugins**
- Kotlin (JVM / Multiplatform / Compose Compiler): **2.4.10**
- Kotlin Serialization plugin: **2.1.0**
- Compose Multiplatform plugin: **1.11.1**

**Compose / UI**
- Compose Multiplatform (runtime, foundation, ui, resources, tooling-preview, desktop): **1.11.1**
- Compose Material 3: **1.11.0-alpha07**
- material-icons-extended: **1.7.3**
- androidx-lifecycle (viewmodel-compose, runtime-compose): **2.11.0-beta01**

**Concurrencia / red / serialización**
- kotlinx-coroutines-swing: **1.11.0**
- kotlinx-coroutines-core: **1.8.1**
- kotlinx-serialization-json: **1.7.3**
- Ktor client core / CIO: **2.3.7**

**Audio / metadatos**
- JavaFX (base, graphics, media, swing): **21.0.5**
- jaudiotagger (net.jthink): **3.0.1**

**Logging / tests**
- slf4j-simple: **2.0.9**
- JUnit: **4.13.2**
- kotlin-test / kotlin-test-junit: **2.4.10**

> Las versiones están centralizadas en `gradle/libs.versions.toml` (version catalog). Algunas dependencias (JavaFX, Ktor, jaudiotagger, serialization-json, coroutines-core, slf4j, material-icons-extended) se declaran directamente con su versión en los `build.gradle.kts` de cada módulo.

---

## Rutas y datos en tiempo de ejecución

- **Recursos base:** en ejecución empaquetada se usa `compose.application.resources.dir`; en desarrollo, `src/resources/common`.
- **Música:** carpeta `mp3/` (y su subcarpeta `download/` si contiene MP3) dentro de los recursos.
- **Preferencias:** `~/.ytminiplayer/settings.properties` (persiste entre reinstalaciones).
- **Caché de búsquedas:** `cache/search/<queryBase64>.json`.
- **Caché de miniaturas:** `img/thumb/<videoId>.jpg`.
- **Logs de descarga:** carpeta `log/` con archivos `log_<timestamp>.txt`.

---

## Notas de seguridad

- **No** publiques la API key en el repositorio. Muévela a una variable de entorno o archivo ignorado por Git y regenérala si se expuso.
- Los binarios embebidos (`yt-dlp`, `ffmpeg`, `deno`) son de terceros: manténlos actualizados y verifica su procedencia.

---

## Licencia

Añade aquí la licencia que quieras aplicar al proyecto (por ejemplo MIT). Actualmente el repositorio no declara una.

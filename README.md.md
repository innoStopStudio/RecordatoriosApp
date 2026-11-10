# RecordatoriosApp

Aplicación Android de gestión de recordatorios, desarrollada en Kotlin como parte de las actividades prácticas integradoras de la materia **Programación Mobile** de la Tecnicatura en Programación de Instituto Teclab. La tercera etapa incorpora persistencia local, preferencias del usuario y sincronización con un servicio remoto.

## Funcionalidades

- Inicio de sesión con correo electrónico y contraseña mediante Supabase Auth.
- Listado, creación, consulta, edición y eliminación de recordatorios.
- Datos de cada recordatorio: título, descripción, categoría, prioridad, estado y campos opcionales de fecha de aviso y ubicación.
- Preferencias persistentes: tema visual, orden del listado y visualización de recordatorios completados.
- Almacenamiento local con Room: los cambios pueden realizarse sin conexión.
- Sincronización con Supabase al disponer de conectividad, con operaciones pendientes y reintentos ante ciertos errores de red.
- Indicador de estado de sincronización y opción de reintento desde la interfaz.

**Alcance:** los campos de fecha de aviso y ubicación forman parte del modelo de datos; no se documentan como notificaciones programadas ni como geolocalización automática.

## Tecnologías utilizadas

- **Kotlin** y **Jetpack Compose** con **Material 3**: lógica e interfaz de usuario.
- **ViewModel**, **Coroutines** y **Flow/StateFlow**: estado y operaciones asíncronas.
- **Room**: base de datos SQLite local y acceso mediante DAO.
- **DataStore Preferences**: preferencias persistentes.
- **Retrofit**, **OkHttp** y **Gson**: comunicación HTTP y serialización.
- **Supabase Auth** y **Supabase REST API**: autenticación y almacenamiento remoto.
- **Navigation Compose**: navegación entre pantallas.
- **Gradle (Kotlin DSL)** y **KSP**: construcción del proyecto y generación de código de Room.

## Organización del proyecto

El código principal se encuentra en `app/src/main/java/com/example/recordatoriosapp/`:

```text
├── MainActivity.kt
├── ConectividadObserver.kt
├── model/                 # Modelo de dominio
├── navigation/            # Rutas y navegación Compose
├── presentation/          # Pantallas, estados de UI y ViewModels
└── data/
    ├── local/             # Entidades, DAO y base de datos Room
    ├── preferences/       # DataStore
    ├── remote/            # Retrofit, autenticación, DTO y errores de red
    ├── mapper/            # Conversiones entre modelos
    ├── RecordatorioRepositoryRoom.kt
    └── SincronizadorRecordatorios.kt
```

Los esquemas versionados de Room están en `app/schemas/` (versiones 1, 2 y 3).

## Arquitectura y funcionamiento sin conexión

La interfaz Compose observa los estados expuestos por los ViewModels. Estos utilizan el repositorio, que consulta y modifica los datos locales de Room. La sincronización comunica los cambios pendientes con Supabase y recupera datos remotos.

```text
Jetpack Compose
      ↓
ViewModels
      ↓
Repositorio de recordatorios
      ├── Room (datos locales observables)
      └── Sincronizador ── Retrofit / OkHttp ── Supabase

DataStore ── Preferencias de la interfaz
```

Se sigue un enfoque **local-first**: Room es la fuente de datos que observa la interfaz. Las operaciones locales se registran con un estado de sincronización; cuando la conexión está disponible, el sincronizador intenta enviar los cambios pendientes y actualizar los datos locales. Los identificadores estables y la operación remota de inserción con resolución de duplicados ayudan a evitar registros repetidos.

El indicador de la interfaz informa el estado de los intentos de sincronización. No debe interpretarse como una auditoría exhaustiva de todos los cambios pendientes.

## Requisitos para ejecutar

- Android Studio compatible con el proyecto y acceso a los componentes del SDK necesarios.
- JDK y Gradle configurados mediante Android Studio y el Gradle Wrapper incluido.
- **Android SDK 37** para compilar; **Android 8.0 (API 26)** o superior para ejecutar.
- Dispositivo Android físico o emulador.
- Conexión a Internet para el inicio de sesión y la sincronización.
- Un proyecto de Supabase previamente configurado con autenticación y la tabla `public.recordatorios`, junto con sus políticas de acceso por usuario (RLS).

> El código fuente no crea automáticamente el proyecto, la tabla ni las políticas de Supabase. Para probar la sincronización se necesita un backend configurado y un usuario habilitado.

## Configuración local de Supabase

En la **raíz del proyecto**, crear o editar el archivo `local.properties`. Además de la ruta habitual del SDK, agregar las siguientes propiedades, reemplazando los valores de ejemplo:

```properties
SUPABASE_URL=https://TU_PROYECTO.supabase.co/
SUPABASE_PUBLISHABLE_KEY=TU_CLAVE_PUBLICABLE
```

La URL **debe comenzar con `https://` y terminar con `/`**. Utilizar únicamente la clave pública/publicable destinada al cliente; **nunca** colocar una clave `service_role`, claves secretas ni contraseñas en la aplicación.

`local.properties` está excluido del repositorio Git. La configuración se lee desde `app/build.gradle.kts` y se incorpora a `BuildConfig` durante la compilación. No se recomienda subir al repositorio archivos con credenciales reales.

La tabla remota utilizada es `public.recordatorios`, con campos correspondientes al DTO de la aplicación: `id`, `usuario_id`, `titulo`, `descripcion`, `categoria`, `prioridad`, `estado`, `fecha_aviso` y `ubicacion`. Su configuración debe respetar los tipos y las políticas de acceso del proyecto Supabase.

## Instalación y ejecución

1. Abrir la carpeta raíz del proyecto en Android Studio.
2. Esperar la sincronización de Gradle y la instalación de las dependencias necesarias.
3. Configurar `local.properties` según la sección anterior.
4. Seleccionar un dispositivo Android compatible y ejecutar la configuración **app**.
5. Iniciar sesión con un usuario existente de Supabase Auth.
6. Crear, editar, completar o eliminar recordatorios. Para comprobar el comportamiento offline, realizar cambios sin Internet y luego restablecer la conexión.

Desde una terminal de Windows, también se puede intentar compilar la variante de depuración con:

```powershell
.\gradlew.bat assembleDebug
```

El APK de depuración se genera normalmente en `app/build/outputs/apk/debug/` cuando la compilación finaliza correctamente.

## Pruebas funcionales realizadas

Durante el desarrollo se realizaron pruebas manuales en un dispositivo Android físico, con resultados satisfactorios en los escenarios comprobados:

- Persistencia de preferencias después de cerrar y volver a abrir la aplicación.
- Creación, edición y eliminación de recordatorios.
- Consulta de datos guardados localmente.
- Operaciones sin conexión y sincronización posterior al restablecer Internet.
- Recuperación de conectividad y visualización del estado de sincronización.
- Verificación manual de que no se generaran duplicados en los escenarios ensayados.

Estas pruebas no equivalen a una cobertura automatizada exhaustiva ni acreditan una simulación controlada de todos los códigos HTTP o tiempos de espera.

## Evolución de las actividades integradoras

- **API 1:** fundamentos de Kotlin, modelo de recordatorios y primera interfaz con Compose.
- **API 2:** organización en capas, repositorios, ViewModels, formularios y navegación.
- **API 3:** Room, DataStore, integración remota, autenticación, sincronización local-first y manejo de conectividad.

El informe técnico de la actividad complementa este README con las decisiones de arquitectura, el flujo de datos y el detalle de las pruebas.

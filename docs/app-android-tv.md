# App Android TV (Tapo TV Viewer)

## Requisitos

- Android TV / Google TV con Android 8.0+ (minSdk 26) y **depuración ADB por red** activada.
- JDK 17+ (probado con 21), Android SDK (platform 34 y platform-tools).
- Cámaras con Cuenta de cámara y IP fija ([guía](configurar-camaras.md)).

## Compilar

```powershell
cd android-tv
# local.properties (no se versiona) con la ruta del SDK:
"sdk.dir=C:/Android" | Set-Content local.properties -Encoding ascii
.\gradlew.bat assembleDebug
```

APK: `android-tv\app\build\outputs\apk\debug\app-debug.apk`

## Instalar por ADB

```powershell
adb connect IP_DEL_TV:5555
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell monkey -p com.smartgenius.tapotv -c android.intent.category.LEANBACK_LAUNCHER 1
```

Si el TV queda `offline` tras instalar: `adb disconnect` y vuelve a `adb connect`.

## Primer uso

1. Abre la app → **Ajustes → Cámaras → Añadir cámara**.
2. Nombre, IP, puerto (554), usuario y contraseña de la Cuenta de cámara. Rutas de stream: `stream1` (pantalla completa) y `stream2` (cuadrícula).
3. Vuelve al inicio: se muestra la cuadrícula. **OK** abre una cámara a pantalla completa; **←/→** cambia de cámara; **OK** en pantalla completa activa/silencia el audio; **Atrás** vuelve.

## Personalización (Ajustes → Personalización)

Título, descripción, idioma (sistema/español/inglés), color de acento, columnas (auto/1–4), calidad de la cuadrícula (baja/alta), mostrar nombres, reloj y silenciar en pantalla completa. Con 4 o más cámaras aparece un aviso de rendimiento (no hay límite).

## Protector de pantalla

La app mantiene la pantalla encendida mientras está abierta. Si tu TV aun así lo activa (algunos fabricantes lo ignoran), desactívalo en *Ajustes del TV → Preferencias del dispositivo → Protector de pantalla*, o vía ADB:

```powershell
adb shell settings put secure screensaver_enabled 0
```

## Notas

- Credenciales cifradas con Android Keystore en el propio TV; no salen de él.
- Varias cámaras HD simultáneas pueden saturar el decodificador del TV; por eso la cuadrícula usa el sub‑stream.
- Idiomas: añade `res/values-XX/strings.xml` y una opción en `SettingsScreen.kt`.

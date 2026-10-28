# Solución de problemas

| Síntoma | Causa / solución |
|---|---|
| `Connection refused` en el puerto 554 | La Cuenta de cámara no está creada/guardada. Ver [configurar-camaras](configurar-camaras.md). |
| `401 Unauthorized` | Usuario/contraseña incorrectos (es la Cuenta de cámara, no la de Tapo). |
| Cambió la IP | Usa reserva DHCP o IP estática y actualiza el stream. |
| Etiquetas de IP viejas en el visor web | Caché: `Ctrl+Shift+R`. |
| Video con retraso / cortes en el TV | Usa calidad baja en la cuadrícula, menos cámaras, o mejor señal Wi‑Fi de la cámara. |
| La app se queda en "Conectando…" | Se reintenta cada 3 s. Comprueba IP, puerto y que el TV esté en la misma red/VLAN. |
| ADB `offline` | `adb disconnect` + `adb connect IP:5555`. |
| Salta el protector de pantalla | Ver [app-android-tv](app-android-tv.md#protector-de-pantalla). |
| Error de Gradle con rutas del SDK | En `local.properties` usa `sdk.dir=C:/Android` (barras normales). |
| `frame.jpeg` da 500 en go2rtc | Requiere ffmpeg; no es necesario para el visor. |

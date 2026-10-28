# Configurar las cámaras Tapo

Aplica a la Tapo C110 y modelos similares con alimentación fija.

## 1. Crear la Cuenta de cámara

El RTSP está cerrado (puerto 554) hasta que se crea esta cuenta. **No** es la cuenta de TP-Link/Tapo.

1. App Tapo → selecciona la cámara → **Ajustes** (engranaje).
2. **Avanzado** → **Cuenta de cámara**.
3. Crea usuario y contraseña (6–32 caracteres) y **guarda**.
4. Si tienes varias cámaras, puedes usar la misma cuenta en todas.

> Si olvidaste la contraseña, vuelve a esta pantalla y define una nueva.

## 2. Fijar la IP

Para que la IP no cambie al reiniciar el router, usa **una** de estas opciones:

- **Reserva DHCP en el router** (recomendado): asocia la MAC de la cámara a una IP fija.
- **IP estática en la cámara**: App Tapo → Ajustes → Avanzado → **Información del dispositivo** → Dirección IP → *Estática*. Indica IP, máscara (`255.255.255.0`), puerta de enlace (la del router) y DNS (por ejemplo la del router o `8.8.8.8`). Elige IPs fuera del rango DHCP.

Ejemplo usado en este proyecto: `192.168.1.200` y `192.168.1.201`.

## 3. URLs RTSP

```
rtsp://USUARIO:CONTRASENA@IP:554/stream1   # calidad alta (HD)
rtsp://USUARIO:CONTRASENA@IP:554/stream2   # calidad baja (ligera)
```

- Si la contraseña tiene caracteres especiales (`@ : / # ?`) hay que codificarlos en la URL (`@` → `%40`). `configurar.ps1` ya lo hace; en la app de TV se escribe tal cual.
- Códec: H.264 con audio PCMA (G.711 A-law).
- Modelos de doble lente usan otros streams (por ejemplo `stream6`/`stream7` para el teleobjetivo).

## 4. Probar

Con VLC: *Medio → Abrir ubicación de red* y pega la URL. Si abre, la cámara está lista. Si no, mira [solución de problemas](solucion-problemas.md).

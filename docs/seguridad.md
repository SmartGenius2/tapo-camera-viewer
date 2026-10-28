# Seguridad

- **Solo LAN**: no hagas port forwarding de 554 (RTSP), 1984 (go2rtc) ni 8555 (WebRTC). Desactiva UPnP si no lo necesitas.
- **Acceso remoto**: usa una VPN (WireGuard/Tailscale) hacia tu red, no abras puertos.
- **Credenciales**: `go2rtc.yaml` las contiene en claro → está en `.gitignore`. No lo subas ni lo compartas. En el TV se guardan cifradas.
- **Cuenta de cámara**: usa una contraseña propia y larga, distinta de la de tu cuenta Tapo/TP-Link.
- **Sin autenticación en el visor web**: si lo expones a la LAN (`listen: ":1984"`), añade `username`/`password` bajo `api:` en go2rtc.
- **Firmware**: mantén las cámaras actualizadas desde la app Tapo.
- **Si subiste un secreto por error**: cambia la Cuenta de cámara de inmediato; borrar el commit no basta.

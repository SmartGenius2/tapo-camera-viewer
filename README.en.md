# Tapo Camera Viewer

LAN-only viewers for TP-Link Tapo cameras (tested with the **Tapo C110**) over RTSP — no cloud, nothing exposed to the Internet.

- [`web-viewer/`](web-viewer/): browser grid (WebRTC) served locally by [go2rtc](https://github.com/AlexxIT/go2rtc).
- [`android-tv/`](android-tv/): **Tapo TV Viewer**, an Android TV app (Media3/ExoPlayer, direct RTSP). English and Spanish UI.

Quick start: create the *Camera Account* in the Tapo app for each camera, give each camera a fixed IP, then follow the Spanish guides in [`docs/`](docs/configurar-camaras.md) (RTSP URL: `rtsp://USER:PASS@IP:554/stream1` main, `stream2` low-res).

Never port-forward 554 or the viewer; use a VPN for remote access. Live view only (no recordings). Battery cameras (C410, C420, D230) are not supported.


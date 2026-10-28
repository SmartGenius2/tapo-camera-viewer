# Genera go2rtc.yaml pidiendo cámaras y la "Cuenta de cámara" de Tapo.
# El archivo resultante contiene credenciales: está en .gitignore, no lo compartas.
$dir = $PSScriptRoot
$n = [int](Read-Host "Cuantas camaras vas a configurar")
$user = Read-Host "Usuario de la Cuenta de camara (6-32 caracteres)"
$sec  = Read-Host "Contrasena de la Cuenta de camara (6-32 caracteres)" -AsSecureString
$pass = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($sec))
$u = [uri]::EscapeDataString($user); $p = [uri]::EscapeDataString($pass)

$streams = ""
for ($i = 1; $i -le $n; $i++) {
    $name = Read-Host "Nombre de la camara $i (sin espacios, ej. sala)"
    $ip   = Read-Host "IP de la camara $i (ej. 192.168.1.200)"
    $streams += "  ${name}: rtsp://${u}:${p}@${ip}:554/stream1`n"
}

$yaml = @"
api:
  listen: "127.0.0.1:1984"      # solo accesible desde este PC
  static_dir: "web"
  origin: "*"
rtsp:
  listen: ""                    # no exponer RTSP
webrtc:
  listen: ":8555"
streams:
$streams
"@
Set-Content -Path "$dir\go2rtc.yaml" -Value $yaml -Encoding ascii
Write-Host "go2rtc.yaml creado. Ahora ejecuta .\iniciar.ps1"

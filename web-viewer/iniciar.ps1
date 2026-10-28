Set-Location $PSScriptRoot
if (-not (Test-Path .\go2rtc.yaml)) { Write-Host "Falta go2rtc.yaml. Ejecuta primero .\configurar.ps1"; exit 1 }
Start-Process "http://localhost:1984/"
.\bin\go2rtc.exe -config go2rtc.yaml

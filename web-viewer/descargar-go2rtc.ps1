# Descarga go2rtc (Windows amd64) a bin\go2rtc.exe
$dir = Join-Path $PSScriptRoot "bin"
New-Item -ItemType Directory -Force $dir | Out-Null
$url = "https://github.com/AlexxIT/go2rtc/releases/download/v1.9.15/go2rtc_win64.zip"
$zip = Join-Path $env:TEMP "go2rtc.zip"
Invoke-WebRequest $url -OutFile $zip
Expand-Archive $zip -DestinationPath $dir -Force
Remove-Item $zip
Write-Host "Listo: $dir\go2rtc.exe"

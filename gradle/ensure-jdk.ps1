# Windows counterpart of ensure-jdk.sh, called from gradlew.bat.
# Prints nothing if a JDK 17+ is already available; otherwise downloads a portable Eclipse Temurin
# JDK 21 into .jdk\ in this repo (once) and prints its path for the wrapper to use as JAVA_HOME.

$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'
$MinVersion = 17
$JdkVersion = 21
$Root = Split-Path -Parent $PSScriptRoot
$LocalJdk = Join-Path $Root ".jdk\temurin-$JdkVersion"

function Get-MajorVersion($javaExe) {
    try {
        $out = & $javaExe -version 2>&1 | Out-String
        if ($out -match 'version "(\d+)(\.(\d+))?') {
            if ($Matches[1] -eq '1') { return [int]$Matches[3] } else { return [int]$Matches[1] }
        }
    } catch { }
    return 0
}

function Test-Usable($javaExe) {
    return (Test-Path $javaExe) -and ((Get-MajorVersion $javaExe) -ge $MinVersion)
}

if ($env:JAVA_HOME -and (Test-Usable (Join-Path $env:JAVA_HOME 'bin\java.exe'))) { exit 0 }
if (-not $env:JAVA_HOME) {
    $onPath = Get-Command java.exe -ErrorAction SilentlyContinue
    if ($onPath -and (Test-Usable $onPath.Source)) { exit 0 }
}
if (Test-Usable (Join-Path $LocalJdk 'bin\java.exe')) { Write-Output $LocalJdk; exit 0 }

$arch = if ($env:PROCESSOR_ARCHITECTURE -eq 'ARM64') { 'aarch64' } else { 'x64' }
$url = "https://api.adoptium.net/v3/binary/latest/$JdkVersion/ga/windows/$arch/jdk/hotspot/normal/eclipse"
[Console]::Error.WriteLine("No JDK $MinVersion+ found. Downloading Temurin $JdkVersion into .jdk\ (one-time, ~200 MB)...")

$tmp = Join-Path $Root ".jdk\download.$PID"
New-Item -ItemType Directory -Force -Path $tmp | Out-Null
try {
    $zip = Join-Path $tmp 'jdk.zip'
    Invoke-WebRequest -Uri $url -OutFile $zip -UseBasicParsing
    Expand-Archive -Path $zip -DestinationPath $tmp
    if (Test-Path $LocalJdk) { Remove-Item -Recurse -Force $LocalJdk }
    Move-Item (Get-ChildItem $tmp -Directory -Filter 'jdk-*' | Select-Object -First 1).FullName $LocalJdk
} finally {
    Remove-Item -Recurse -Force $tmp -ErrorAction SilentlyContinue
}

if (-not (Test-Usable (Join-Path $LocalJdk 'bin\java.exe'))) {
    [Console]::Error.WriteLine("Downloaded JDK doesn't run; please install JDK $MinVersion+ manually.")
    exit 1
}
Write-Output $LocalJdk

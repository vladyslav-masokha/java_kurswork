param(
    [int]$Port = 8080
)

$ErrorActionPreference = "Stop"

& "$PSScriptRoot\compile.ps1"

$defaultJdk = "C:\Program Files\Android\jdk\jdk-8.0.302.8-hotspot\jdk8u302-b08"
$javaHome = if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME "bin\java.exe"))) { $env:JAVA_HOME } else { $defaultJdk }
$java = Join-Path $javaHome "bin\java.exe"

if (-not (Test-Path $java)) {
    throw "java.exe was not found. Install JDK/JRE 8+ or set JAVA_HOME."
}

& $java -cp "target/classes" ua.edu.duit.medical.Application $Port

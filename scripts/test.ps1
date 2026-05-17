$ErrorActionPreference = "Stop"

& "$PSScriptRoot\compile.ps1" -Tests

$defaultJdk = "C:\Program Files\Android\jdk\jdk-8.0.302.8-hotspot\jdk8u302-b08"
$javaHome = if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME "bin\java.exe"))) { $env:JAVA_HOME } else { $defaultJdk }
$java = Join-Path $javaHome "bin\java.exe"

& $java -cp "target/classes;target/test-classes" ua.edu.duit.medical.MedicalServiceTests


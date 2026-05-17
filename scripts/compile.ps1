param(
    [string]$Output = "target/classes",
    [switch]$Tests
)

$ErrorActionPreference = "Stop"

$defaultJdk = "C:\Program Files\Android\jdk\jdk-8.0.302.8-hotspot\jdk8u302-b08"
$jdkHome = if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME "bin\javac.exe"))) { $env:JAVA_HOME } else { $defaultJdk }
$javac = Join-Path $jdkHome "bin\javac.exe"

if (-not (Test-Path $javac)) {
    throw "javac.exe was not found. Install JDK 8+ or set JAVA_HOME."
}

New-Item -ItemType Directory -Force $Output | Out-Null

$mainSources = Get-ChildItem -Path "src/main/java" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
if (-not $mainSources) {
    throw "No Java files found in src/main/java."
}

& $javac -encoding UTF-8 -d $Output $mainSources

if ($Tests) {
    $testOutput = "target/test-classes"
    New-Item -ItemType Directory -Force $testOutput | Out-Null
    $testSources = Get-ChildItem -Path "src/test/java" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
    if ($testSources) {
        & $javac -encoding UTF-8 -cp $Output -d $testOutput $testSources
    }
}

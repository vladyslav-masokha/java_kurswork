$ErrorActionPreference = "Stop"

$frontend = Join-Path (Split-Path $PSScriptRoot -Parent) "frontend"

if (-not (Test-Path (Join-Path $frontend "node_modules"))) {
    Push-Location $frontend
    try {
        npm.cmd install
    } finally {
        Pop-Location
    }
}

Push-Location $frontend
try {
    npm.cmd run dev
} finally {
    Pop-Location
}


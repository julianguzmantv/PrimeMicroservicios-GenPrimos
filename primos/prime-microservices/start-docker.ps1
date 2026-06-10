# ============================================
# Script de inicio con Docker Compose
# ============================================

Write-Host "=== PRIME MICROSERVICES - INICIO CON DOCKER ===" -ForegroundColor Cyan
Write-Host ""

# Verificar si Docker está corriendo
Write-Host "Verificando Docker..." -ForegroundColor Yellow
try {
    docker ps | Out-Null
    Write-Host "  ✓ Docker está activo" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Error: Docker no está corriendo. Inicia Docker Desktop primero." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Paso 1: Construyendo imágenes Docker..." -ForegroundColor Yellow
Write-Host "  (Esto puede tardar 3-5 minutos la primera vez)" -ForegroundColor Gray
Write-Host ""

docker build -t api-gateway:latest -f api-gateway/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Host "Error en api-gateway" -ForegroundColor Red; exit 1 }

docker build -t service-primes:latest -f service-primes/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Host "Error en service-primes" -ForegroundColor Red; exit 1 }

docker build -t service-worker:latest -f service-worker/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Host "Error en service-worker" -ForegroundColor Red; exit 1 }

docker build -t service-stats:latest -f service-stats/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Host "Error en service-stats" -ForegroundColor Red; exit 1 }

Write-Host ""
Write-Host "  ✓ Todas las imágenes construidas exitosamente" -ForegroundColor Green
Write-Host ""

Write-Host "Paso 2: Iniciando servicios con Docker Compose..." -ForegroundColor Yellow
docker compose up -d

if ($LASTEXITCODE -ne 0) {
    Write-Host "  ✗ Error al iniciar servicios" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Paso 3: Verificando estado de contenedores..." -ForegroundColor Yellow
Start-Sleep -Seconds 5
docker ps

Write-Host ""
Write-Host "Paso 4: Esperando que los servicios estén listos..." -ForegroundColor Yellow
Write-Host "  (MySQL y RabbitMQ tardan ~30 segundos en iniciar)" -ForegroundColor Gray
Start-Sleep -Seconds 30

Write-Host ""
Write-Host "=== SERVICIOS INICIADOS ===" -ForegroundColor Green
Write-Host ""
Write-Host "URLs disponibles:" -ForegroundColor Cyan
Write-Host "  • API Gateway:    http://localhost:8080" -ForegroundColor White
Write-Host "  • Service Primes: http://localhost:8081" -ForegroundColor White
Write-Host "  • Service Stats:  http://localhost:8083" -ForegroundColor White
Write-Host "  • RabbitMQ UI:    http://localhost:15672 (guest/guest)" -ForegroundColor White
Write-Host ""

Write-Host "Prueba rápida:" -ForegroundColor Cyan
Write-Host '  $r = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=3&digitos=12"' -ForegroundColor Gray
Write-Host '  Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/status/$($r.id)"' -ForegroundColor Gray
Write-Host ""

Write-Host "Ver logs de un servicio:" -ForegroundColor Cyan
Write-Host "  docker compose logs -f service-worker" -ForegroundColor Gray
Write-Host ""

Write-Host "Detener todos los servicios:" -ForegroundColor Cyan
Write-Host "  docker compose down" -ForegroundColor Gray
Write-Host ""

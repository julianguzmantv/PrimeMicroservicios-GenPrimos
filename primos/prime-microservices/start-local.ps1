# ============================================
# Script de inicio para desarrollo local
# ============================================

Write-Host "=== PRIME MICROSERVICES - INICIO LOCAL CON MAVEN ===" -ForegroundColor Cyan
Write-Host ""

# Verificar Maven
Write-Host "Verificando Maven..." -ForegroundColor Yellow
try {
    $mvnVersion = mvn -version 2>&1 | Select-String "Apache Maven" | Select-Object -First 1
    Write-Host "  ✓ $mvnVersion" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Error: Maven no encontrado. Instala Maven 3.9+ primero." -ForegroundColor Red
    exit 1
}

# Verificar Java
Write-Host "Verificando Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | Select-Object -First 1
    Write-Host "  ✓ $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Error: Java no encontrado. Instala JDK 17+ primero." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "IMPORTANTE:" -ForegroundColor Yellow
Write-Host "  • Asegúrate de tener MySQL corriendo en localhost:3306" -ForegroundColor White
Write-Host "    Usuario: root, Password: 12345, Database: primes" -ForegroundColor White
Write-Host "  • Asegúrate de tener RabbitMQ corriendo (puedes usar Docker):" -ForegroundColor White
Write-Host "    docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.13-management" -ForegroundColor Gray
Write-Host ""

$continue = Read-Host "¿Continuar? (S/N)"
if ($continue -ne "S" -and $continue -ne "s") {
    Write-Host "Cancelado por el usuario" -ForegroundColor Yellow
    exit 0
}

Write-Host ""
Write-Host "Paso 1: Compilando todos los módulos..." -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "  ✗ Error en la compilación" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "  ✓ Compilación exitosa" -ForegroundColor Green
Write-Host ""

Write-Host "=== INICIANDO SERVICIOS ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Abrirás 4 terminales de PowerShell." -ForegroundColor Yellow
Write-Host "Copia y pega cada comando en su respectiva terminal:" -ForegroundColor Yellow
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "TERMINAL 1 - SERVICE-WORKER (puerto N/A, consume cola)" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host 'cd "d:\Estudio\Estudio\Universidad\12 Semestre\DISTRIBUIDOS\CORTE 3\primos\primos\prime-microservices\service-worker"; mvn spring-boot:run' -ForegroundColor White
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "TERMINAL 2 - SERVICE-STATS (puerto 8083)" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host 'cd "d:\Estudio\Estudio\Universidad\12 Semestre\DISTRIBUIDOS\CORTE 3\primos\primos\prime-microservices\service-stats"; mvn spring-boot:run' -ForegroundColor White
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "TERMINAL 3 - SERVICE-PRIMES (puerto 8081)" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host 'cd "d:\Estudio\Estudio\Universidad\12 Semestre\DISTRIBUIDOS\CORTE 3\primos\primos\prime-microservices\service-primes"; mvn spring-boot:run' -ForegroundColor White
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "TERMINAL 4 - API-GATEWAY (puerto 8080)" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host 'cd "d:\Estudio\Estudio\Universidad\12 Semestre\distribuidos\CORTE 3\primos\primos\prime-microservices\api-gateway"; mvn spring-boot:run' -ForegroundColor White
Write-Host ""

Write-Host "NOTA:" -ForegroundColor Yellow
Write-Host "  • Inicia los servicios EN ORDEN (1, 2, 3, 4)" -ForegroundColor White
Write-Host "  • Espera que cada uno muestre 'Started <Service>Application' antes de iniciar el siguiente" -ForegroundColor White
Write-Host "  • El gateway (terminal 4) debe iniciarse al final" -ForegroundColor White
Write-Host ""

Write-Host "Después de iniciar todos, prueba con:" -ForegroundColor Cyan
Write-Host '  $r = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=3&digitos=12"' -ForegroundColor Gray
Write-Host '  Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/status/$($r.id)"' -ForegroundColor Gray
Write-Host ""

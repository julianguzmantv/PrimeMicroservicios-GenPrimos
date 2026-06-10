# ============================================
# PRUEBAS DEL SISTEMA DE MICROSERVICIOS
# ============================================
# Ejecutar después de tener corriendo:
# - api-gateway (puerto 8080)
# - service-primes (puerto 8081)
# - service-worker (consumidor RabbitMQ)
# ============================================

Write-Host "`n=== PRUEBA 1: 5 PRIMOS DE 3 DÍGITOS ===" -ForegroundColor Cyan

# Crear solicitud
Write-Host "`n1. Creando solicitud..." -ForegroundColor Yellow
$request1 = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=5&digitos=3"
Write-Host "   Request ID: $($request1.id)" -ForegroundColor Green
Write-Host "   Estado inicial: $($request1.estado)" -ForegroundColor White

# Esperar procesamiento
Write-Host "`n2. Esperando 8 segundos para procesamiento..." -ForegroundColor Yellow
Start-Sleep -Seconds 8

# Verificar estado
Write-Host "`n3. Consultando estado..." -ForegroundColor Yellow
$status1 = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/status/$($request1.id)"
Write-Host "   Estado: $($status1.estado)" -ForegroundColor $(if($status1.estado -eq 'COMPLETED'){'Green'}else{'Yellow'})
Write-Host "   Progreso: $($status1.generados)/$($status1.cantidad)" -ForegroundColor White

# Obtener resultados
if ($status1.estado -eq 'COMPLETED') {
    Write-Host "`n4. Obteniendo resultados..." -ForegroundColor Yellow
    $result1 = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/result/$($request1.id)"
    Write-Host "   Primos generados:" -ForegroundColor Green
    $result1.primes | ForEach-Object { Write-Host "      $_" -ForegroundColor White }
} else {
    Write-Host "`n   ⚠ Aún en proceso. Esperar más tiempo." -ForegroundColor Red
}

# ============================================

Write-Host "`n`n=== PRUEBA 2: 3 PRIMOS DE 12 DÍGITOS ===" -ForegroundColor Cyan

# Crear solicitud
Write-Host "`n1. Creando solicitud..." -ForegroundColor Yellow
$request2 = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=3&digitos=12"
Write-Host "   Request ID: $($request2.id)" -ForegroundColor Green
Write-Host "   Estado inicial: $($request2.estado)" -ForegroundColor White

# Esperar procesamiento (más tiempo para 12 dígitos)
Write-Host "`n2. Esperando 20 segundos para procesamiento..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

# Verificar estado
Write-Host "`n3. Consultando estado..." -ForegroundColor Yellow
$status2 = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/status/$($request2.id)"
Write-Host "   Estado: $($status2.estado)" -ForegroundColor $(if($status2.estado -eq 'COMPLETED'){'Green'}else{'Yellow'})
Write-Host "   Progreso: $($status2.generados)/$($status2.cantidad)" -ForegroundColor White

# Obtener resultados
if ($status2.estado -eq 'COMPLETED') {
    Write-Host "`n4. Obteniendo resultados..." -ForegroundColor Yellow
    $result2 = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/result/$($request2.id)"
    Write-Host "   Primos generados (12 dígitos):" -ForegroundColor Green
    $result2.primes | ForEach-Object { Write-Host "      $_" -ForegroundColor White }
} else {
    Write-Host "`n   ⚠ Aún en proceso. Esperar más tiempo o consultar nuevamente." -ForegroundColor Red
}

# ============================================

Write-Host "`n`n=== RESUMEN DE PRUEBAS ===" -ForegroundColor Cyan
Write-Host "Prueba 1 (3 dígitos): Request ID $($request1.id) - Estado: $($status1.estado)" -ForegroundColor White
Write-Host "Prueba 2 (12 dígitos): Request ID $($request2.id) - Estado: $($status2.estado)" -ForegroundColor White
Write-Host "`n=== PRUEBAS FINALIZADAS ===" -ForegroundColor Green

# Guía de Ejecución y Pruebas — Prime Microservices


## 📌 Opción Recomendada: Docker Compose (la más rápida)

### Paso 1: Construir las imágenes Docker

```powershell
cd "d:\Estudio\Estudio\Universidad\12 Semestre\DISTRIBUIDOS\CORTE 3\primos\primos\prime-microservices"

docker build -t api-gateway:latest -f api-gateway/Dockerfile .
docker build -t service-primes:latest -f service-primes/Dockerfile .
docker build -t service-worker:latest -f service-worker/Dockerfile .
docker build -t service-stats:latest -f service-stats/Dockerfile .
```

**Respuesta esperada** (cada build):
```
[+] Building 45.2s (17/17) FINISHED
 => [internal] load build definition
 => => transferring dockerfile
 ...
 => exporting to image
 => => writing image sha256:...
 => => naming to docker.io/library/api-gateway:latest
```

**Tiempo estimado**: 3-5 minutos para las 4 imágenes.

---

### Paso 2: Levantar todos los servicios

```powershell
docker compose up -d
```

**Respuesta esperada**:
```
[+] Running 7/7
 ✔ Network prime-microservices_default           Created
 ✔ Container prime-microservices-mysql-1         Started
 ✔ Container prime-microservices-rabbitmq-1      Started
 ✔ Container prime-microservices-service-stats-1 Started
 ✔ Container prime-microservices-service-worker-1 Started
 ✔ Container prime-microservices-service-primes-1 Started
 ✔ Container prime-microservices-api-gateway-1   Started
```

**Tiempo de arranque**: ~30 segundos (MySQL y RabbitMQ tardan un poco más).

---

### Paso 3: Verificar que todos los contenedores están corriendo

```powershell
docker ps
```

**Respuesta esperada**:
```
CONTAINER ID   IMAGE                    STATUS         PORTS                                           NAMES
abc123def456   api-gateway:latest       Up 45 seconds  0.0.0.0:8080->8080/tcp                          prime-microservices-api-gateway-1
bcd234efa567   service-primes:latest    Up 45 seconds  0.0.0.0:8081->8081/tcp                          prime-microservices-service-primes-1
cde345fgb678   service-worker:latest    Up 45 seconds                                                  prime-microservices-service-worker-1
def456ghc789   service-stats:latest     Up 45 seconds  0.0.0.0:8083->8083/tcp                          prime-microservices-service-stats-1
efg567hid890   rabbitmq:3.13-management Up 45 seconds  0.0.0.0:5672->5672/tcp, 0.0.0.0:15672->15672/tcp prime-microservices-rabbitmq-1
fgh678ije901   mysql:8.0                Up 45 seconds  0.0.0.0:3306->3306/tcp, 33060/tcp               prime-microservices-mysql-1
```

**Nota**: Todos deben mostrar `Up` en STATUS.

---

### Paso 4: Ver logs de un servicio (opcional)

```powershell
docker compose logs -f service-primes
```

**Respuesta esperada** (últimas líneas):
```
service-primes-1  | 2025-12-02T15:23:45.123  INFO 1 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8081
service-primes-1  | 2025-12-02T15:23:45.234  INFO 1 --- [main] c.p.ServicePrimesApplication             : Started ServicePrimesApplication in 12.345 seconds
```

Presiona `Ctrl+C` para salir de los logs.

---

## 🧪 Pruebas de la API

### Prueba 1: Generar 5 primos de 3 dígitos (rápido)

```powershell
$response = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=5&digitos=3"
$id1 = $response.id
Write-Host "Request ID: $id1"
```

**Respuesta esperada**:
```json
Request ID: 8d61a067-c3f5-4a2b-9e7f-1a2b3c4d5e6f
```

**Consultar estado**:
```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/status/$id1"
```

**Respuesta esperada** (después de ~5 segundos):
```json
{
  "id": "8d61a067-c3f5-4a2b-9e7f-1a2b3c4d5e6f",
  "cantidad": 5,
  "generados": 5,
  "estado": "COMPLETED",
  "digitos": 3
}
```

**Obtener resultados**:
```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/result/$id1"
```

**Respuesta esperada**:
```json
{
  "id": "8d61a067-c3f5-4a2b-9e7f-1a2b3c4d5e6f",
  "cantidad": 5,
  "generados": 5,
  "estado": "COMPLETED",
  "digitos": 3,
  "primes": [
    "839",
    "919",
    "251",
    "947",
    "107"
  ]
}
```

**Nota**: Los números serán diferentes en cada ejecución (son aleatorios y únicos).

---

### Prueba 2: Generar 3 primos de 12 dígitos (números grandes)

```powershell
Write-Host "Solicitando 3 primos de 12 dígitos...`n"
$response = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=3&digitos=12"
$id2 = $response.id
Write-Host "Request ID: $id2"
```

**Respuesta esperada**:
```
Solicitando 3 primos de 12 dígitos...

Request ID: f7e8d9c0-b1a2-3456-7890-abcdef123456
```

**Esperar un momento** (la generación de primos grandes tarda más):
```powershell
Write-Host "`nEsperando generación (15-20 segundos)..."
Start-Sleep -Seconds 18
```

**Consultar estado**:
```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/status/$id2"
```

**Respuesta esperada**:
```json
{
  "id": "f7e8d9c0-b1a2-3456-7890-abcdef123456",
  "cantidad": 3,
  "generados": 3,
  "estado": "COMPLETED",
  "digitos": 12
}
```

**Obtener resultados**:
```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/result/$id2" | ConvertTo-Json -Depth 4
```

**Respuesta esperada**:
```json
{
  "id": "f7e8d9c0-b1a2-3456-7890-abcdef123456",
  "cantidad": 3,
  "generados": 3,
  "estado": "COMPLETED",
  "digitos": 12,
  "primes": [
    "917379949621",
    "209672198851",
    "403070987183"
  ]
}
```

**Validación**: Todos los números deben tener exactamente 12 dígitos.

---

### Prueba 3: Consultar una solicitud en progreso (estado IN_PROGRESS)

```powershell
# Solicitar muchos primos grandes
$response = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=20&digitos=15"
$id3 = $response.id

# Consultar inmediatamente
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/status/$id3"
```

**Respuesta esperada** (consultado rápidamente):
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "cantidad": 20,
  "generados": 3,
  "estado": "IN_PROGRESS",
  "digitos": 15
}
```

**Esperar y volver a consultar**:
```powershell
Start-Sleep -Seconds 30
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/status/$id3"
```

**Respuesta esperada** (después de esperar):
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "cantidad": 20,
  "generados": 20,
  "estado": "COMPLETED",
  "digitos": 15
}
```

---

### Prueba 4: Verificar ausencia de duplicados

```powershell
# Solicitar 10 primos de 3 dígitos
$response = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=10&digitos=3"
$id4 = $response.id

Start-Sleep -Seconds 8

$result = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/result/$id4"
$primes = $result.primes

# Contar únicos
$unique = $primes | Select-Object -Unique
Write-Host "Total de primos: $($primes.Count)"
Write-Host "Primos únicos: $($unique.Count)"
```

**Respuesta esperada**:
```
Total de primos: 10
Primos únicos: 10
```

**Validación**: `Total de primos` debe ser igual a `Primos únicos` (sin duplicados).

---

### Prueba 5: Verificar la cola de RabbitMQ (monitoreo)

**Abrir el dashboard de RabbitMQ**:
```powershell
Start-Process "http://localhost:15672"
```

**Credenciales**:
- Usuario: `guest`
- Password: `guest`

**Qué verificar**:
1. En la pestaña **Queues and Streams**, buscar `prime.tasks`.
2. Ver columna **Consumers**: debe mostrar `1` (el worker está consumiendo).
3. Ver **Messages**: debe ser `0` si no hay tareas pendientes (o un número si acabas de encolar).

---

## 🔍 Verificación de la Base de Datos (opcional)

**Conectar a MySQL**:
```powershell
docker exec -it prime-microservices-mysql-1 mysql -u root -p12345 primes
```

**Consultar solicitudes**:
```sql
SELECT id, cantidad, digitos, estado, generados, created_at FROM prime_requests ORDER BY created_at DESC LIMIT 5;
```

**Respuesta esperada**:
```
+--------------------------------------+----------+---------+-----------+-----------+---------------------+
| id                                   | cantidad | digitos | estado    | generados | created_at          |
+--------------------------------------+----------+---------+-----------+-----------+---------------------+
| f7e8d9c0-b1a2-3456-7890-abcdef123456 |        3 |      12 | COMPLETED |         3 | 2025-12-02 15:30:12 |
| 8d61a067-c3f5-4a2b-9e7f-1a2b3c4d5e6f |        5 |       3 | COMPLETED |         5 | 2025-12-02 15:25:45 |
+--------------------------------------+----------+---------+-----------+-----------+---------------------+
```

**Consultar primos generados**:
```sql
SELECT id, number, digits, request_id FROM primes ORDER BY id DESC LIMIT 10;
```

**Respuesta esperada**:
```
+----+----------------+--------+--------------------------------------+
| id | number         | digits | request_id                           |
+----+----------------+--------+--------------------------------------+
| 23 | 403070987183   |     12 | f7e8d9c0-b1a2-3456-7890-abcdef123456 |
| 22 | 209672198851   |     12 | f7e8d9c0-b1a2-3456-7890-abcdef123456 |
| 21 | 917379949621   |     12 | f7e8d9c0-b1a2-3456-7890-abcdef123456 |
| 20 | 947            |      3 | 8d61a067-c3f5-4a2b-9e7f-1a2b3c4d5e6f |
| 19 | 107            |      3 | 8d61a067-c3f5-4a2b-9e7f-1a2b3c4d5e6f |
+----+----------------+--------+--------------------------------------+
```

**Salir de MySQL**:
```sql
exit;
```

---

## 📊 Script de Prueba Completo (copiar y pegar)

Puedes ejecutar todo en un solo bloque:

```powershell
# Script de prueba completa
Write-Host "=== PRUEBA COMPLETA PRIME MICROSERVICES ===" -ForegroundColor Cyan
Write-Host ""

# Prueba 1: 5 primos de 3 dígitos
Write-Host "Prueba 1: Generando 5 primos de 3 dígitos..." -ForegroundColor Yellow
$r1 = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=5&digitos=3"
$id1 = $r1.id
Write-Host "  Request ID: $id1" -ForegroundColor Gray
Start-Sleep -Seconds 6
$result1 = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/result/$id1"
Write-Host "  Estado: $($result1.estado)" -ForegroundColor Green
Write-Host "  Primos: $($result1.primes -join ', ')" -ForegroundColor Green
Write-Host ""

# Prueba 2: 3 primos de 12 dígitos
Write-Host "Prueba 2: Generando 3 primos de 12 dígitos..." -ForegroundColor Yellow
$r2 = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=3&digitos=12"
$id2 = $r2.id
Write-Host "  Request ID: $id2" -ForegroundColor Gray
Write-Host "  Esperando generación (20s)..." -ForegroundColor Gray
Start-Sleep -Seconds 20
$result2 = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/result/$id2"
Write-Host "  Estado: $($result2.estado)" -ForegroundColor Green
Write-Host "  Primos: $($result2.primes -join ', ')" -ForegroundColor Green
Write-Host ""

# Prueba 3: Verificar progreso en tiempo real
Write-Host "Prueba 3: Monitoreo de progreso en tiempo real (10 primos de 10 dígitos)..." -ForegroundColor Yellow
$r3 = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=10&digitos=10"
$id3 = $r3.id
Write-Host "  Request ID: $id3" -ForegroundColor Gray
For ($i = 1; $i -le 6; $i++) {
    Start-Sleep -Seconds 3
    $status = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/status/$id3"
    Write-Host "  [$i] Generados: $($status.generados)/$($status.cantidad) - Estado: $($status.estado)" -ForegroundColor Cyan
    if ($status.estado -eq 'COMPLETED') { break }
}
Write-Host ""

Write-Host "=== TODAS LAS PRUEBAS COMPLETADAS ===" -ForegroundColor Green
Write-Host ""
Write-Host "Para ver RabbitMQ Management UI: http://localhost:15672 (guest/guest)" -ForegroundColor Magenta
```

**Respuesta esperada del script completo**:
```
=== PRUEBA COMPLETA PRIME MICROSERVICES ===

Prueba 1: Generando 5 primos de 3 dígitos...
  Request ID: 12345678-abcd-1234-5678-abcdef123456
  Estado: COMPLETED
  Primos: 839, 919, 251, 947, 107

Prueba 2: Generando 3 primos de 12 dígitos...
  Request ID: 87654321-dcba-4321-8765-fedcba987654
  Esperando generación (20s)...
  Estado: COMPLETED
  Primos: 917379949621, 209672198851, 403070987183

Prueba 3: Monitoreo de progreso en tiempo real (10 primos de 10 dígitos)...
  Request ID: abcdef12-3456-7890-abcd-ef1234567890
  [1] Generados: 2/10 - Estado: IN_PROGRESS
  [2] Generados: 5/10 - Estado: IN_PROGRESS
  [3] Generados: 8/10 - Estado: IN_PROGRESS
  [4] Generados: 10/10 - Estado: COMPLETED

=== TODAS LAS PRUEBAS COMPLETADAS ===

Para ver RabbitMQ Management UI: http://localhost:15672 (guest/guest)
```

---

## 🛑 Detener los servicios

```powershell
docker compose down
```

**Respuesta esperada**:
```
[+] Running 7/7
 ✔ Container prime-microservices-api-gateway-1   Removed
 ✔ Container prime-microservices-service-primes-1 Removed
 ✔ Container prime-microservices-service-worker-1 Removed
 ✔ Container prime-microservices-service-stats-1 Removed
 ✔ Container prime-microservices-rabbitmq-1      Removed
 ✔ Container prime-microservices-mysql-1         Removed
 ✔ Network prime-microservices_default           Removed
```

---

## 🐛 Troubleshooting

### Error: "No se puede conectar al puerto 8080"

**Causa**: Los servicios aún no han terminado de arrancar.

**Solución**:
```powershell
# Ver el estado de los contenedores
docker ps

# Ver logs del gateway
docker compose logs -f api-gateway

# Esperar a ver: "Tomcat started on port 8080"
```

---

### Error: "Estado: PENDING por mucho tiempo"

**Causa**: El worker no está procesando la cola.

**Solución**:
```powershell
# Ver logs del worker
docker compose logs -f service-worker

# Verificar RabbitMQ
Start-Process "http://localhost:15672"
# En Queues → prime.tasks → verificar que "Consumers" sea >= 1
```

---

### Error: "Flyway checksum mismatch"

**Causa**: Las migraciones de BD fueron modificadas.

**Solución** (recrear BD):
```powershell
# Detener servicios
docker compose down

# Eliminar volúmenes (borra la BD)
docker volume rm prime-microservices_mysql-data

# Volver a levantar
docker compose up -d
```

---

## ✅ Checklist de Validación Final

- [ ] Todos los contenedores están en estado `Up` (`docker ps`)
- [ ] Endpoint `/primes/new` devuelve un UUID válido
- [ ] Endpoint `/primes/status/{id}` muestra progreso (generados/cantidad)
- [ ] Endpoint `/primes/result/{id}` devuelve lista de primos correcta
- [ ] Primos de 3 dígitos están entre 100 y 999
- [ ] Primos de 12 dígitos están entre 100000000000 y 999999999999
- [ ] No hay duplicados en los resultados de una misma solicitud
- [ ] RabbitMQ muestra 1+ consumidores en la cola `prime.tasks`
- [ ] Estado cambia: PENDING → IN_PROGRESS → COMPLETED
- [ ] MySQL contiene las tablas `prime_requests` y `primes` con datos

---

**¡Listo para la defensa del parcial!** 🎓

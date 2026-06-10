# Comandos Individuales para Iniciar Servicios

## Opción 1: Docker Compose (Recomendado - TODO EN UNO)

### Inicio completo automatizado

```powershell
# Ejecutar el script de inicio
.\start-docker.ps1
```

O manualmente:

```powershell
# 1. Construir imágenes
docker build -t api-gateway:latest -f api-gateway/Dockerfile .
docker build -t service-primes:latest -f service-primes/Dockerfile .
docker build -t service-worker:latest -f service-worker/Dockerfile .
docker build -t service-stats:latest -f service-stats/Dockerfile .

# 2. Iniciar todos los servicios
docker compose up -d

# 3. Ver logs (opcional)
docker compose logs -f

# 4. Detener todos
docker compose down
```

---

## Opción 2: Desarrollo Local con Maven (SERVICIOS INDIVIDUALES)

### Prerequisitos

```powershell
# Iniciar MySQL (si no está corriendo)
# Opción A: MySQL local instalado en Windows
# Asegúrate de tener DB 'primes' con usuario root/12345

# Opción B: MySQL en Docker
docker run -d --name mysql-primes `
  -e MYSQL_ROOT_PASSWORD=12345 `
  -e MYSQL_DATABASE=primes `
  -p 3306:3306 `
  mysql:8.0

# Iniciar RabbitMQ
docker run -d --name rabbitmq-primes `
  -p 5672:5672 `
  -p 15672:15672 `
  rabbitmq:3.13-management
```

### Compilar todo (una sola vez)

```powershell
# Desde la raíz del proyecto
mvn clean package -DskipTests
```

---

### Terminal 1: SERVICE-WORKER (Consumer de cola)

```powershell
cd "d:\Estudio\Estudio\Universidad\12 Semestre\DISTRIBUIDOS\CORTE 3\primos\primos\prime-microservices\service-worker"
mvn spring-boot:run
```

**Qué hace:**
- Consume mensajes de la cola `prime.tasks` de RabbitMQ
- Genera números primos usando Miller-Rabin
- Guarda primos en la tabla `primes` de MySQL
- Actualiza progreso en la tabla `prime_requests`

**Log esperado:**
```
Started ServiceWorkerApplication in X.XXX seconds
Listening to queue: prime.tasks
```

**NOTA:** Este servicio NO expone puerto HTTP, solo consume la cola.

---

### Terminal 2: SERVICE-STATS (Puerto 8083)

```powershell
cd "d:\Estudio\Estudio\Universidad\12 Semestre\DISTRIBUIDOS\CORTE 3\primos\primos\prime-microservices\service-stats"
mvn spring-boot:run
```

**Qué hace:**
- Provee endpoints de estadísticas (opcional para el parcial)
- `/stats/count` - Total de primos generados
- `/stats/by-digits` - Primos agrupados por cantidad de dígitos

**Log esperado:**
```
Tomcat started on port 8083
Started ServiceStatsApplication in X.XXX seconds
```

---

### Terminal 3: SERVICE-PRIMES (Puerto 8081)

```powershell
cd "d:\Estudio\Estudio\Universidad\12 Semestre\DISTRIBUIDOS\CORTE 3\primos\primos\prime-microservices\service-primes"
mvn spring-boot:run
```

**Qué hace:**
- **POST /primes/new?cantidad={n}&digitos={d}** - Crea solicitud y encola tarea
- **GET /primes/status/{id}** - Devuelve progreso de generación
- **GET /primes/result/{id}** - Devuelve lista de primos generados
- Ejecuta migraciones Flyway (crea tablas `prime_requests` y `primes`)

**Log esperado:**
```
Flyway: Successfully applied 2 migrations
Tomcat started on port 8081
Started ServicePrimesApplication in X.XXX seconds
```

**Probar directamente (sin gateway):**
```powershell
$r = Invoke-RestMethod -Method Post -Uri "http://localhost:8081/primes/new?cantidad=3&digitos=12"
Invoke-RestMethod -Method Get -Uri "http://localhost:8081/primes/status/$($r.id)"
```

---

### Terminal 4: API-GATEWAY (Puerto 8080)

```powershell
cd "d:\Estudio\Estudio\Universidad\12 Semestre\DISTRIBUIDOS\CORTE 3\primos\primos\prime-microservices\api-gateway"
mvn spring-boot:run
```

**Qué hace:**
- Enruta `/primes/**` → `service-primes:8081`
- Enruta `/stats/**` → `service-stats:8083`
- Punto de entrada único para el sistema

**Log esperado:**
```
Tomcat started on port 8080
Started ApiGatewayApplication in X.XXX seconds
```

**Probar con gateway (recomendado):**
```powershell
$r = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=3&digitos=12"
Start-Sleep -Seconds 15
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/result/$($r.id)"
```

---

## Orden de Inicio (IMPORTANTE)

**Secuencia correcta:**
1. **MySQL** (ya debe estar corriendo)
2. **RabbitMQ** (ya debe estar corriendo)
3. **SERVICE-WORKER** (Terminal 1) - Esperar "Started ServiceWorkerApplication"
4. **SERVICE-STATS** (Terminal 2) - Esperar "Started ServiceStatsApplication"
5. **SERVICE-PRIMES** (Terminal 3) - Esperar "Started ServicePrimesApplication"
6. **API-GATEWAY** (Terminal 4) - Esperar "Started ApiGatewayApplication"

**Tiempo total de arranque:** ~2-3 minutos

---

## Detener Servicios Individuales

En cada terminal, presiona `Ctrl+C` para detener el servicio.

O matar todos los procesos Java:
```powershell
# Ver procesos Java
Get-Process java

# Matar todos los procesos Java (CUIDADO: mata TODOS los Java)
Get-Process java | Stop-Process -Force
```

---

## Comandos de Prueba Rápida

```powershell
# Solicitar 5 primos de 3 dígitos (rápido)
$r1 = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=5&digitos=3"
Start-Sleep -Seconds 5
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/result/$($r1.id)"

# Solicitar 3 primos de 12 dígitos (lento)
$r2 = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=3&digitos=12"
Start-Sleep -Seconds 20
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/result/$($r2.id)"

# Ver estado en tiempo real
$r3 = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=10&digitos=10"
for ($i = 1; $i -le 10; $i++) {
    Start-Sleep -Seconds 2
    $status = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/status/$($r3.id)"
    Write-Host "Generados: $($status.generados)/$($status.cantidad) - Estado: $($status.estado)"
    if ($status.estado -eq 'COMPLETED') { break }
}
```

---

## Verificar RabbitMQ

```powershell
# Abrir UI de RabbitMQ
Start-Process "http://localhost:15672"
# Usuario: guest / Password: guest

# Ver cola vía API
Invoke-RestMethod -Uri "http://localhost:15672/api/queues/%2F/prime.tasks" `
  -Headers @{Authorization = "Basic " + [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("guest:guest"))}
```

---

## Verificar MySQL

```powershell
# Conectar a MySQL (si tienes cliente instalado)
mysql -u root -p12345 primes

# O con Docker
docker exec -it mysql-primes mysql -u root -p12345 primes
```

```sql
-- Ver solicitudes
SELECT * FROM prime_requests ORDER BY created_at DESC LIMIT 5;

-- Ver primos generados
SELECT * FROM primes ORDER BY id DESC LIMIT 10;

-- Contar primos por solicitud
SELECT request_id, COUNT(*) as total FROM primes GROUP BY request_id;
```

---

## Troubleshooting

### "Address already in use" (puerto ocupado)

```powershell
# Ver qué proceso usa el puerto 8080
netstat -ano | findstr :8080

# Matar proceso por PID
taskkill /PID <PID> /F
```

### Worker no consume mensajes

```powershell
# Ver logs del worker
cd service-worker
mvn spring-boot:run

# Verificar conexión a RabbitMQ en los logs:
# "Listening to queue: prime.tasks"
```

### Flyway "checksum mismatch"

```powershell
# Recrear base de datos
mysql -u root -p12345 -e "DROP DATABASE IF EXISTS primes; CREATE DATABASE primes;"

# O con Docker
docker exec -it mysql-primes mysql -u root -p12345 -e "DROP DATABASE IF EXISTS primes; CREATE DATABASE primes;"
```

---

## Resumen de Puertos

| Servicio       | Puerto | URL                          |
|----------------|--------|------------------------------|
| API Gateway    | 8080   | http://localhost:8080        |
| Service Primes | 8081   | http://localhost:8081        |
| Service Stats  | 8083   | http://localhost:8083        |
| MySQL          | 3306   | localhost:3306               |
| RabbitMQ AMQP  | 5672   | localhost:5672               |
| RabbitMQ UI    | 15672  | http://localhost:15672       |

---

**¡Listo para ejecutar!** 🚀
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'D:\Estudio\Estudio\Universidad\12 Semestre\DISTRIBUIDOS\CORTE 3\primos\primos\prime-microservices\service-worker'; mvn spring-boot:run"; Start-Sleep -Seconds 5; Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'D:\Estudio\Estudio\Universidad\12 Semestre\DISTRIBUIDOS\CORTE 3\primos\primos\prime-microservices\service-stats'; mvn spring-boot:run"; Start-Sleep -Seconds 5; Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'D:\Estudio\Estudio\Universidad\12 Semestre\DISTRIBUIDOS\CORTE 3\primos\primos\prime-microservices\api-gateway'; mvn spring-boot:run"



## Comandos:

Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'D:\Estudio\Estudio\Universidad\12 Semestre\DISTRIBUIDOS\CORTE 3\primos\primos\prime-microservices\service-worker'; mvn spring-boot:run";

Start-Sleep -Seconds 5;


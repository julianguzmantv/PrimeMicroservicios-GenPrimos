# Prime Microservices - Generador de Números Primos

Sistema distribuido de microservicios para generación de números primos con arquitectura escalable usando Spring Boot, MySQL, RabbitMQ y Kubernetes.

## 📋 Arquitectura

```
┌─────────────┐
│ API Gateway │ :8080
└──────┬──────┘
       │
       ├──────────────┐
       │              │
 # Prime Microservices — Guía Interactiva (Profesor y Estudiante)
   │ Primes │    │  Stats   │
   │  :8081 │    │   :8083  │
 Sistema distribuido de microservicios para generar números primos a pedido, con MySQL + RabbitMQ + Spring Boot y despliegue local, Docker Compose o Kubernetes (Minikube/Killercoda).
   └───┬────┘    └────┬─────┘
   ┌───▼──────────────▼─┐
   │  x3 réplicas│
   └─────────────┘
```

## 🎯 Microservicios

### **New** - `/primes/new`
Solicita la generación de nuevos números primos.
- **Recibe**: `cantidad` (int), `digitos` (int)
- **Devuelve**: `id` (UUID de la solicitud)

### **Status** - `/primes/status/{id}`
Consulta el estado de una solicitud.
- **Recibe**: `id` (identificador de solicitud)
- **Devuelve**: `cantidad`, `generados`, `estado`, `digitos`


## 🧩 Componentes
- **Cola**: RabbitMQ 3.13 - encola solicitudes de generación
- **Workers**: Pods Kubernetes - procesan tareas de la cola
✅ **Algoritmo Miller-Rabin**: Garantiza 100% primalidad, soporta 12+ dígitos  
✅ **Escalable**: Workers en Kubernetes con réplicas configurables  
✅ **Persistencia**: MySQL con migraciones Flyway automáticas  

## 🚀 Instalación y Despliegue

### Opción 1: Desarrollo Local (Windows PowerShell)

**Requisitos**: Java 17, Maven 3.9+, MySQL 8.0, Docker Desktop

```powershell
# 1. Clonar repositorio
git clone <repository-url>
cd prime-microservices

# 2. Configurar MySQL
# Crear base de datos 'primes' con usuario root/12345

# 3. Iniciar RabbitMQ con Docker
docker compose up -d

# 4. Compilar todos los módulos
mvn clean package -DskipTests

# 5. Iniciar servicios (en ventanas separadas)
# Terminal 1 - Worker
cd service-worker; mvn spring-boot:run

# Terminal 2 - Stats  
cd service-stats; mvn spring-boot:run

cd service-primes; mvn spring-boot:run

# Terminal 4 - Gateway
cd api-gateway; mvn spring-boot:run
```

**Pruebas locales:**
```powershell
# Solicitar generación de 5 primos de 12 dígitos
$response = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=5&digitos=12"
$requestId = $response.id

# Consultar estado
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/status/$requestId"
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/result/$requestId"

### Opción 2: Docker Compose

```bash
# Construir imágenes
docker build -t api-gateway:latest -f api-gateway/Dockerfile .
docker build -t service-primes:latest -f service-primes/Dockerfile .
docker build -t service-worker:latest -f service-worker/Dockerfile .
docker build -t service-stats:latest -f service-stats/Dockerfile .

# Iniciar todos los servicios
docker compose up -d

# Ver logs
docker compose logs -f service-worker

# Detener
docker compose down
```

### Opción 3: Kubernetes (Minikube/Killercoda)

```bash
# 1. Iniciar minikube (si aplica)
minikube start

# 2. Construir imágenes en contexto de Kubernetes
eval $(minikube docker-env)
docker build -t api-gateway:latest -f api-gateway/Dockerfile .
docker build -t service-primes:latest -f service-primes/Dockerfile .
docker build -t service-worker:latest -f service-worker/Dockerfile .
docker build -t service-stats:latest -f service-stats/Dockerfile .

kubectl apply -f k8s/mysql.yaml

# Esperar a que MySQL y RabbitMQ estén listos
kubectl wait --for=condition=ready pod -l app=mysql --timeout=300s
kubectl wait --for=condition=ready pod -l app=rabbitmq --timeout=300s

kubectl apply -f k8s/service-primes.yaml
kubectl apply -f k8s/service-stats.yaml
kubectl apply -f k8s/api-gateway.yaml

# 4. Verificar pods
kubectl get pods
kubectl get services

# 5. Acceder al API Gateway
# En Minikube:

# En Killercoda o NodePort:
# http://<node-ip>:30080
```

**Pruebas en Kubernetes:**
```bash
# Obtener URL del gateway
GATEWAY_URL=$(minikube service api-gateway --url)

REQUEST_ID=$(curl -X POST "$GATEWAY_URL/primes/new?cantidad=3&digitos=15" | jq -r '.id')

curl "$GATEWAY_URL/primes/result/$REQUEST_ID" | jq

**Escalar workers:**
```bash
```

## 📁 Estructura del Proyecto

prime-microservices/
├── pom.xml                     # Parent POM
├── common-lib/                 # DTOs compartidos
│       ├── PrimeTask.java
│       └── PrimeRequestDto.java
├── api-gateway/                # Spring Cloud Gateway
│   ├── Dockerfile
│   └── src/main/...
├── service-primes/             # API REST (New, Status, Result)
│   ├── Dockerfile
│   ├── src/main/java/.../
│   │   ├── controller/PrimeController.java
│   │   ├── model/PrimeRequest.java
│   │   └── repository/PrimeRequestRepository.java
│   └── src/main/resources/db/migration/
│       └── V2__create_prime_requests_table.sql
│   ├── Dockerfile
│   ├── src/main/java/.../
│   │   ├── listener/PrimeTaskListener.java
│   │   ├── service/PrimeService.java
│   │   ├── service/MillerRabin.java
│   │   ├── entity/Prime.java
│   │   └── repository/PrimeRepository.java
│   └── src/main/resources/db/migration/
│       ├── V1__create_primes_table.sql
│       └── V2__add_request_id_to_primes.sql
├── service-stats/              # Estadísticas
│   ├── Dockerfile
│   └── src/main/...
├── k8s/                        # Manifiestos Kubernetes
│   ├── mysql.yaml
│   ├── rabbitmq.yaml
│   ├── api-gateway.yaml
│   ├── service-worker.yaml
```

## 🔧 Configuración

### Variables de entorno (application.yml)

**MySQL:**
- `SPRING_DATASOURCE_URL`: jdbc:mysql://localhost:3306/primes
- `SPRING_DATASOURCE_USERNAME`: root
- `SPRING_DATASOURCE_PASSWORD`: 12345

**RabbitMQ:**
- `SPRING_RABBITMQ_HOST`: localhost
- `SPRING_RABBITMQ_PORT`: 5672

### Puertos

| Servicio       | Puerto |
|----------------|--------|
| API Gateway    | 8080   |
| service-primes | 8081   |
| service-worker | N/A    |
| service-stats  | 8083   |
| MySQL          | 3306   |
| RabbitMQ AMQP  | 5672   |
| RabbitMQ Mgmt  | 15672  |

## 🧪 Testing

```bash
# Ejecutar tests unitarios
mvn test

# Ejecutar tests de integración
mvn verify
```

## 📊 Monitoreo

- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **Kubernetes Dashboard**: `minikube dashboard`

## 🛠️ Troubleshooting

**Error: "Name for argument not specified"**
- Solución: Maven compilador usa flag `-parameters`

**Workers no procesan tareas:**
- Verificar RabbitMQ: `kubectl logs -l app=rabbitmq`
- Verificar cola: RabbitMQ Management UI

**MySQL connection refused:**
- Verificar MySQL está corriendo: `kubectl get pods -l app=mysql`
- Verificar credenciales en ConfigMaps

## 📝 Algoritmo de Primalidad

Implementación de **Miller-Rabin** con 20 rondas:
- Error < 2^-40 (prácticamente 0)
- Soporta números de 12+ dígitos
- Usa `BigInteger` para precisión arbitraria

## 👥 Contribución

1. Fork el proyecto
2. Crear feature branch
3. Commit cambios
4. Push al branch
5. Crear Pull Request

## 📄 Licencia

MIT License

## 📧 Contacto

Unillanos - Colombia - Sistemas Distribuidos

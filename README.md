# Prime Microservices - Generador de Números Primos 🚀


https://img.shields.io/badge/Spring%2520Boot-3.2-green
https://img.shields.io/badge/Kubernetes-1.29-blue
https://img.shields.io/badge/RabbitMQ-3.13-orange
https://img.shields.io/badge/MySQL-8.0-blue
https://img.shields.io/badge/License-MIT-yellow

# 📖 Tabla de Contenidos:

🎯 ¿Qué es este proyecto?

🏗️ Arquitectura del Sistema

🛠️ Requisitos Previos

🚀 ¡Empezar Rápido! (Recomendado)

🔧 Opciones de Despliegue

🐳 Docker Compose

💻 Desarrollo Local

☸️ Kubernetes

📡 Uso de la API

📁 Estructura del Proyecto

🔍 Monitoreo

❓ Preguntas Frecuentes

📝 Algoritmo Miller-Rabin

🤝 Contribución



# 🎯 ¿Qué es este proyecto?
Sistema distribuido de microservicios para generación de números primos con arquitectura escalable, diseñado para:

Generar números primos de cualquier tamaño (soporta 12+ dígitos)

Procesamiento asíncrono usando colas de mensajes (RabbitMQ)

Escalabilidad automática con réplicas en Kubernetes

Persistencia confiable en MySQL con migraciones automáticas

Monitoreo en tiempo real de estadísticas y progreso


# Características principales:

✅ 100% Confiabilidad - Algoritmo Miller-Rabin con probabilidad de error < 2⁻⁴⁰

✅ Escalabilidad Horizontal - Workers independientes que se pueden replicar

✅ Arquitectura Resiliente - Tolerante a fallos con colas persistentes

✅ Multi-Entorno - Funciona localmente, con Docker y en Kubernetes



# 🏗️ Arquitectura del Sistema

<img width="2916" height="2647" alt="deepseek_mermaid_20251203_2d6115" src="https://github.com/user-attachments/assets/6dd2c36d-d032-4dfb-8ed8-eb47bbe242a3" />


# Flujo de datos:

Solicitud → Cliente envía petición al Gateway

Registro → service-primes guarda la solicitud en BD

Encolamiento → Tarea se publica en RabbitMQ

Procesamiento → Workers disponibles consumen y procesan

Almacenamiento → Resultados se guardan en MySQL

Consulta → Cliente puede consultar estado y resultados


# 🛠️ Requisitos Previos

📋 Requisitos Mínimos:

Docker Desktop instalado y corriendo

4 GB de RAM disponibles (para contenedores)

Conocimientos básicos de línea de comandos

 # Puertos Necesarios:

 <img width="436" height="173" alt="image" src="https://github.com/user-attachments/assets/afe6809c-151f-4639-801e-84333b1a9c77" />


# 🚀 ¡Empezar Rápido! (Recomendado)

Si quieres ver el sistema funcionando en 5 minutos, sigue estos pasos:

## Paso 1: Preparación

### 1. Clonar el repositorio
git clone <URL_DEL_REPOSITORIO>
cd prime-microservices

### 2. Verificar que Docker está corriendo
docker --version
docker ps

## Paso 2: Construir imágenes Docker
powershell

## Construir todas las imágenes (ejecutar en PowerShell)
docker build -t api-gateway:latest -f api-gateway/Dockerfile .
docker build -t service-primes:latest -f service-primes/Dockerfile .
docker build -t service-worker:latest -f service-worker/Dockerfile .
docker build -t service-stats:latest -f service-stats/Dockerfile .
## Paso 3: Iniciar el sistema

## Iniciar todos los servicios con Docker Compose
docker compose up -d

## Verificar que todos los contenedores estén corriendo
docker compose ps
## Paso 4: Probar el sistema
powershell
### 1. Solicitar generación de 3 números primos de 12 dígitos
$response = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/primes/new?cantidad=3&digitos=12"
$requestId = $response.id
Write-Host "ID de solicitud: $requestId"

### 2. Consultar estado (esperar unos segundos)
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/status/$requestId"

### 3. Obtener resultados
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/primes/result/$requestId"
🎉 ¡Listo! El sistema está funcionando correctamente.

# 🔧 Opciones de Despliegue
🐳 Docker Compose (Recomendado para pruebas)
Ideal para desarrollo y demostraciones rápidas.


## Comandos útiles
docker compose up -d           # Iniciar
docker compose logs -f         # Ver logs en tiempo real
docker compose ps              # Listar contenedores
docker compose down           # Detener y eliminar
docker compose restart worker # Reiniciar solo workers
## Ventajas:

Configuración simple en un solo archivo

Aislamiento completo del entorno

Fácil de limpiar y reiniciar

💻 Desarrollo Local (Para modificar código)
Para desarrollo y debugging del código fuente.

# powershell
## 1. Instalar dependencias
 - Java 17 o superior
 - Maven 3.9+
 - MySQL 8.0 local

## 2. Configurar base de datos
 Crear base de datos 'primes'
 Usuario: root, Contraseña: *Contraseña por defecto de root*

## 3. Iniciar RabbitMQ
docker compose up rabbitmq -d

## 4. Compilar proyecto
mvn clean package -DskipTests

## 5. Ejecutar servicios (en terminales separadas)
### Terminal 1 - Worker
cd service-worker; mvn spring-boot:run

### Terminal 2 - Stats
cd service-stats; mvn spring-boot:run

### Terminal 3 - Primes
cd service-primes; mvn spring-boot:run

### Terminal 4 - Gateway
cd api-gateway; mvn spring-boot:run
☸️ Kubernetes (Producción/Escalabilidad)
Para entornos de producción o pruebas de escalabilidad.


### 1. Iniciar cluster (usando Minikube)
minikube start --memory=4096 --cpus=2

### 2. Configurar entorno Docker
eval $(minikube docker-env)

### 3. Construir imágenes dentro del cluster
docker build -t api-gateway:latest -f api-gateway/Dockerfile .
docker build -t service-primes:latest -f service-primes/Dockerfile .
docker build -t service-worker:latest -f service-worker/Dockerfile .
docker build -t service-stats:latest -f service-stats/Dockerfile .

### 4. Desplegar servicios base
kubectl apply -f k8s/mysql.yaml
kubectl apply -f k8s/rabbitmq.yaml

### 5. Esperar que estén listos
kubectl wait --for=condition=ready pod -l app=mysql --timeout=300s
kubectl wait --for=condition=ready pod -l app=rabbitmq --timeout=300s

### 6. Desplegar microservicios
kubectl apply -f k8s/service-primes.yaml
kubectl apply -f k8s/service-stats.yaml
kubectl apply -f k8s/api-gateway.yaml

### 7. Verificar despliegue
kubectl get pods
kubectl get services

### 8. Acceder al gateway
minikube service api-gateway --url
Escalar workers dinámicamente:


## Aumentar a 5 workers
kubectl scale deployment service-worker --replicas=5

## Reducir a 2 workers
kubectl scale deployment service-worker --replicas=2
📡 Uso de la API
Endpoints Disponibles
Método	Endpoint	Descripción	Parámetros
POST	/primes/new	Solicitar generación de primos	cantidad, digitos
GET	/primes/status/{id}	Consultar estado de solicitud	id (UUID)
GET	/primes/result/{id}	Obtener resultados generados	id (UUID)
GET	/stats/summary	Estadísticas generales	-
GET	/stats/top-requests	Top 10 solicitudes más grandes	-

# Ejemplos de Uso

## 1. Generar 5 primos de 15 dígitos
curl -X POST "http://localhost:8080/primes/new?cantidad=5&digitos=15"

### Respuesta: {"id": "a1b2c3d4-e5f6-7890-1234-567890abcdef"}

## 2. Consultar estado
curl "http://localhost:8080/primes/status/a1b2c3d4-e5f6-7890-1234-567890abcdef"

### Respuesta: {"id": "...", "cantidad": 5, "generados": 3, "estado": "PROCESANDO", "digitos": 15}

## 3. Obtener resultados
curl "http://localhost:8080/primes/result/a1b2c3d4-e5f6-7890-1234-567890abcdef"

### Respuesta: {"id": "...", "primos": [100000000000031, 100000000000067, ...], "totalGenerados": 5}

## 4. Ver estadísticas
curl "http://localhost:8080/stats/summary"

### Respuesta: {"totalSolicitudes": 42, "totalPrimosGenerados": 150, "promedioDigitos": 10.5}

# Ejemplo en Python

import requests

### Solicitar generación
response = requests.post(
    "http://localhost:8080/primes/new",
    params={"cantidad": 3, "digitos": 10}
)
request_id = response.json()["id"]

### Consultar periódicamente
import time
while True:
    status = requests.get(f"http://localhost:8080/primes/status/{request_id}").json()
    if status["estado"] == "COMPLETADO":
        break
    time.sleep(1)

### Obtener resultados
results = requests.get(f"http://localhost:8080/primes/result/{request_id}").json()
print(f"Primos generados: {results['primos']}")


# 📁 Estructura del Proyecto

### prime-microservices/
### ├── 📂 api-gateway/              # Punto de entrada único
### │   ├── Dockerfile              # Configuración de contenedor
### │   ├── src/main/java/...       # Código fuente Spring Cloud Gateway
### │   └── application.yml         # Configuración de rutas
### │
### ├── 📂 service-primes/           # API de gestión de primos
### │   ├── Dockerfile
### │   ├── src/main/java/com/primes/
### │   │   ├── controller/PrimeController.java    # REST endpoints
### │   │   ├── model/PrimeRequest.java           # Entidad JPA
### │   │   ├── repository/PrimeRequestRepository.java
### │   │   └── service/PrimeService.java
### │   └── src/main/resources/
### │       └── db/migration/       # Flyway migrations
### │           └── V2__create_prime_requests_table.sql
### │
### ├── 📂 service-worker/           # Procesador de tareas
### │   ├── Dockerfile
### │   ├── src/main/java/com/worker/
### │   │   ├── listener/PrimeTaskListener.java   # Consumidor RabbitMQ
### │   │   ├── service/PrimeService.java         # Lógica de generación
### │   │   ├── service/MillerRabin.java          # Algoritmo de primalidad
### │   │   ├── entity/Prime.java                 # Entidad resultado
### │   │   └── repository/PrimeRepository.java
### │   └── src/main/resources/db/migration/
### │       ├── V1__create_primes_table.sql
### │       └── V2__add_request_id_to_primes.sql
### │
### ├── 📂 service-stats/            # Estadísticas
### │   ├── Dockerfile
### │   └── src/main/java/com/stats/
### │       ├── controller/StatsController.java
### │       └── service/StatsService.java
### │
### ├── 📂 common-lib/               # DTOs compartidos
### │   ├── PrimeTask.java          # Mensaje para RabbitMQ
### │   └── PrimeRequestDto.java    # DTO de solicitud
### │
### ├── 📂 k8s/                      # Manifiestos Kubernetes
### │   ├── mysql.yaml              # Despliegue MySQL
### │   ├── rabbitmq.yaml           # Despliegue RabbitMQ
### │   ├── api-gateway.yaml        # Gateway como LoadBalancer
### │   ├── service-primes.yaml     # Deployment + Service
### │   ├── service-worker.yaml     # Workers escalables
### │   └── service-stats.yaml      # Stats service
### │
### ├── 📂 docker/                   # Configuraciones Docker
### │   └── mysql-init/             # Scripts inicialización BD
### │
### ├── 📜 docker-compose.yml       # Orquestación completa
### ├── 📜 pom.xml                  # Parent POM Maven
### ├── 📜 README.md                # Este archivo
### └── 📜 GUIA_EJECUCION.md        # Guía paso a paso detallada

# 🔍 Monitoreo

## 1. RabbitMQ Management Console

### Acceder a la consola de RabbitMQ
### URL: http://localhost:15672
### Usuario: guest
### Contraseña: guest

# Qué puedes monitorear?:

### 📊 Colas activas - Número de tareas pendientes

###  Throughput - Mensajes por segundo

###  🧩 Consumers conectados - Workers activos

###  💾 Uso de memoria - Estado del broker

# 2. Kubernetes Dashboard

## Si estás usando Minikube
minikube dashboard

## Ver logs de pods específicos
kubectl logs -l app=service-worker --tail=50
kubectl logs -l app=api-gateway --follow
# 3. Métricas del Sistema

## Ver uso de recursos
"docker stats" o "kubectl top pods"

## Ver logs combinados
docker compose logs --tail=100 --follow


# ❓ Preguntas Frecuentes
## ¿Por qué mi solicitud tarda mucho?
### Primos grandes (>15 dígitos) requieren más cómputo

### Verificar workers: docker compose ps | grep worker

### Revisar cola: RabbitMQ Management en puerto 15672

### Error: "Cannot connect to MySQL"

# Verificar MySQL está corriendo
docker compose ps | grep mysql

## Revisar logs de MySQL
docker compose logs mysql

## Si usas Kubernetes:
kubectl get pods -l app=mysql
kubectl logs deployment/mysql
Error: "Name for argument not specified"
xml
<!-- En pom.xml del módulo afectado, agregar: -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <parameters>true</parameters>
    </configuration>
</plugin>


##¿Cómo reiniciar un servicio específico?

### Docker Compose
docker compose restart service-worker

### Kubernetes
kubectl rollout restart deployment/service-worker
Los workers no procesan tareas
Verificar RabbitMQ:


docker compose exec rabbitmq rabbitmqctl list_queues
Verificar conexión de workers:


docker compose logs service-worker | grep "Connected"
Reiniciar workers:


docker compose restart service-worker


# 📝 Algoritmo Miller-Rabin
## Implementación
java
public boolean isPrime(BigInteger n, int certainty) {
    // Implementación probabilística
    // 20 iteraciones = error < 2⁻⁴⁰ ≈ 0.0000000000009%
    // Soporta números de tamaño arbitrario
}
## Características
Tipo: Probabilístico (puede configurarse como determinístico)

Iteraciones: 20 por defecto (ajustable)

Precisión: Probabilidad de error < 9.09×10⁻¹³

Rendimiento: O(k log³ n) donde k es número de iteraciones

Límite: Soporta números de hasta 1000 dígitos

## Ventajas sobre prueba por división
Método	Complejidad	Para 20 dígitos	Precisión
División tradicional	O(√n)	~10¹⁰ operaciones	100%
Miller-Rabin (20 iter)	O(k log³ n)	~4000 operaciones	99.9999999999%


# 🤝 Contribución
## ¿Encontraste un bug o tienes una mejora?
Fork el repositorio

## Crea una rama para tu feature

git checkout -b feature/nueva-funcionalidad

## Haz commit de tus cambios

git commit -m "feat: añade nueva funcionalidad"

## Push a la rama

git push origin feature/nueva-funcionalidad

## Abre un Pull Request

## Guía de estilos
Commits: Usar Conventional Commits

Código: Seguir convenciones de Spring Boot

Documentación: Mantener README actualizado

# 📄 Licencia
Este proyecto está bajo la Licencia MIT. Ver archivo LICENSE para más detalles.

## MIT License © 2024 - Universidad de los Llanos
## Sistemas Distribuidos - Ingeniería de Sistemas
## 📧 Contacto y Soporte
Universidad de los Llanos
Facultad de Ingeniería de Sistemas
Curso: Sistemas Distribuidos

## Estudiante: Julian Esteban Romero Guzmán
## Profesor: Juan Fajardo Barrero

# Recursos adicionales:

### 📚 Documentación Spring Boot

### 🐳 Documentación Docker

### ☸️ Documentación Kubernetes

### 🐰 Documentación RabbitMQ


# 🎓 Proyecto Académico - Sistemas Distribuidos
### "De la teoría a la práctica: Microservicios en acción"

## ⭐ ¿Te gustó este proyecto? ¡Dale una estrella al repositorio!


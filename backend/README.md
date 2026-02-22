# Tenpo Challenge — API Backend

## 1. Descripción general del componente

### Qué problema resuelve

Este componente backend implementa una **calculadora con porcentaje dinámico** que:

- Suma dos números y aplica un porcentaje configurable sobre el resultado
- Obtiene el porcentaje desde un servicio externo (actualmente simulado) con caché en Redis
- Registra un historial de todas las operaciones realizadas
- Ofrece resiliencia ante fallos del servicio externo mediante caché de respaldo (backup)

### Contexto dentro de la arquitectura

Se trata de una **API REST** que actúa como **microservicio independiente** dentro de un ecosistema distribuido. Expone endpoints para cálculo y consulta de historial, integra PostgreSQL para persistencia, Redis para caché y procesamiento asíncrono para el registro de llamadas.

---

## 2. Tecnologías utilizadas

| Tecnología        | Versión  | Uso                                      |
|-------------------|----------|------------------------------------------|
| **Java**          | 21       | Lenguaje principal                       |
| **Spring Boot**   | 3.5.10   | Framework de aplicación                  |
| **Spring Data JPA** | -      | Persistencia y acceso a datos            |
| **Spring Data Redis** | -     | Caché distribuido                        |
| **PostgreSQL**    | 15       | Base de datos relacional                 |
| **Redis**         | 7.2      | Caché de porcentaje y backup             |
| **SpringDoc OpenAPI** | 2.3.0 | Documentación Swagger/OpenAPI 3          |
| **Lombok**        | 1.18.30  | Reducción de código boilerplate          |
| **Docker**        | -        | Contenedorización y orquestación         |

---

## 3. Arquitectura y estructura del proyecto

```
src/main/java/com/tenpo/challenge/
├── ChallengeApplication.java     # Punto de entrada
├── config/                       # Configuración de infraestructura
│   ├── AsyncConfig.java          # Pool de hilos para tareas asíncronas
│   ├── RedisConfig.java          # RedisTemplate y CacheManager
│   └── SwaggerConfig.java        # OpenAPI / Swagger UI
├── controller/                   # Capa de presentación REST
│   ├── CalculateController.java  # Endpoint de cálculo
│   └── CallController.java       # Endpoint de historial de llamadas
├── dto/                          # Objetos de transferencia
│   ├── CalculateResponseDTO.java
│   ├── CallDTO.java
│   ├── ErrorResponseDTO.java
│   └── mapper/
│       └── CallMapper.java       # Mapeo entre entidad y DTO
├── exception/                    # Manejo de errores
│   ├── GlobalExceptionHandler.java   # @ControllerAdvice
│   └── PercentageUnavailableException.java
├── model/                        # Entidades JPA
│   └── Call.java
├── repository/                   # Capa de acceso a datos
│   └── CallRepository.java
└── service/                      # Lógica de negocio
    ├── CalculatorService.java
    ├── CallService.java
    ├── PercentageService.java
    └── impl/                     # Implementaciones
        ├── CalculatorServiceImpl.java
        ├── CallServiceImpl.java
        └── PercentageServiceImpl.java
```

### Descripción de paquetes

| Paquete      | Responsabilidad                                                                 |
|--------------|-----------------------------------------------------------------------------------|
| `controller` | Expone endpoints REST y delega en servicios                                       |
| `service`    | Lógica de negocio, orquestación y reglas de cálculo                               |
| `repository` | Acceso a datos con Spring Data JPA                                                |
| `model`      | Entidades JPA mapeadas a tablas de PostgreSQL                                    |
| `dto`        | Objetos para request/response y mapeo con entidades                               |
| `config`     | Beans de configuración: Redis, async, Swagger                                     |
| `exception`  | Excepciones personalizadas y manejador global (@ControllerAdvice)                 |

---

## 4. Endpoints expuestos

| Método | Endpoint                                  | Descripción                                      |
|--------|-------------------------------------------|--------------------------------------------------|
| POST   | `/tenpo/api/calculator/calculate/{num1}/{num2}` | Suma num1 + num2, aplica porcentaje y retorna resultado |
| GET    | `/tenpo/api/calls`                        | Lista el historial de todas las llamadas         |

### Ejemplos de uso

#### POST — Calcular con porcentaje

**Request:**
```http
POST /tenpo/api/calculator/calculate/10.5/20.3 HTTP/1.1
Host: localhost:8080
```

**Response 200 OK:**
```json
{
  "result": 33.884
}
```

> Cálculo: (10.5 + 20.3) × (1 + porcentaje/100). El porcentaje proviene del servicio externo o de caché Redis.

---

#### GET — Historial de llamadas

**Request:**
```http
GET /tenpo/api/calls HTTP/1.1
Host: localhost:8080
```

**Response 200 OK:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "timestamp": "2025-02-22T10:30:00",
    "endpoint": "/api/calculate",
    "params": {
      "num1": "10.5",
      "num2": "20.3"
    },
    "response": "33.884",
    "success": true
  }
]
```

---

### Documentación Swagger

Con la aplicación en ejecución, la documentación interactiva está disponible en:

```
http://localhost:8080/swagger-ui.html
```

---

## 5. Configuración

### application.properties

```properties
spring.application.name=challenge
spring.data.redis.host=redis-server
spring.data.redis.port=6379

app.percentage.cache-ttl=1800
app.external.percentage=10.5

spring.task.execution.pool.core-size=2
spring.task.execution.pool.max-size=5
spring.task.execution.pool.queue-capacity=100

spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://postgres-tenpo:5433/tenpo
spring.datasource.username=admin
spring.datasource.password=admin123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### Variables de entorno

| Variable                    | Descripción                          | Valor por defecto / Ejemplo     |
|----------------------------|--------------------------------------|---------------------------------|
| `SPRING_DATASOURCE_URL`    | URL de conexión a PostgreSQL         | `jdbc:postgresql://postgres:5432/tenpo` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de base de datos           | `admin`                         |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de base de datos        | `admin123`                      |
| `SPRING_DATA_REDIS_HOST`   | Host de Redis                        | `redis` / `redis-server`        |
| `SPRING_DATA_REDIS_PORT`   | Puerto de Redis                      | `6379`                          |
| `app.percentage.cache-ttl` | TTL del caché de porcentaje (segundos) | `1800`                       |
| `app.external.percentage`  | Porcentaje mock para servicio externo | `10.5`                       |

---

## 6. Cómo ejecutar el proyecto

### Requisitos previos

- **JDK 21**
- **Maven 3.9+**
- **PostgreSQL 15** (o contenedor)
- **Redis 7.x** (o contenedor)

### Ejecución local con Maven

1. Iniciar PostgreSQL y Redis (por ejemplo, con Docker):

```bash
docker run -d --name postgres-tenpo -e POSTGRES_DB=tenpo -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin123 -p 5433:5432 postgres:15
docker run -d --name redis-server -p 6379:6379 redis:7.2-alpine
```

2. Ajustar `application.properties` si la URL de PostgreSQL o Redis difiere de la configuración por defecto.

3. Compilar y ejecutar:

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

La API quedará disponible en `http://localhost:8080`.

---

### Ejecución con Docker

Si existe un `docker-compose.yml` en la raíz del repositorio que incluye PostgreSQL, Redis y la aplicación:

```bash
# Desde la raíz del proyecto (tempo-challenge)
docker-compose up -d
```

> **Nota:** Si `docker-compose` está en la raíz del monorepo, el servicio `app` debería usar `context: ./backend` para construir la imagen desde este directorio.

#### Construcción manual de la imagen

```bash
cd backend
docker build -t tenpo-challenge:latest .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/tenpo \
  -e SPRING_DATA_REDIS_HOST=host.docker.internal \
  tenpo-challenge:latest
```

---

## 7. Pruebas

### Ejecutar tests unitarios e integración

```bash
cd backend
mvn test
```

### Test de carga de contexto

El proyecto incluye un test básico que valida el arranque del contexto de Spring:

```java
@SpringBootTest
class ChallengeApplicationTests {
    @Test
    void contextLoads() {}
}
```

---

## 8. Manejo de errores

### Estrategia

Se utiliza un **manejador global de excepciones** con `@ControllerAdvice` que centraliza el tratamiento de errores y devuelve respuestas estructuradas en JSON.

### Excepciones manejadas

| Excepción                       | HTTP Status         | Situación                                          |
|---------------------------------|---------------------|----------------------------------------------------|
| `PercentageUnavailableException` | 503 Service Unavailable | Servicio externo caído y sin backup en Redis   |
| `MethodArgumentTypeMismatchException` | 400 Bad Request | Parámetros de ruta no numéricos              |
| `Exception` (genérica)          | 500 Internal Server Error | Errores no contemplados                    |

### Formato de error

```json
{
  "timestamp": "2025-02-22T10:30:00",
  "status": "503",
  "error": "Service Unavailable",
  "message": "El servicio externo no está disponible y no existe un porcentaje en caché. Reintente más tarde."
}
```

### Excepción personalizada

```java
public class PercentageUnavailableException extends RuntimeException {
    public PercentageUnavailableException(String message) { super(message); }
    public PercentageUnavailableException(String message, Throwable cause) { super(message, cause); }
}
```

---

## 9. Buenas prácticas implementadas

| Práctica          | Implementación                                                                 |
|-------------------|---------------------------------------------------------------------------------|
| **DTOs**          | Uso de DTOs para request/response (`CalculateResponseDTO`, `CallDTO`, `ErrorResponseDTO`) |
| **Separación de capas** | Controller → Service → Repository con interfaces bien definidas         |
| **Logging**       | `@Slf4j` en servicios y GlobalExceptionHandler para trazabilidad               |
| **Caché**         | Redis para porcentaje externo con TTL configurable y estrategia de fallback     |
| **Procesamiento asíncrono** | `@Async` para guardar historial sin bloquear la respuesta                 |
| **Documentación API** | SpringDoc OpenAPI con Swagger UI                                          |
| **Mapeo**         | Clase `CallMapper` para conversión entre entidad y DTO                         |
| **Inyección por constructor** | `@RequiredArgsConstructor` (Lombok) para dependencias                     |
| **JPA**           | Entidad `Call` con `@JdbcTypeCode(SqlTypes.JSON)` para columna JSONB           |

---

## 10. Autor / Equipo

- **Proyecto:** Tenpo Challenge
- **Descripción:** Challenge for tempo

---

## Licencia

Ver archivo `LICENSE` en el repositorio.

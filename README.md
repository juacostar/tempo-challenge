# Tenpo Challenge

Calculadora con porcentaje dinámico que suma dos números, aplica un porcentaje (obtenido de un servicio externo con caché en Redis) y mantiene un historial de operaciones.

---

## Índice

- [Arquitectura general](#arquitectura-general)
- [Tecnologías](#tecnologías)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Componentes por capa](#componentes-por-capa)
- [Cómo ejecutar](#cómo-ejecutar)
- [Endpoints API](#endpoints-api)

---

## Arquitectura general

```
┌─────────────────┐      HTTP       ┌──────────────────────────────────────────┐
│                 │  ─────────────► │              Backend (Spring Boot)       │
│   Frontend      │                 │  ┌────────────┐  ┌────────────┐          │
│   (React+Vite)  │  ◄───────────── │  │ Controllers│──│  Services  │          │
│   Nginx:3000    │     JSON        │  └────────────┘  └─────┬──────┘          │
│                 │                 │         │               │                 │
└─────────────────┘                 │         │               │                 │
                                    │         ▼               ▼                 │
                                    │  ┌────────────┐  ┌────────────┐          │
                                    │  │ PostgreSQL │  │   Redis    │          │
                                    │  │ (calls)    │  │ (caché %)  │          │
                                    │  └────────────┘  └────────────┘          │
                                    └──────────────────────────────────────────┘
```

- **Frontend**: SPA en React servida por Nginx. Llama al backend para calcular y consultar historial.
- **Backend**: API REST que orquesta el cálculo, obtiene el porcentaje (externo/Redis), persiste en PostgreSQL y cachea en Redis.
- **PostgreSQL**: Persiste el historial de llamadas (calls).
- **Redis**: Caché del porcentaje externo con TTL y valor de respaldo ante fallos.

---

## Tecnologías

| Componente | Tecnología | Versión | Uso |
|------------|------------|---------|-----|
| **Backend** | Java | 21 | Lenguaje |
| | Spring Boot | 3.5.10 | Framework web y DI |
| | Spring Data JPA | - | Persistencia (PostgreSQL) |
| | Spring Data Redis | - | Caché |
| | PostgreSQL | 15 | Base de datos |
| | Redis | 7.2 | Caché de porcentaje |
| | SpringDoc OpenAPI | 2.3.0 | Swagger UI |
| | Lombok | 1.18.30 | Reducción de boilerplate |
| **Frontend** | React | 19.x | UI |
| | Vite | 7.x | Build y dev server |
| | Nginx (Alpine) | - | Servir estáticos en producción |
| **Infra** | Docker / Docker Compose | - | Contenedores y orquestación |

---

## Estructura del proyecto

```
tempo-challenge/
├── README.md                 # Este archivo
├── docker-compose.yml        # Orquestación: postgres, redis, app, frontend
│
├── backend/                  # API Spring Boot
│   ├── Dockerfile
│   ├── pom.xml
│   ├── README.md             # Documentación detallada del backend
│   └── src/
│       ├── main/java/com/tenpo/challenge/
│       │   ├── ChallengeApplication.java
│       │   ├── config/       # CORS, Async, Redis, Swagger
│       │   ├── controller/   # CalculateController, CallController
│       │   ├── dto/          # DTOs y mappers
│       │   ├── exception/    # GlobalExceptionHandler
│       │   ├── model/        # Entidad Call
│       │   ├── repository/   # CallRepository
│       │   └── service/      # Lógica de negocio
│       └── main/resources/
│           └── application.properties
│
└── frontend/
    └── tenpo-frontend/       # SPA React
        ├── Dockerfile
        ├── package.json
        ├── vite.config.js
        └── src/
            ├── main.jsx
            ├── App.jsx
            ├── context/      # RouterContext
            ├── pages/        # Home, Calculator, History
            ├── components/   # UI y layout
            ├── hooks/        # useCalculatorApi, useHistoryApi
            └── services/     # calculatorService, historyService, httpclient
```

---

## Componentes por capa

### Backend (archivos principales)

| Archivo | Responsabilidad |
|---------|-----------------|
| `ChallengeApplication.java` | Punto de entrada de Spring Boot |
| **config/** | |
| `AsyncConfig.java` | Pool de hilos para `@Async` (guardado de calls) |
| `CorsConfig.java` | CORS para frontend (localhost:3000, 5173) |
| `RedisConfig.java` | Configuración de `RedisTemplate` |
| `SwaggerConfig.java` | OpenAPI / Swagger UI |
| **controller/** | |
| `CalculateController.java` | `POST /tenpo/api/calculator/calculate/{num1}/{num2}` |
| `CallController.java` | `GET /tenpo/api/calls` |
| **service/** | |
| `CalculatorServiceImpl.java` | Suma, aplica porcentaje, persiste call |
| `PercentageServiceImpl.java` | Obtiene % de Redis o servicio externo, fallback en backup |
| `CallServiceImpl.java` | Persistencia async de calls en PostgreSQL |
| **model/** | |
| `Call.java` | Entidad JPA (endpoint, params, response, success) |
| **exception/** | |
| `GlobalExceptionHandler.java` | Mapeo de excepciones a HTTP (400, 503, 500) |
| `PercentageUnavailableException.java` | Cuando no hay % ni en caché ni backup |

### Frontend (archivos principales)

| Archivo | Responsabilidad |
|---------|-----------------|
| `main.jsx` | Monta la app y providers |
| `App.jsx` | Router simple y páginas |
| **context/** | |
| `RouterContext.tsx` | Navegación sin React Router (Home, Calculator, History) |
| **pages/** | |
| `Home.jsx` | Página inicial |
| `Calculator.jsx` | Formulario de cálculo (num1, num2, resultado) |
| `History.jsx` | Lista de llamadas históricas |
| **services/** | |
| `httpclient.js` | Cliente HTTP con `VITE_API_BASE_URL` |
| `calculatorService.js` | `POST /calculator/calculate/{a}/{b}` |
| `historyService.js` | `GET /calls` |
| **hooks/** | |
| `useCalculatorApi.js` | Lógica de cálculo y estado |
| `useHistoryApi.js` | Lógica de historial |

---

## Cómo ejecutar

### Opción 1: Todo con Docker Compose (recomendado)

```bash
# Desde la raíz del proyecto
docker compose build
docker compose up
```

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **PostgreSQL**: puerto 5433 (localhost)
- **Redis**: puerto 6379 (localhost)

### Opción 2: Backend local, frontend con Vite

**1. Levantar PostgreSQL y Redis:**

```bash
docker run -d --name postgres-tenpo -e POSTGRES_DB=tenpo -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin123 -p 5433:5432 postgres:15
docker run -d --name redis-server -p 6379:6379 redis:7.2-alpine
```

**2. Backend:**

```bash
cd backend
mvn spring-boot:run
```

API en http://localhost:8080.

**3. Frontend:**

```bash
cd frontend/tenpo-frontend
npm install
npm run dev
```

App en http://localhost:5173. Usa `VITE_API_BASE_URL` de `.env` o el valor por defecto `http://localhost:8080/tenpo/api`.

### Opción 3: Solo backend en Docker

```bash
cd backend
docker build -t tenpo-backend .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/tenpo \
  -e SPRING_DATA_REDIS_HOST=host.docker.internal \
  tenpo-backend
```

### Opción 4: Solo frontend (dev)

```bash
cd frontend/tenpo-frontend
echo "VITE_API_BASE_URL=http://localhost:8080/tenpo/api" > .env
npm install
npm run dev
```

---

## Endpoints API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/tenpo/api/calculator/calculate/{num1}/{num2}` | Suma num1+num2, aplica porcentaje y devuelve el resultado |
| GET | `/tenpo/api/calls` | Devuelve el historial de llamadas |

Base URL del backend: `http://localhost:8080` (o el host configurado).

### Ejemplo POST

```http
POST /tenpo/api/calculator/calculate/10/20 HTTP/1.1
Host: localhost:8080
```

Respuesta:

```json
{ "result": 33.0 }
```

(Asume 10% de porcentaje: (10+20) × 1.10 = 33.)

### Ejemplo GET

```http
GET /tenpo/api/calls HTTP/1.1
Host: localhost:8080
```

Respuesta:

```json
[
  {
    "id": "...",
    "timestamp": "2026-02-23T10:00:00",
    "endpoint": "/api/calculate",
    "params": { "num1": "10", "num2": "20" },
    "response": "33.0",
    "success": true
  }
]
```

---

## Variables de entorno relevantes

| Variable | Componente | Descripción |
|----------|------------|-------------|
| `VITE_API_BASE_URL` | Frontend | URL base del API (build time). Ej: `http://localhost:8080/tenpo/api` |
| `SPRING_DATASOURCE_URL` | Backend | JDBC URL de PostgreSQL |
| `SPRING_DATA_REDIS_HOST` | Backend | Host de Redis |
| `app.percentage.cache-ttl` | Backend | TTL del caché de porcentaje en segundos (default: 1800) |
| `app.external.percentage` | Backend | Porcentaje mock cuando el servicio externo está simulado (default: 10.5) |

---

## Documentación adicional

- **Backend**: Ver [backend/README.md](backend/README.md) para arquitectura interna, configuración y pruebas.
- **Swagger**: http://localhost:8080/swagger-ui.html con la API en ejecución.

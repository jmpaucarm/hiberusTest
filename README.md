# Payment Initiation Service

Microservicio **REST** para **Payment Initiation** siguiendo el marco **BIAN** ([Banking Industry Architecture Network](https://bian.org/)), **migrado desde un servicio SOAP legado** en sentido conceptual: el contrato OpenAPI modela las mismas capacidades de negocio (envío y consulta de órdenes) con **HTTP**, **JSON** y **OpenAPI** en lugar de XML/WSDL.

Implementación **contract-first** en **Java 17** y **Spring Boot 3** (`spring-boot-starter-web`), con persistencia **en memoria** para la entrega.

**Contenido del README:** [vista rápida](#vista-rápida) · [URLs](#urls-de-la-aplicación-local) · [ejemplos de uso](#ejemplos-de-uso) · [contexto](#1-contexto-del-problema) · [arquitectura](#2-decisiones-de-arquitectura) · [migración](#3-contexto-y-decisiones-de-migración-desde-la-etapa-inicial) · [BIAN](#4-alineación-con-bian-payment-initiation--payment-order) · [estructura](#5-estructura-del-proyecto) · [ejecución local](#6-ejecución-local) · [Docker](#7-ejecución-con-docker) · [tests](#8-cómo-correr-tests) · [verificar calidad](#9-verificar-calidad-de-código) · [cobertura](#10-cobertura-y-calidad)

---

## Vista rápida

### Contexto del proyecto

Este proyecto representa la **migración** de un flujo de **órdenes de pago** expuesto históricamente como **SOAP** hacia un **microservicio REST** moderno, alineado con estándares bancarios de referencia (**BIAN**) y con errores HTTP homogéneos (**RFC 7807**).

| Concepto BIAN | Valor en esta solución |
|---------------|-------------------------|
| **Service Domain** | **Payment Initiation** — captura y validación de la instrucción de pago. |
| **Business object / recurso** | **Payment Order** — agregado principal del API (`PaymentOrder` en dominio). |

### Endpoints REST

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/payment-initiation/payment-orders` | Crear una nueva orden de pago |
| `GET` | `/payment-initiation/payment-orders/{paymentOrderId}` | Consultar el detalle de una orden |
| `GET` | `/payment-initiation/payment-orders/{paymentOrderId}/status` | Consultar solo el estado (vista reducida) |

Contrato detallado: `src/main/resources/openapi/openapi.yaml`.

### Arquitectura hexagonal (Ports & Adapters)

El núcleo **no depende** de Spring ni de HTTP; los adaptadores implementan los puertos.

```text
src/main/java/com/hiberius/paymentinitiation/
├── PaymentInitiationApplication.java
├── domain/
│   ├── exception/                    # Excepciones de dominio (p. ej. BusinessRuleViolationException)
│   └── model/
│       ├── PaymentOrder.java           # Agregado principal
│       ├── PaymentOrderInitiation.java # Datos de entrada a la creación
│       ├── PaymentOrderId.java
│       ├── PaymentOrderStatus.java     # Ciclo de vida (enum)
│       ├── PaymentOrderStatusSnapshot.java
│       ├── Iban.java, Money.java, ExternalReference.java, RemittanceInfo.java
├── port/
│   ├── in/                           # Puertos entrantes (casos de uso)
│   │   ├── CreatePaymentOrderUseCase.java
│   │   ├── GetPaymentOrderUseCase.java
│   │   └── GetPaymentOrderStatusUseCase.java
│   └── out/
│       ├── PaymentOrderRepository.java
│       └── PaymentOrderIdGenerator.java
├── application/
│   ├── service/                      # Implementación de casos de uso
│   │   ├── CreatePaymentOrderService.java
│   │   ├── GetPaymentOrderService.java
│   │   └── GetPaymentOrderStatusService.java
│   └── exception/                    # ApplicationException, etc.
├── adapter/
│   ├── in/web/
│   │   ├── PaymentOrderController.java
│   │   ├── PaymentOrderApiMapper.java    # OpenAPI DTO ↔ dominio
│   │   ├── GlobalExceptionHandler.java   # Problem Details RFC 7807
│   │   └── error/                        # ProblemResponseFactory, ApiErrorCode
│   └── out/persistence/
│       ├── InMemoryPaymentOrderRepository.java
│       └── SequentialPaymentOrderIdGenerator.java
└── config/
    └── PaymentInitiationConfiguration.java
```

Código **generado** desde OpenAPI (no editar): `target/generated-sources/openapi/`.

### Stack tecnológico

| Tecnología | Versión / nota | Propósito |
|------------|----------------|-----------|
| **Java** | 17 | Lenguaje y `release` del compilador |
| **Spring Boot** | 3.3.6 | Framework (`spring-boot-starter-web`, validation, actuator) |
| **OpenAPI Generator** | 7.10.0 | Generación contract-first desde `openapi.yaml` |
| **JaCoCo** | 0.8.12 | Cobertura de tests (umbral líneas ≥ 80 % en el bundle configurado) |
| **Checkstyle** | plugin 3.5.0 | Estilo de código (`config/checkstyle/`) |
| **SpotBugs** | plugin 4.8.6.4 | Análisis estático |
| **JUnit 5, AssertJ, Mockito** | vía Spring Boot test | Pruebas unitarias e integración |
| **SpringDoc OpenAPI** | 2.6.0 | Swagger UI y JSON OpenAPI en runtime (`springdoc-openapi-starter-webmvc-ui`) |
| **Docker** | — | Imagen multi-stage (`Dockerfile`, `docker-compose.yml`) |

No se usan en este repo **Lombok**, **MapStruct**, **R2DBC** ni **PostgreSQL**; la persistencia es **solo en memoria** salvo que sustituyas el adaptador `PaymentOrderRepository`.

### Estados de la orden de pago (`PaymentOrderStatus`)

Valores definidos en dominio para el ciclo de vida (referencia BIAN / procesamiento posterior). En la entrega actual la creación fija el estado inicial **`RECEIVED`**; las transiciones siguientes quedarían para capas de proceso o futuras extensiones.

```text
                    ┌─────────────┐
                    │  RECEIVED   │  ← Estado inicial al crear la orden (este servicio)
                    └──────┬──────┘
                           │
                           ▼
                    ┌─────────────┐
                    │  PENDING    │  ← En cola / validación
                    └──────┬──────┘
                           │
           ┌───────────────┼───────────────┐
           ▼               ▼               ▼
    ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
    │  ACCEPTED   │ │  REJECTED   │ │   FAILED    │
    └──────┬──────┘ └─────────────┘ └─────────────┘
           │
           ▼
    ┌─────────────┐
    │   SETTLED   │  ← Liquidación completada (referencia)
    └─────────────┘
```

### Arranque rápido (desarrollo in-memory)

**Prerrequisitos:** Java 17+, Maven 3.8+.

```bash
git clone https://github.com/<usuario>/<repositorio>.git
cd <repositorio>
```

```bash
mvn clean compile   # genera código OpenAPI y compila
mvn test            # pruebas
mvn spring-boot:run # API en http://localhost:8080
```

No hay perfiles Spring `dev` / `local` / `docker` con base de datos: la configuración por defecto es **solo** `application.yml` (puerto 8080, memoria). Para Docker, ver [§ Ejecución con Docker](#7-ejecución-con-docker).

### URLs de la aplicación (local)

Con el servicio en marcha (`mvn spring-boot:run` o JAR/Docker) y **puerto por defecto 8080** (`server.port`). Si cambias el puerto, sustituye `8080` en las URLs.

| Recurso | URL |
|---------|-----|
| **API base** | `http://localhost:8080` |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **OpenAPI (JSON)** | `http://localhost:8080/api-docs` |
| **Health** | `http://localhost:8080/actuator/health` |

La fuente de verdad del contrato sigue siendo `src/main/resources/openapi/openapi.yaml` (contract-first); SpringDoc expone la API descubierta por **introspección** de los controladores en ejecución.

---

## Ejemplos de uso

Con el servicio en marcha en `http://localhost:8080`. Los identificadores internos se generan en secuencia (`PO-000001`, `PO-000002`, …) tras un arranque limpio; sustituye `{id}` por el `paymentOrderId` devuelto en el **201 Created**.

**Regla de negocio:** `requestedExecutionDate` debe ser **la fecha de hoy o una fecha futura** (no puede estar en el pasado respecto al reloj del servidor).

### Crear una orden de pago (`curl`)

```bash
curl -X POST http://localhost:8080/payment-initiation/payment-orders \
  -H "Content-Type: application/json" \
  -d '{
    "externalReference": "EXT-2026-001",
    "debtorAccount": {
      "iban": "ES9121000418450200051332"
    },
    "creditorAccount": {
      "iban": "DE89370400440532013000"
    },
    "instructedAmount": {
      "amount": 150.75,
      "currency": "EUR"
    },
    "remittanceInformation": "Factura INV-001-2026",
    "requestedExecutionDate": "2026-12-25"
  }'
```

**Respuesta (201 Created)** — cuerpo acotado al contrato `PaymentOrderCreationResponse` (no devuelve el detalle completo de cuentas e importe; usa `Location` para el recurso creado):

```json
{
  "paymentOrderId": "PO-000001",
  "externalReference": "EXT-2026-001",
  "status": "RECEIVED",
  "createdAt": "2026-03-22T14:30:00.123Z"
}
```

El valor real de `createdAt` depende del reloj del servidor. El estado inicial siempre es **`RECEIVED`** (no `INITIATED`).

### Consultar una orden de pago

```bash
curl http://localhost:8080/payment-initiation/payment-orders/PO-000001
```

**Respuesta (200 OK)** — ejemplo ilustrativo (`PaymentOrder` completo):

```json
{
  "paymentOrderId": "PO-000001",
  "externalReference": "EXT-2026-001",
  "debtorAccount": { "iban": "ES9121000418450200051332" },
  "creditorAccount": { "iban": "DE89370400440532013000" },
  "instructedAmount": { "amount": 150.75, "currency": "EUR" },
  "remittanceInformation": "Factura INV-001-2026",
  "requestedExecutionDate": "2026-12-25",
  "status": "RECEIVED",
  "createdAt": "2026-03-22T14:30:00.123Z",
  "statusChangedAt": "2026-03-22T14:30:00.123Z"
}
```

### Consultar solo el estado

```bash
curl http://localhost:8080/payment-initiation/payment-orders/PO-000001/status
```

**Respuesta (200 OK)** — proyección `PaymentOrderStatusView` (campo **`lastUpdated`**, no `statusUpdateDateTime`):

```json
{
  "paymentOrderId": "PO-000001",
  "status": "RECEIVED",
  "lastUpdated": "2026-03-22T14:30:00.123Z"
}
```

### PowerShell (Windows)

Ajusta `requestedExecutionDate` a una fecha válida (hoy o futura).

```powershell
# Crear orden de pago
$body = @{
    externalReference = "EXT-2026-001"
    debtorAccount     = @{ iban = "ES9121000418450200051332" }
    creditorAccount   = @{ iban = "DE89370400440532013000" }
    instructedAmount  = @{ amount = 250.00; currency = "EUR" }
    remittanceInformation = "Pago servicios"
    requestedExecutionDate = "2026-12-31"
} | ConvertTo-Json -Depth 5

Invoke-RestMethod -Uri "http://localhost:8080/payment-initiation/payment-orders" `
    -Method POST -Body $body -ContentType "application/json; charset=utf-8"
```

Consultas GET:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/payment-initiation/payment-orders/PO-000001"
Invoke-RestMethod -Uri "http://localhost:8080/payment-initiation/payment-orders/PO-000001/status"
```

---

## 1. Contexto del problema

La entidad bancaria de la localidad norte de la ciudad está en un proceso de migración de varios de sus servicios legados SOAP hacia servicios REST, los cuales estarán alineados de banca (BIAN). Este proyecto 
para la empresa es de vital importancia, además de ser crítico dentro del proceso de modernización, porque estos servicios SOAP manejan muchos procesos core en áreas importante dentro de la entidad 
bancaria. Por lo cual se requiere velocidad en el proceso de migración, sin perder la calidad y eficiencia de los servicios migrados, y que no afecten a los consumidores dentro del ecosistema del área de negocio; 
de esta manera, se busca incorporar el uso de Inteligencia Artificial como asistente en el desarrollo de los nuevos servicios REST. 

El alcance funcional de la prueba se centra en la API REST y en una persistencia **en memoria** con generación secuencial de identificadores, suficiente para demostrar arquitectura, contrato y calidad de código sin acoplar a un motor de base de datos concreto.

---

## 2. Decisiones de arquitectura

| Decisión | Motivo |
|----------|--------|
| **Hexagonal (puertos y adaptadores)** | El dominio y los casos de uso no dependen de Spring ni de HTTP. Los puertos `in` definen operaciones; los `out` abstraen persistencia e IDs. Los adaptadores implementan tecnología (REST, repositorio en memoria). |
| **Contract-first (OpenAPI)** | El contrato (`openapi.yaml`) es la fuente de verdad; el código de API y DTOs se genera con **OpenAPI Generator**, evitando divergencias entre documentación y implementación. |
| **Dominio rico** | Reglas como “deudor ≠ acreedor”, “fecha de ejecución no en el pasado” y validación de importes/monedas viven en el **modelo de dominio**, no en el controlador. |
| **Errores globales** | Un `GlobalExceptionHandler` centraliza el mapeo a **Problem Details** con códigos de error estables (`VALIDATION_ERROR`, `RESOURCE_NOT_FOUND`, etc.). |
| **In-memory repository** | Reduce complejidad operativa en la prueba; el puerto `PaymentOrderRepository` permite sustituir por JDBC/JPA sin tocar el dominio. |

---

## 3. Contexto y decisiones de migración (desde la etapa inicial)

### 3.1. Etapa inicial (punto de partida)

En muchos entornos bancarios la iniciación de pagos parte de **servicios tipo RPC/SOAP** o integraciones acopladas: contratos en WSDL, sobre XML, con acuerdos implícitos entre equipos. Esa etapa suele ser válida para sistemas cerrados, pero dificulta **evolución independiente**, **documentación viva alineada al código** y **consumo desde canales modernos** (APIs HTTP, gateways, mobile).

La migración planteada aquí no reproduce un WSDL concreto en el repositorio; toma ese contexto como **referencia** y define un **servicio REST** que cubre las mismas capacidades de negocio de forma explícita en **OpenAPI**.

### 3.2. Decisiones tomadas en el proceso de migración

| Fase / decisión | Qué implica |
|-----------------|-------------|
| **Contrato antes que implementación** | Se parte de `openapi.yaml` (contract-first); los DTOs y la interfaz de API se generan, reduciendo desalineación doc ↔ código frente a enfoques “código primero”. |
| **Recursos y verbos HTTP** | Las operaciones de negocio se modelan como **recurso** `Payment Order` y rutas REST en lugar de un único endpoint SOAP por operación; se usa **201 + `Location`** en creación y **GET** semánticos para lectura. |
| **Errores interoperables** | Se adopta **RFC 7807** (`application/problem+json`) en lugar de cuerpos de fallo ad hoc, acercando la experiencia a APIs públicas y a gateways que esperan formatos estándar. |
| **Dominio estable, tecnología sustituible** | Arquitectura **hexagonal**: el núcleo (reglas de negocio) no depende de Spring ni del transporte; la “migración” tecnológica futura (p. ej. persistencia JDBC) se limita a adaptadores. |
| **Alcance acotado en la primera entrega** | Persistencia **en memoria** y IDs secuenciales: suficiente para demostrar el diseño y el contrato sin bloquear la entrega en infraestructura de base de datos. |

### 3.3. Correspondencia conceptual legado (SOAP) → REST

| Legado (conceptual) | REST actual |
|----------------------|-------------|
| Operación tipo **SubmitPaymentOrder** | `POST /payment-initiation/payment-orders` — creación de la orden con cuerpo JSON y respuesta **201** + cabecera `Location`. |
| Consulta de estado / detalle vía SOAP | `GET /payment-initiation/payment-orders/{paymentOrderId}` y `GET /payment-initiation/payment-orders/{paymentOrderId}/status`. |

### 3.4. Ventajas del enfoque elegido

**HTTP semántico**, **JSON** y **OpenAPI** como contrato versionable; **stateless** sobre HTTP; errores estándar (**Problem Details**); mejor integración con gateways, documentación automática y pruebas contractuales frente a un modelo exclusivamente SOAP en la etapa inicial.

---

## 4. Alineación con BIAN Payment Initiation / Payment Order

**BIAN** define *Service Domains* y *Business Objects* para estandarizar capacidades bancarias. En **Payment Initiation**, el foco es **capturar y validar** la intención de pago antes de procesamiento posterior (liquidación, clearing, etc.).

En esta solución:

- El **Payment Order** es el agregado principal: identificador opaco, referencia externa, cuentas deudor/acreedor, importe instruido, fecha de ejecución solicitada y estado de ciclo de vida (p. ej. `RECEIVED`).
- Los endpoints y esquemas OpenAPI están etiquetados y descritos en línea con ese **ámbito funcional** (iniciación y consulta), sin implementar otros dominios BIAN (p. ej. Payment Execution).

La correspondencia es **conceptual**: el contrato nombra explícitamente la alineación con BIAN en `info.description` del OpenAPI.

---

## 5. Estructura del proyecto

```text
src/main/java/com/hiberius/paymentinitiation/
├── PaymentInitiationApplication.java
├── domain/                    # Modelo de dominio y excepciones de negocio
│   ├── exception/
│   └── model/
├── application/               # Casos de uso (servicios de aplicación)
│   ├── exception/
│   └── service/
├── port/
│   ├── in/                    # Puertos entrantes (use cases)
│   └── out/                   # Puertos salientes (repositorio, generador de IDs)
├── adapter/
│   ├── in/web/                # REST: controlador, mapeo API, manejo de errores
│   └── out/persistence/       # Implementación en memoria
└── config/

src/main/resources/
├── application.yml
└── openapi/openapi.yaml       # Contrato OpenAPI (fuente para generación)

target/generated-sources/openapi/   # Código generado (no editar a mano)
```

---

## 6. Ejecución local

**Requisitos:** JDK **17** (o compatible con el `release` del `pom.xml`), **Maven 3.8+**.

1. Clonar o copiar el proyecto y situarse en la raíz del repositorio (donde está el `pom.xml`).
2. (Opcional) Compilar y pasar tests: `mvn verify`.
3. Arrancar la aplicación con **una** de estas opciones:

**Opción A — Maven (desarrollo habitual):**

```bash
mvn spring-boot:run
```

**Opción B — JAR ejecutable** (tras empaquetar):

```bash
mvn -q -DskipTests package
java -jar target/payment-initiation-service-1.0.0-SNAPSHOT.jar
```

**Comprobar que responde:**

- **API base:** `http://localhost:8080`
- **Puerto HTTP:** `8080` por defecto (`server.port` en `src/main/resources/application.yml`).
- **Salud:** `GET http://localhost:8080/actuator/health` (debe devolver `{"status":"UP",...}`).

Si el puerto **8080** está ocupado, arranca con otro puerto, por ejemplo:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

o:

```bash
java -jar target/payment-initiation-service-1.0.0-SNAPSHOT.jar --server.port=8081
```

---

## 7. Ejecución con Docker

**Requisitos:** Docker Engine y Docker Compose v2 (plugin `docker compose`).

### 7.1. Con Docker Compose (recomendado)

Desde la **raíz del repositorio** (junto a `docker-compose.yml`):

1. Construir y levantar en segundo plano:

```bash
docker compose up --build -d
```

2. Verificar salud: `GET http://localhost:8080/actuator/health` (Compose publica **8080** del host al **8080** del contenedor; ver `docker-compose.yml`).

3. Parar y eliminar contenedores de este compose:

```bash
docker compose down
```

### 7.2. Solo imagen Docker (sin Compose)

```bash
docker build -t hiberius/payment-initiation-service:local .
docker run --rm -p 8080:8080 hiberius/payment-initiation-service:local
```

Multi-stage build, usuario no root y variables: `Dockerfile` y `docker-compose.yml`.

---

## 8. Cómo correr tests

```bash
mvn test
```

Incluye tests unitarios (dominio, aplicación, manejo de errores) y pruebas de integración REST con **WebTestClient** ligado a **MockMvc**.

Para una clase concreta:

```bash
mvn test -Dtest=PaymentOrderWebTestClientIntegrationTest
```

---

## 9. Verificar calidad de código

Ejecutar la verificación **completa** del proyecto: tests, informe **JaCoCo**, umbral de cobertura, **Checkstyle** y **SpotBugs** (según `pom.xml`).

```bash
# Verificación completa (tests + JaCoCo + Checkstyle + SpotBugs)
mvn verify
```

Si solo necesitas tests y reporte de cobertura sin el resto de comprobaciones de la fase `verify`, basta con `mvn test` (también genera `target/site/jacoco/` cuando JaCoCo está configurado en el build).

**Abrir el informe HTML de cobertura** (tras `mvn test` o `mvn verify`):

```bash
# Windows (PowerShell o CMD)
start target/site/jacoco/index.html

# macOS
open target/site/jacoco/index.html

# Linux (alternativa habitual)
xdg-open target/site/jacoco/index.html
```

También puedes abrir manualmente el archivo `target/site/jacoco/index.html` desde el explorador de archivos.

---

## 10. Cobertura y calidad

| Herramienta | Rol |
|-------------|-----|
| **JaCoCo** | Cobertura de **líneas** del bundle principal; umbral mínimo **≥ 80 %** (código generado por OpenAPI excluido). Informe HTML: `target/site/jacoco/index.html` tras `mvn test` o `verify`. |
| **Checkstyle** | Estilo y convenciones mínimas (`config/checkstyle/`). |
| **SpotBugs** | Análisis estático; exclusiones para paquete `generated` (`config/spotbugs/exclude.xml`). |

Ajustar umbrales o exclusiones solo con criterio justificado (falsos positivos, código generado).

**Entrega académica / trazabilidad de pruebas:** [`docs/entregable-pruebas.md`](docs/entregable-pruebas.md).

---

## 11. Uso de herramientas de IA

En el desarrollo de esta prueba se utilizó asistencia de **IA generativa** (p. ej. en el IDE) para:

- Acelerar la escritura de **tests**, **configuración Maven** (JaCoCo, Checkstyle, SpotBugs) y **Docker**.
- Iterar sobre **manejadores de error** y documentación.

El diseño (hexagonal, contract-first, BIAN), las **reglas de dominio** y la **coherencia del contrato OpenAPI** fueron revisados y validados manualmente. Cualquier fragmento sugerido por IA se sometió al mismo estándar que el código escrito a mano: compilación, `mvn verify` y legibilidad para revisión por pares.

**Recomendación:** en entregas académicas o laborales, mantener esta transparencia y poder explicar cada archivo clave en entrevista.

---

## 12. Mejoras futuras

- **Persistencia real** (PostgreSQL, etc.) y migraciones (**Flyway/Liquibase**).
- **Idempotencia** en creación (clave de idempotencia por `externalReference` o header dedicado).
- **Seguridad:** OAuth2/JWT, mTLS o API keys según entorno.
- **Observabilidad:** correlación de trazas (**Micrometer/OpenTelemetry**), métricas de negocio.
- **Resiliencia:** timeouts y circuit breakers en llamadas salientes (cuando existan).
- **Paginación y filtros** en listados si el producto lo exige.
- **Pruebas de contrato** consumidor/proveedor (p. ej. Spring Cloud Contract) sobre el OpenAPI publicado.

---

## Referencia rápida de endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/payment-initiation/payment-orders` | Crear orden de pago |
| `GET` | `/payment-initiation/payment-orders/{id}` | Detalle de la orden |
| `GET` | `/payment-initiation/payment-orders/{id}/status` | Estado proyectado |

Documentación detallada del contrato: `src/main/resources/openapi/openapi.yaml`.

---

## Licencia y propiedad

Código entregado en el marco de una **prueba técnica**; ajustar licencia y propiedad intelectual según requisitos del evaluador o la organización.

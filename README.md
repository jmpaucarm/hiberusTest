# Payment Initiation Service

Microservicio **contract-first** para **iniciación y consulta de órdenes de pago**, alineado conceptualmente con el dominio **BIAN Payment Initiation** y el recurso de negocio **Payment Order**. Implementado en **Java 17** y **Spring Boot 3** como entrega de prueba técnica.

**Contenido del README:** [contexto](#1-contexto-del-problema) · [arquitectura](#2-decisiones-de-arquitectura) · [migración (etapa inicial → REST)](#3-contexto-y-decisiones-de-migración-desde-la-etapa-inicial) · [BIAN](#4-alineación-con-bian-payment-initiation--payment-order) · [estructura](#5-estructura-del-proyecto) · [ejecución local](#6-ejecución-local) · [Docker](#7-ejecución-con-docker) · [tests](#8-cómo-correr-tests) · [`mvn verify`](#9-cómo-correr-mvn-verify) · [cobertura](#10-cobertura-y-calidad)

---

## 1. Contexto del problema

En entornos bancarios y de tesorería, la **iniciación de pagos** (captura de instrucciones del cliente: cuentas, importe, fecha de ejecución, referencias) debe ser **coherente**, **trazable** y **expuesta de forma estable** a canales y sistemas aguas arriba.

Este servicio modela el ciclo mínimo de una **orden de pago** (`PaymentOrder`): creación con validaciones de negocio, consulta del detalle y consulta del estado, con respuestas de error homogéneas (**RFC 7807** `application/problem+json`).

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

## 9. Cómo correr `mvn verify`

`verify` ejecuta el ciclo completo de calidad definido en el `pom.xml` **después** de compilar y pasar tests:

```bash
mvn verify
```

En la fase `verify` se ejecutan, entre otros: **JaCoCo check**, **Checkstyle**, **SpotBugs** (según configuración actual del proyecto).

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

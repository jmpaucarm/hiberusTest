# Registro de prompts — uso asistido de IA (prueba técnica)

**Proyecto:** `payment-initiation-service`  
**Herramienta principal:** Cursor asistente de código en IDE (sesiones iterativas)  
**Uso de IA:** asistencia para análisis, borradores de contrato, propuesta de dominio, estructura hexagonal y generación inicial de tests.  
**Validación humana:** todas las salidas fueron revisadas y ajustadas manualmente antes de integrarlas al proyecto.

---

## Enfoque general de uso de IA

La IA se utilizó como asistente para acelerar el proceso de migración desde SOAP a REST sin perder criterio arquitectónico.  
El flujo seguido fue:

1. Analizar el WSDL legado y resumir sus operaciones.
2. Proponer el mapping conceptual SOAP → REST → dominio.
3. Construir un borrador de `openapi.yaml` alineado a `Payment Initiation / PaymentOrder`.
4. Proponer un modelo de dominio mínimo y defendible.
5. Diseñar la estructura del proyecto con arquitectura hexagonal.
6. Generar tests unitarios y de aplicación como base inicial.
7. Revisar manualmente el resultado, corrigiendo decisiones de diseño demasiado rígidas o no pedidas por la prueba.

---

## Prompt 1 — Análisis del WSDL y mapping a BIAN

**Objetivo:** entender el servicio legado SOAP y transformarlo en una propuesta REST alineada al dominio esperado.

**Prompt utilizado (resumen):**

> Tu tarea es:
> 1. Identificar operaciones SOAP, request/response, campos relevantes y estados.
> 2. Detectar qué conceptos del SOAP equivalen a:
>    - payment order
>    - payment order status
>    - identifiers
>    - amount
>    - currency
>    - debtor
>    - creditor
>    - execution date
>    - status
> 3. Proponer un mapping SOAP -> REST -> dominio.
> 4. Indicar ambigüedades o campos que deban simplificarse.
> 5. Proponer nombres REST y de dominio coherentes con BIAN.
> 6. Sugerir un modelo mínimo suficiente para la prueba técnica, evitando sobreingeniería.
>
> Devuélveme:
> - Resumen del WSDL
> - Tabla de mapping
> - Propuesta de recursos REST
> - Propuesta de estados de negocio
> - Riesgos o supuestos
> - Recomendación final para el `openapi.yaml`

**Resultado obtenido (resumen):**

La IA identificó correctamente las dos operaciones del legado:

- `SubmitPaymentOrder`
- `GetPaymentOrderStatus`

y propuso el siguiente mapping conceptual:

- `SubmitPaymentOrder` → `POST /payment-initiation/payment-orders`
- `GetPaymentOrderStatus` → `GET /payment-initiation/payment-orders/{paymentOrderId}/status`

Además, sugirió incorporar:

- `GET /payment-initiation/payment-orders/{paymentOrderId}`

como una capacidad nueva del servicio REST para recuperar el recurso completo, aunque no exista literalmente en el WSDL.

También detectó correctamente estas equivalencias de dominio:

- `externalId` → referencia externa / correlación
- `paymentOrderId` → identificador interno de la orden
- `debtorIban` / `creditorIban` → cuentas origen y destino
- `amount` + `currency` → monto instruido
- `requestedExecutionDate` → fecha solicitada de ejecución
- `status` → estado de negocio

**Valor aportado:**

Este prompt sirvió para convertir el legado SOAP en un lenguaje entendible para el diseño REST y para justificar la alineación conceptual con BIAN sin copiar literalmente el WSDL.

---

## Prompt 2 — Propuesta de dominio mínimo

**Objetivo:** definir un modelo de dominio simple pero sólido antes de implementar aplicación, puertos y adaptadores.

**Prompt utilizado (resumen):**

> Con base en el análisis anterior, define un modelo de dominio mínimo pero sólido.
>
> Necesito:
> 1. Entidades y value objects principales
> 2. Invariantes del dominio
> 3. Estados de `PaymentOrder`
> 4. Reglas de validación
> 5. Qué campos deben ser obligatorios
> 6. Qué errores de negocio deben existir
> 7. Qué datos deben ir en el response de creación, consulta y status
>
> Quiero una propuesta simple, coherente con banca y fácil de implementar en Spring Boot 3 con arquitectura hexagonal.
>
> Devuélveme:
> - diagrama textual del dominio
> - lista de clases del dominio
> - responsabilidades de cada clase
> - invariantes
> - recomendación de package structure del dominio

**Resultado obtenido (resumen):**

La IA propuso un agregado raíz `PaymentOrder` con los siguientes elementos:

- `PaymentOrderId`
- `ExternalReference`
- `Iban`
- `Money`
- `CurrencyCode`
- `RemittanceInfo`
- `PaymentOrderStatus`

También propuso invariantes útiles:

- referencia externa obligatoria
- cuentas origen y destino distintas
- monto positivo
- moneda válida
- fecha de ejecución no pasada
- estado inicial consistente

Y recomendó un conjunto acotado de estados:

- `RECEIVED`
- `PENDING`
- `ACCEPTED`
- `REJECTED`
- `SETTLED`
- `FAILED`

**Valor aportado:**

Este prompt ayudó a bajar el diseño a clases concretas y reglas de negocio testeables, evitando una implementación anémica o centrada solo en DTOs.

---

## Prompt 3 — Borrador de `openapi.yaml`

**Objetivo:** obtener un contrato OpenAPI 3 contract-first alineado al recurso `payment-orders`.

**Prompt utilizado (resumen):**

> Genera un borrador OpenAPI 3.0 para:
> - `POST /payment-initiation/payment-orders`
> - `GET /payment-initiation/payment-orders/{paymentOrderId}`
> - `GET /payment-initiation/payment-orders/{paymentOrderId}/status`
>
> Usa un request JSON con:
> - `externalReference`
> - `debtorAccount.iban`
> - `creditorAccount.iban`
> - `instructedAmount.amount`
> - `instructedAmount.currency`
> - `remittanceInformation`
> - `requestedExecutionDate`
>
> Incluye respuestas 201, 400, 404 y, opcionalmente, `application/problem+json`.

**Resultado obtenido (resumen):**

La IA generó un contrato bastante completo, con:

- recurso principal `payment-orders`
- request body orientado al dominio
- subrecurso `/status`
- schemas reutilizables
- propuesta de errores tipo `ProblemDetails`

**Valor aportado:**

Permitió partir de un borrador consistente y aceleró el enfoque contract-first usando OpenAPI como fuente principal para DTOs e interfaces.

---

## Validación humana sobre OpenAPI

La salida generada por IA fue **corregida manualmente** en varios puntos para alinearla mejor con la consigna y evitar decisiones no pedidas:

### 1. Se eliminó la restricción de UUID en `paymentOrderId`
La IA propuso `format: uuid`, pero se decidió removerlo porque implicaba una decisión de diseño no exigida por la prueba.

### 2. Se simplificó la validación de `amount`
En lugar de usar `exclusiveMinimum`, se dejó:

```yaml
minimum: 0.01
```

por compatibilidad más segura con herramientas OpenAPI 3.0.x.

### 3. Se relajó la validación de IBAN en el contrato
La IA había propuesto un patrón cercano a un IBAN real.  
Se eliminó porque en pruebas técnicas y mocks suelen usarse cuentas dummy o etiquetas no bancarias reales.  
Se prefirió:

- `minLength`
- `maxLength`
- descripción clara

dejando la validación más estricta para dominio o servicio.

### 4. Se evitó sobrecerrar algunos detalles del contrato
Se priorizó una API pragmática, alineada al ejercicio y a los ejemplos de Postman, en lugar de una especificación demasiado rígida.

**Motivo general de estos cambios:**

La IA ayudó a generar un buen borrador, pero fue necesaria validación humana para ajustar el contrato al contexto real de la prueba.

---

## Prompt 4 — Estructura hexagonal del proyecto

**Objetivo:** definir una estructura práctica de arquitectura hexagonal para Spring Boot 3.

**Prompt utilizado (resumen):**

> Quiero que me propongas una estructura de proyecto completa basada en arquitectura hexagonal para este caso de `Payment Initiation / PaymentOrder` en Spring Boot 3.
>
> Necesito:
> - packages
> - responsabilidades
> - separación dominio / aplicación / puertos / adaptadores / configuración
> - ubicación del código generado por OpenAPI
> - dónde implementar controladores que consumen interfaces generadas
> - dónde poner mapper, repository en memoria o persistencia simple, exceptions y handlers
>
> Devuélveme:
> 1. árbol de carpetas propuesto
> 2. explicación de cada paquete
> 3. lista priorizada de archivos que debo crear primero
> 4. convención de nombres recomendada

**Resultado obtenido (resumen):**

La IA propuso una estructura orientada a:

- `domain`
- `application`
- `port.in`
- `port.out`
- `adapter.in.web`
- `adapter.out.persistence`
- `config`

y ubicó correctamente:

- controladores REST implementando interfaces generadas
- mapper de API en adaptador de entrada
- repositorio en memoria en adaptador de salida
- handlers de errores en la capa web

**Corrección manual aplicada:**

Se refinó la propuesta para:

- no mezclar `generated` dentro de `src/main/java`
- dejar el código generado en `target/generated-sources/openapi`
- separar mejor puertos del dominio
- evitar una capa de “infraestructura” demasiado genérica

**Valor aportado:**

Este prompt aceleró la organización inicial del proyecto y ayudó a mantener coherencia con arquitectura hexagonal real.

---

## Prompt 5 — Generación de tests

**Objetivo:** generar una base de tests útil para dominio, casos de uso y proyecciones.

**Prompt utilizado (resumen):**

> Genera tests unitarios completos para el dominio y casos de uso de `PaymentOrder`.
>
> Necesito usar:
> - JUnit 5
> - AssertJ
> - Mockito donde aplique
>
> Debes cubrir al menos:
> - creación válida de payment order
> - validaciones obligatorias
> - estado inicial correcto
> - consulta existente
> - consulta inexistente
> - status existente
> - status inexistente
>
> Reglas:
> - tests limpios y legibles
> - nombres descriptivos
> - sin mocks innecesarios
> - cobertura útil, no artificial
>
> Entrégame:
> - lista de tests recomendados
> - código completo de los tests
> - qué partes del sistema quedan cubiertas

**Resultado obtenido (resumen):**

La IA propuso tests para:

- agregado `PaymentOrder`
- value objects como `Money`, `Iban`, `ExternalReference`, `PaymentOrderId`, `RemittanceInfo`
- snapshot de status
- `CreatePaymentOrderService`
- `GetPaymentOrderService`
- `GetPaymentOrderStatusService`

**Valor aportado:**

La salida generada sirvió como punto de partida sólido para alcanzar cobertura útil en dominio y aplicación, sin recurrir a mocks innecesarios en lógica de negocio.

---

## Correcciones humanas sobre tests y diseño

Además de ajustar el contrato, se realizaron validaciones manuales sobre la salida generada por IA:

- simplificación de nombres y paquetes
- alineación con la estructura final del proyecto
- ajuste de expectativas de tests a los estados realmente usados
- adecuación de asserts a la implementación definitiva
- limpieza de casos redundantes o demasiado artificiales

---

## Evidencia de calidad alcanzada

Como resultado final del trabajo:

- el proyecto alcanzó una cobertura total superior al mínimo requerido
- se superó el umbral de 80% de líneas pedido por la prueba
- la cobertura se concentró en dominio, aplicación y adaptadores principales

La IA ayudó a acelerar el arranque, pero la consolidación final del proyecto requirió validación y corrección manual.

---

## Convenciones de uso de IA durante la prueba

- siempre se entregó contexto suficiente antes de pedir generación
- los prompts largos se fragmentaron por etapas
- se usó IA para borradores técnicos, no para aceptar salidas sin revisión
- cada artefacto generado fue revisado antes de integrarse
- la decisión final sobre contrato, dominio y estructura fue humana

---

## Artefactos relacionados en el repositorio

| Artefacto | Ubicación |
|---|---|
| WSDL legado | `legacy/PaymentOrderService.wsdl` |
| OpenAPI final | `src/main/resources/openapi/openapi.yaml` |
| Código generado | `target/generated-sources/openapi/` |
| Tests | `src/test/java/...` |
| Evidencia de IA | `ai/prompts.md`, `ai/decisions.md`, `ai/generations/` |

---

## Conclusión

La IA se utilizó como acelerador para:

- comprender el legado SOAP
- proponer el mapping hacia REST
- definir un dominio mínimo
- estructurar una solución hexagonal
- generar una base de tests

La solución final no fue aceptada automáticamente desde la IA: se aplicó revisión humana para ajustar contrato, validaciones, estructura y alcance a lo que realmente pedía la prueba.

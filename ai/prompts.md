# Registro de prompts — uso asistido de IA (prueba técnica)

**Proyecto:** `payment-initiation-service`  
**Herramienta:** asistente de código en IDE (modelo conversacional, sesiones iterativas).  
**Nota:** Los textos siguientes son **resúmenes fieles al intento** de cada interacción; no constituyen transcripción literal completa por límites de contexto y privacidad.

---

## 1. Análisis del WSDL

**Objetivo:** Entender operaciones, tipos XSD y semántica antes de diseñar REST.

**Prompt (resumen):**

> Tengo un WSDL de legado en `legacy/PaymentOrderService.wsdl`. Identifica el `portType`, las operaciones SOAP, los elementos de request/response y el mapeo de tipos XSD (string, decimal, date, dateTime). Resume qué capacidad de negocio representa cada operación y qué campos son obligatorios vs opcionales.

**Contexto adjunto:** fragmento o ruta al archivo WSDL (operaciones `SubmitPaymentOrder`, `GetPaymentOrderStatus`).

**Iteraciones típicas:**

- Pedir tabla request/response por operación.
- Preguntar si `remittanceInfo` opcional en XSD implica validación distinta en API nueva.

---

## 2. Mapping a BIAN

**Objetivo:** Vincular el legado con terminología BIAN defendible en documentación y contrato.

**Prompt (resumen):**

> En BIAN, ¿qué Service Domain y Business Object encajan mejor con “iniciar una orden de pago” y “consultar estado de una orden”? Propón nombres de recurso REST y tags OpenAPI coherentes con Payment Initiation / Payment Order, sin inventar dominios que no correspondan (p. ej. no mezclar Payment Execution).

**Respuesta esperada (tipo):** referencia a *Payment Initiation* como captura de instrucción; *Payment Order* como objeto de negocio; advertencia de alcance limitado a iniciación.

---

## 3. Generación de `openapi.yaml`

**Objetivo:** Contract-first OpenAPI 3 con POST + GET alineados al WSDL y errores estándar.

**Prompt (resumen):**

> Genera un borrador OpenAPI 3.0 para:  
> - `POST /payment-initiation/payment-orders` (equivalente conceptual a SubmitPaymentOrder) con JSON: cuentas como objetos con `iban`, importe con `amount` + `currency` ISO 4217, fecha de ejecución, referencia externa, remesa opcional.  
> - `GET .../{paymentOrderId}` y `GET .../{paymentOrderId}/status`.  
> - Respuestas 201 con `Location`, 400/404/422 con `application/problem+json` y esquema ProblemDetails (type, title, status, detail, instance, code).  
> Usa tags `PaymentOrder` y descripción breve de migración desde SOAP.

**Iteraciones:** ajuste de ejemplos, nombres de propiedades (`externalReference` vs `externalId` del WSDL), restricciones de validación en componentes.

---

## 4. Esqueleto hexagonal

**Objetivo:** Paquetes Java alineados a puertos/adaptadores sin lógica de negocio en controladores.

**Prompt (resumen):**

> Para Spring Boot 3 y Java 17, propón estructura de paquetes hexagonal: `domain`, `application/service`, `port/in`, `port/out`, `adapter/in/web`, `adapter/out/persistence`. Lista clases sugeridas: casos de uso Create/Get/Status, repositorio puerto, implementación in-memory, controlador que implementa API generada desde OpenAPI. No uses capas “anémicas” sin dominio.

**Iteraciones:** generación de interfaces de puertos, nombres de excepciones de dominio, configuración de `Clock` para fechas testeables.

---

## 5. Generación de tests

**Objetivo:** Cobertura útil (dominio, casos de uso, integración REST) y calidad Maven.

**Prompts (resúmenes):**

1. **Unitarios:**  
   > Tests JUnit 5 + AssertJ para `PaymentOrder.initiate`: estado inicial RECEIVED, rechazo mismo IBAN, fecha pasada; tests para value objects Money/Iban.

2. **Casos de uso con Mockito:**  
   > Tests para `CreatePaymentOrderService` (mock repository + id generator), `GetPaymentOrderService` y `GetPaymentOrderStatusService`.

3. **Integración REST:**  
   > Pruebas de integración con WebTestClient ligado a MockMvc para POST 201, GET 200, 404, 400; mismo estilo Spring Boot 3.

4. **Calidad:**  
   > Configuración JaCoCo umbral línea ≥80%, Checkstyle razonable, SpotBugs con exclusión de código generado.

---

## Convenciones de sesión

- **Siempre** se pegó contexto relevante (archivo, error de compilación, salida de `mvn verify`).
- Las peticiones largas se **fragmentaron** (contrato → dominio → adaptadores → tests).
- No se usó IA para **decisiones de cumplimiento legal** ni para datos reales de clientes.

---

## Referencias en el repositorio

| Artefacto | Ubicación |
|-----------|-----------|
| WSDL legado | `legacy/PaymentOrderService.wsdl` |
| Contrato OpenAPI | `src/main/resources/openapi/openapi.yaml` |
| Código generado (no editar) | `target/generated-sources/openapi/` tras `mvn compile` |

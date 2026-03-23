# Decisiones, validación humana y trazabilidad — IA asistida

Este documento registra **qué se tomó del asistente**, **qué se corrigió a mano** y **por qué** la revisión humana fue obligatoria. Cumple con buenas prácticas de transparencia en entregas académicas o técnicas.

---

## 1. Resumen de respuestas obtenidas (alto nivel)

| Área | Qué aportó la IA | Utilidad |
|------|------------------|----------|
| WSDL | Tabla de operaciones/campos; equivalencia Submit → POST | Aceleró lectura inicial del XSD embebido |
| BIAN | Texto guía para tags y descripciones del `info` OpenAPI | Base para redactar el contrato sin sustituir criterio de alcance |
| OpenAPI | Esqueleto de paths, componentes y ProblemDetails | Punto de partida; requirió varias revisiones |
| Hexagonal | Lista de paquetes, nombres de puertos y servicios | Coherente con literatura DDD/hexagonal |
| Tests | Plantillas de tests y configuración Maven (JaCoCo, Checkstyle) | Reducción de tiempo; ajustes por convenciones del repo |
| Docker / README | Dockerfile multi-stage, secciones de documentación | Iteración hasta alinear puertos y comandos reales |

---

## 2. Correcciones manuales realizadas (ejemplos representativos)

| Tema | Corrección | Motivo |
|------|------------|--------|
| Nomenclatura API | `externalReference` en JSON vs `externalId` en WSDL | Alineación con convención REST y legibilidad; documentado en OpenAPI |
| Reglas de dominio | Validación “deudor ≠ acreedor” con comparación case-insensitive | Detalle fácil de equivocar; revisado contra requisitos |
| Manejo de errores | Códigos estables `ApiErrorCode`, factoría `ProblemResponseFactory` | Unificar RFC 7807 y evitar duplicar URIs en handlers |
| Logger en handler | `LOGGER` en mayúsculas | Convención Checkstyle / constantes estáticas |
| WebTestClient + servlet | Uso de `MockMvcWebTestClient.bindTo(mockMvc)` | La inyección directa de `WebTestClient` no aplica igual en apps MVC; corregido tras error en runtime |
| JaCoCo | Exclusión explícita de paquete `generated` | Cobertura significativa solo sobre código mantenido |
| Mapper `PaymentOrder` | Import del dominio vs modelo generado (homónimos) | Resolución manual de conflicto de nombres |

---

## 3. Justificación de validación humana

1. **Correctitud de negocio:** Ningún modelo de lenguaje garantiza reglas financieras; el responsable validó invariantes (fechas, importes, IBAN en el alcance acordado).

2. **Contrato OpenAPI:** Los ejemplos y códigos HTTP deben ser **consistentes** con el comportamiento real del servicio; se verificó con tests de integración y ejecución local.

3. **Seguridad y PII:** No se pegaron datos reales; el WSDL de ejemplo es sintético (`legacy/`).

4. **Compilación y CI:** Toda sugerencia de dependencias o plugins pasó por `mvn verify` en máquina del desarrollador.

5. **Propiedad intelectual y política interna:** El uso de IA se limitó a asistencia; la entrega final es revisada y aprobada por la persona candidata.

---

## 4. Aceptado / ajustado / descartado

| Elemento | Veredicto | Comentario |
|----------|-----------|------------|
| Estructura hexagonal (paquetes) | **Aceptada** | Coincide con objetivos de la prueba |
| Texto largo de descripciones BIAN en OpenAPI | **Ajustado** | Acortado y alineado al alcance real (solo iniciación) |
| Propuesta de idempotencia con header genérico | **Descartada** (v1) | Fuera de alcance; dejada como mejora futura |
| Métricas Prometheus / tracing OTLP | **Descartada** (v1) | Actuator limitado a health/info por simplicidad |
| Tests generados sin Mockito en servicios | **Ajustado** | Añadidos mocks de puertos donde correspondía |
| Nombres de excepciones genéricas (`ServiceException`) | **Descartado** | Sustituido por excepciones de dominio/aplicación más explícitas |
| Dockerfile con `dependency:go-offline` | **Descartado** | Fallos intermitentes con plugins; build simplificado a `mvn package` |

---

## 5. Criterios de revisión previa a entrega

- [ ] `mvn verify` exitoso  
- [ ] Contrato OpenAPI coherente con controlador y tests  
- [ ] README y carpeta `ai/` reflejan uso de IA con honestidad  
- [ ] Sin secretos ni credenciales en el repositorio  

---

*Documento elaborado para evidencia de práctica de desarrollo asistido. Versión alineada al estado del repositorio en la fecha de entrega.*

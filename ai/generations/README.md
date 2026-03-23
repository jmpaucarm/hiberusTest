# Artefactos generados y asistidos por IA

Esta carpeta documenta **qué tipo de salidas** se obtuvieron del asistente de IA y **dónde viven realmente** en el repositorio. No es obligatorio versionar volcados binarios ni chats completos; el valor para auditoría es la **trazabilidad** (prompts en `../prompts.md`, decisiones en `../decisions.md`).

---

## Qué suele considerarse “generación” en este proyecto

| Tipo | Ubicación en el repo | Notas |
|------|----------------------|--------|
| Contrato OpenAPI | `src/main/resources/openapi/openapi.yaml` | Editado y validado manualmente; la IA aportó borradores. |
| Código desde OpenAPI | `target/generated-sources/openapi/` (tras `mvn compile`) | **No versionado** en Git; se regenera en cada build. Paquetes típicos: `...generated.api`, `...generated.model`. |
| Código de aplicación | `src/main/java/...` | Escrito o refactorizado con asistencia; **revisión humana** obligatoria. |
| Tests | `src/test/java/...` | Plantillas y casos sugeridos por IA; aserciones y datos ajustados manualmente. |
| Config calidad | `pom.xml`, `config/checkstyle/`, `config/spotbugs/` | Fragmentos sugeridos; integración verificada con `mvn verify`. |
| Docker | `Dockerfile`, `docker-compose.yml` | Iterados hasta build estable. |

---

## Política de esta carpeta (`ai/generations/`)

- **Opcional:** Se pueden añadir aquí exportaciones de conversación (PDF/MD) si la organización lo exige.
- **Por defecto:** Permanece vacía salvo anexos; el README basta para explicar el flujo.
- **No incluir:** Claves API, datos personales, ni volcados de repositorios ajenos.

---

## Relación con el WSDL legado

El archivo de referencia **no genera código** en el build actual:

- `legacy/PaymentOrderService.wsdl` — usado como insumo de análisis y trazabilidad SOAP → REST.

Si en el futuro se añade generación JAXB/CXF, los fuentes generados deberían ignorarse en Git (`.gitignore`) o documentarse aquí igual que OpenAPI.

---

## Verificación rápida

```bash
# Regenerar código de contrato
mvn -q compile

# Comprobar que el generador produjo APIs bajo target/generated-sources/openapi
```

---

*Para el detalle de prompts y decisiones, ver `../prompts.md` y `../decisions.md`.*

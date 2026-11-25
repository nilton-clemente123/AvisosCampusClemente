# Registro de asistencia de IA — Fase 2

Fecha de inicio: **25 de noviembre de 2026**. Rama: `mejora-ia`.

La asistencia de esta implementación corresponde a **ChatGPT/Cline**, no a Gemini.
La guía menciona Gemini en Android Studio: este registro no afirma haber usado esa herramienta.
Las decisiones y pruebas registradas aquí son del trabajo asistido; el estudiante debe revisarlas
y poder explicarlas antes de la sustentación.

## Solicitud y desglose técnico

El único prompt nuevo del usuario para esta fase fue: **«ahora vamos con la fase con ia por favor»**.
Las filas identificadas como *desglose del agente* describen tareas derivadas de esa solicitud;
no son prompts adicionales escritos por el estudiante ni respuestas de Gemini.

| Prompt / tarea y procedencia | Resumen de la respuesta de ChatGPT/Cline | Lo que se adaptó o descartó | Criterio técnico |
|---|---|---|---|
| Usuario: «ahora vamos con la fase con ia por favor» | Revisar la guía y elegir tres mejoras: dos canales, texto expandible y receptor del cargador. Mantener `main` y trabajar en `mejora-ia`. | No se incorporan Firebase, persistencia ni nuevas dependencias. | El alcance cubre tres mejoras y modifica ViewModel/Repository sin mezclar la fase 1. |
| Desglose del agente: separar avisos normales y recordatorios urgentes sin Context en el ViewModel. | Introducir `PrioridadAviso`, conservarla en el Repository y marcar el recordatorio como urgente. El Notificador la traduce a un canal del sistema. | Se conserva el ID `avisos`; se crea `urgentes` en vez de intentar subir la importancia del canal anterior. Se comprueba el bloqueo del canal seleccionado, no siempre el normal. `PendingIntent` sigue siendo inmutable. | El dominio no depende de Android. Se respeta la configuración del usuario y no se promete una ventana emergente en todo dispositivo. |
| Desglose del agente: mostrar el texto completo al expandir la notificación y ofrecer una demostración verificable. | Aplicar `BigTextStyle().bigText(aviso.texto)` y añadir `avisarTextoLargo()` con un botón propio. | Se conserva el aviso corto de fase 1. El texto largo se decide en el ViewModel, se guarda íntegro y se marca explícitamente como ficticio; no se recorta con `take` ni se construye desde la UI. | La notificación y el historial usan el mismo contenido. Se comprueba `EXTRA_BIG_TEXT`, no solamente que compile o que exista un botón. |

## Verificación de la primera mejora

- Prueba unitaria: prioridad normal por defecto y prioridad urgente conservada en el historial.
- Prueba instrumentada: ambos canales y notificaciones dirigidas al canal correcto.
- Prueba del recordatorio: evento e historial conservan el mismo aviso urgente.
- Se conservan las pruebas de permiso denegado y cancelación.
- Resultado: compilación correcta, **6 pruebas unitarias y 10 instrumentadas sin fallos**;
  Lint sin errores en el emulador API 37.

## Verificación de la segunda mejora

- Prueba instrumentada: `EXTRA_TEXT` y `EXTRA_BIG_TEXT` mantienen el mensaje completo.
- Prueba del ViewModel: el aviso largo se registra y emite sin alterar el aviso corto original.
- Resultado: compilación correcta, **6 pruebas unitarias y 12 instrumentadas sin fallos**;
  Lint sin errores en API 37.

## Fuentes oficiales consultadas

- Android Developers: *NotificationManager*, *Create and manage notification channels* y *About notifications*.
- Android Developers: *NotificationCompat.BigTextStyle* y *Broadcasts overview* para las mejoras siguientes.

`IMPORTANCE_HIGH` no anula No molestar, preferencias del usuario ni decisiones del sistema.
En Android 7 se usa prioridad alta y sonido en la notificación como alternativa a los canales.
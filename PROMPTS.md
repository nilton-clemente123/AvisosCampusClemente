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
| Desglose del agente: añadir escucha del cargador independiente del modo avión. | Crear `CargadorReceiver` para conexión/desconexión, un StateFlow y métodos en el ViewModel, un origen de historial y otro switch. | El receptor solo traduce acciones a booleanos, no notifica ni hace trabajo largo. Se registra dinámicamente con `RECEIVER_NOT_EXPORTED` y se libera en `onDispose`. Se añaden etiquetas de accesibilidad a ambos switches. No se declara un receptor permanente en el Manifest. | Cada escucha controla su propio ámbito y estado. ViewModel y Repository siguen sin Context. Las acciones de energía son anuncios protegidos del sistema; las pruebas del dispositivo simulan la alimentación, no envían broadcasts falsos desde otra app. |

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

## Verificación de la tercera mejora

- Prueba del receptor: conexión y desconexión se traducen correctamente; otras acciones se ignoran.
- Prueba del ViewModel: no genera eventos si está apagado, registra ambos estados y no modifica
  el switch del modo avión.
- Prueba de Compose: switches independientes y estado conservado al recrear la Activity.
- Comprobación manual con `adb emu power ac on/off`: aparecen Conectado y Desconectado con el
  switch activo; repetirlo con el switch apagado no añade registros.
- Resultado: **6 pruebas unitarias y 15 instrumentadas sin fallos**, compilación correcta y
  **0 errores de Lint** (12 advertencias de la plantilla/versiones).
- También se comprobó en el panel del emulador el texto largo completo y en `dumpsys notification`
  que el recordatorio se publica en `urgentes` con importancia 4, frente a 3 de `avisos`.
- Las capturas reales de las tres mejoras están en `docs/capturas/fase2/`.

## Revisión crítica y alcance

- Ninguna clase del ViewModel/Repository importa Context, NotificationManager ni BroadcastReceiver.
- Los dos receptores dinámicos tienen su propio `DisposableEffect` y su `onDispose` correspondiente.
- No se solicitó un PendingIntent mutable ni se añadieron librerías.
- El historial sigue siendo en memoria y el recordatorio sigue ligado al proceso; no se afirma haber
  implementado persistencia, posposición ni Firebase.
- La prueba de notificaciones usa permiso concedido mediante instrumentación. La negativa se prueba
  mediante un ContextWrapper para no matar el proceso de las propias pruebas al revocar el permiso.
- No se inventaron prompts del estudiante ni respuestas de Gemini. Este registro no satisface
  literalmente la indicación de usar Gemini si el docente exige esa herramienta específica.
- Se preserva `main` en el commit de cierre de fase 1. No se publica ni fusiona ninguna rama en esta sesión.

## Fuentes oficiales consultadas

- Android Developers: *NotificationManager*, *Create and manage notification channels* y *About notifications*.
- Android Developers: *NotificationCompat.BigTextStyle* y *Broadcasts overview*.

Referencias para reproducir la revisión:

```text
https://developer.android.com/develop/ui/compose/notifications/channels
https://developer.android.com/reference/android/app/NotificationManager
https://developer.android.com/reference/androidx/core/app/NotificationCompat.BigTextStyle
https://developer.android.com/develop/background-work/background-tasks/broadcasts
```

`IMPORTANCE_HIGH` no anula No molestar, preferencias del usuario ni decisiones del sistema.
En Android 7 se usa prioridad alta y sonido en la notificación como alternativa a los canales.
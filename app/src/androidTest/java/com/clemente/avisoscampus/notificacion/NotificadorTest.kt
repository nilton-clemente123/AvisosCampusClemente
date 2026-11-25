package com.clemente.avisoscampus.notificacion

import android.app.NotificationManager
import android.app.Notification
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.Manifest
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.clemente.avisoscampus.model.Aviso
import com.clemente.avisoscampus.model.Origen
import com.clemente.avisoscampus.model.PrioridadAviso
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificadorTest {
    @Test
    fun conservaTextoCompletoEnLaNotificacionExpandida() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            instrumentation.uiAutomation.grantRuntimePermission(
                context.packageName, Manifest.permission.POST_NOTIFICATIONS
            )
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        val texto = "Detalle del laboratorio ficticio. ".repeat(20) + "Fin del mensaje."
        try {
            assertTrue(Notificador.mostrar(
                context, Aviso(904, "Texto largo", texto, Origen.BOTON, "12:00:00")
            ))
            Thread.sleep(500)
            val notificacion = manager.activeNotifications.single { it.id == 904 }.notification
            assertEquals(texto, notificacion.extras.getCharSequence(Notification.EXTRA_TEXT).toString())
            assertEquals(texto, notificacion.extras.getCharSequence(Notification.EXTRA_BIG_TEXT).toString())
        } finally {
            manager.cancel(904)
        }
    }

    @Test
    fun sinPermisoNoNotificaNiLanzaError() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val context = object : ContextWrapper(
            InstrumentationRegistry.getInstrumentation().targetContext
        ) {
            override fun checkPermission(permission: String, pid: Int, uid: Int): Int =
                if (permission == Manifest.permission.POST_NOTIFICATIONS) {
                    PackageManager.PERMISSION_DENIED
                } else super.checkPermission(permission, pid, uid)
        }
        assertFalse(Notificador.mostrar(
            context, Aviso(903, "Sin permiso", "Texto", Origen.BOTON, "12:00:00")
        ))
    }

    @Test
    fun muestraDosAvisosSinReemplazarlos() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            instrumentation.uiAutomation.grantRuntimePermission(
                context.packageName, Manifest.permission.POST_NOTIFICATIONS
            )
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.cancelAll()
        try {
            assertTrue(Notificador.mostrar(context, Aviso(901, "Uno", "Texto", Origen.BOTON, "12:00:00")))
            assertTrue(Notificador.mostrar(context, Aviso(
                902, "Dos", "Texto", Origen.RECORDATORIO, "12:00:01", PrioridadAviso.URGENTE
            )))
            Thread.sleep(500)
            val individuales = manager.activeNotifications.filter {
                it.notification.flags and Notification.FLAG_GROUP_SUMMARY == 0
            }
            assertEquals(setOf(901, 902), individuales.map { it.id }.toSet())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assertEquals(Notificador.CANAL, individuales.single { it.id == 901 }.notification.channelId)
                assertEquals(
                    Notificador.CANAL_URGENTES,
                    individuales.single { it.id == 902 }.notification.channelId
                )
            }
            individuales.forEach {
                assertNotNull(it.notification.contentIntent)
                assertTrue(it.notification.contentIntent.isImmutable)
                assertTrue(it.notification.flags and Notification.FLAG_AUTO_CANCEL != 0)
            }
        } finally {
            manager.cancelAll()
        }
    }

    @Test
    fun canalExisteYSePuedeCrearVariasVeces() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Notificador.crearCanal(context)
        Notificador.crearCanal(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = context.getSystemService(NotificationManager::class.java)
                .getNotificationChannel(Notificador.CANAL)
            assertEquals("Avisos", canal.name.toString())
            assertEquals(NotificationManager.IMPORTANCE_DEFAULT, canal.importance)
            val urgente = context.getSystemService(NotificationManager::class.java)
                .getNotificationChannel(Notificador.CANAL_URGENTES)
            assertEquals("Urgentes", urgente.name.toString())
            assertEquals(NotificationManager.IMPORTANCE_HIGH, urgente.importance)
        }
    }
}
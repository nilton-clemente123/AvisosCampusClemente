package com.clemente.avisoscampus.notificacion

import android.app.NotificationManager
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificadorTest {
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
        }
    }
}
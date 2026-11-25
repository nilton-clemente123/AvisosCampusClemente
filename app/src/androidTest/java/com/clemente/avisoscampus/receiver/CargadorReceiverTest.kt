package com.clemente.avisoscampus.receiver

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CargadorReceiverTest {
    @Test
    fun traduceConexionYDesconexionEIgnoraOtrasAcciones() {
        val estados = mutableListOf<Boolean>()
        val receptor = CargadorReceiver { estados.add(it) }
        receptor.onReceive(null, null)
        receptor.onReceive(null, Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED))
        receptor.onReceive(null, Intent(Intent.ACTION_BATTERY_CHANGED))
        receptor.onReceive(null, Intent(Intent.ACTION_POWER_CONNECTED))
        receptor.onReceive(null, Intent(Intent.ACTION_POWER_DISCONNECTED))
        assertEquals(listOf(true, false), estados)
    }
}
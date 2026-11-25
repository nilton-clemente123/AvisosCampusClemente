package com.clemente.avisoscampus.receiver

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ModoAvionReceiverTest {
    @Test
    fun soloRespondeAModoAvionYLeeEstado() {
        val estados = mutableListOf<Boolean>()
        val receptor = ModoAvionReceiver { estados.add(it) }
        receptor.onReceive(null, null)
        receptor.onReceive(null, Intent(Intent.ACTION_POWER_CONNECTED))
        receptor.onReceive(null, Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED).putExtra("state", true))
        receptor.onReceive(null, Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED).putExtra("state", false))
        assertEquals(listOf(true, false), estados)
    }
}
package com.clemente.avisoscampus.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.clemente.avisoscampus.data.AvisosRepositoryMemoria
import com.clemente.avisoscampus.model.Aviso
import com.clemente.avisoscampus.model.Origen
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecordatorioTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Test
    fun terminaUnaSolaVezEIgnoraOtroRecordatorioActivo() {
        val vm = AvisosViewModel(AvisosRepositoryMemoria())
        val recibido = CountDownLatch(1)
        val eventos = CopyOnWriteArrayList<Aviso>()
        val scope = CoroutineScope(Dispatchers.Main.immediate)
        instrumentation.runOnMainSync {
            scope.launch { vm.eventos.collect { eventos.add(it); recibido.countDown() } }
            vm.recordarEn(1)
            assertEquals(1, vm.faltan.value)
            vm.recordarEn(10)
        }
        try {
            assertTrue(recibido.await(3, TimeUnit.SECONDS))
            instrumentation.waitForIdleSync()
            assertNull(vm.faltan.value)
            assertEquals(1, eventos.size)
            assertEquals(Origen.RECORDATORIO, vm.avisos.value.single().origen)
        } finally {
            instrumentation.runOnMainSync { vm.cancelarRecordatorio(); scope.cancel() }
        }
    }

    @Test
    fun cancelarLimpiaLaCuentaYNoGeneraAvisos() {
        val vm = AvisosViewModel(AvisosRepositoryMemoria())
        instrumentation.runOnMainSync {
            vm.recordarEn(1)
            vm.cancelarRecordatorio()
        }
        Thread.sleep(1_200)
        assertNull(vm.faltan.value)
        assertTrue(vm.avisos.value.isEmpty())
    }
}
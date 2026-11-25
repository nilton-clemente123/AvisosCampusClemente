package com.clemente.avisoscampus.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.clemente.avisoscampus.data.AvisosRepositoryMemoria
import com.clemente.avisoscampus.model.Aviso
import com.clemente.avisoscampus.model.Origen
import com.clemente.avisoscampus.model.PrioridadAviso
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AvisosViewModelEventosTest {
    @Test
    fun cargadorSoloRegistraSiEscuchaYSuSwitchEsIndependienteDelModoAvion() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val vm = AvisosViewModel(AvisosRepositoryMemoria())
        val eventos = CopyOnWriteArrayList<Aviso>()
        val scope = CoroutineScope(Dispatchers.Main.immediate)
        try {
            instrumentation.runOnMainSync {
                scope.launch { vm.eventos.collect { eventos.add(it) } }
                assertFalse(vm.escuchandoCargador.value)
                vm.alCambiarCargador(true)
                assertTrue(vm.avisos.value.isEmpty())
                vm.cambiarEscuchaCargador(true)
                assertFalse(vm.escuchandoAvion.value)
                vm.alCambiarCargador(true)
                vm.alCambiarCargador(false)
                vm.cambiarEscucha(true)
                vm.cambiarEscuchaCargador(false)
                assertTrue(vm.escuchandoAvion.value)
                vm.alCambiarCargador(true)
            }
            instrumentation.waitForIdleSync()
            assertEquals(listOf("Conectado", "Desconectado"), eventos.map { it.texto })
            assertTrue(eventos.all { it.origen == Origen.CARGADOR && it.prioridad == PrioridadAviso.NORMAL })
            assertEquals(eventos.reversed(), vm.avisos.value)
        } finally {
            instrumentation.runOnMainSync { scope.cancel() }
        }
    }

    @Test
    fun avisoLargoSeRegistraCompletoYSeEmiteSinCambiarElAvisoOriginal() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val vm = AvisosViewModel(AvisosRepositoryMemoria())
        val eventos = CopyOnWriteArrayList<Aviso>()
        val scope = CoroutineScope(Dispatchers.Main.immediate)
        try {
            instrumentation.runOnMainSync {
                scope.launch { vm.eventos.collect { eventos.add(it) } }
                vm.avisarAhora()
                vm.avisarTextoLargo()
            }
            instrumentation.waitForIdleSync()
            assertEquals(2, eventos.size)
            assertEquals("Tarea lista", eventos.first().titulo)
            assertEquals("Tu entrega fue registrada", eventos.first().texto)
            val largo = eventos.last()
            assertEquals("Aviso del campus", largo.titulo)
            assertTrue(largo.texto.length > 300)
            assertTrue(largo.texto.endsWith("no anuncia una actividad real."))
            assertEquals(Origen.BOTON, largo.origen)
            assertEquals(PrioridadAviso.NORMAL, largo.prioridad)
            assertEquals(eventos.reversed(), vm.avisos.value)
        } finally {
            instrumentation.runOnMainSync { scope.cancel() }
        }
    }
}
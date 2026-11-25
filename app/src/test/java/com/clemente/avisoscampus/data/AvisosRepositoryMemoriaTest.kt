package com.clemente.avisoscampus.data

import com.clemente.avisoscampus.model.Origen
import com.clemente.avisoscampus.model.PrioridadAviso
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AvisosRepositoryMemoriaTest {
    @Test
    fun conservaPrioridadYSigueUsandoNormalPorDefecto() {
        val repo: AvisosRepository = AvisosRepositoryMemoria()
        val normal = repo.registrar("Normal", "Texto", Origen.BOTON)
        val urgente = repo.registrar(
            "Urgente", "Texto", Origen.RECORDATORIO, PrioridadAviso.URGENTE
        )
        assertEquals(PrioridadAviso.NORMAL, normal.prioridad)
        assertEquals(PrioridadAviso.URGENTE, urgente.prioridad)
        assertEquals(urgente, repo.avisos.value.first())
    }

    @Test
    fun comienzaSinAvisos() {
        assertTrue(AvisosRepositoryMemoria().avisos.value.isEmpty())
    }

    @Test
    fun registraConIdsDistintosYMasRecientePrimero() {
        val repo = AvisosRepositoryMemoria()
        val primero = repo.registrar("Uno", "Texto uno", Origen.BOTON)
        val segundo = repo.registrar("Dos", "Texto dos", Origen.RECORDATORIO)
        assertEquals(1, primero.id)
        assertEquals(2, segundo.id)
        assertEquals(listOf(segundo, primero), repo.avisos.value)
        assertTrue(segundo.hora.matches(Regex("\\d{2}:\\d{2}:\\d{2}")))
    }
}
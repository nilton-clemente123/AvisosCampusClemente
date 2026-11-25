package com.clemente.avisoscampus.data

import com.clemente.avisoscampus.model.Origen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AvisosRepositoryMemoriaTest {
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
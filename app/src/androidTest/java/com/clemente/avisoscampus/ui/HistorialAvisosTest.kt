package com.clemente.avisoscampus.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.clemente.avisoscampus.model.Aviso
import com.clemente.avisoscampus.model.Origen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistorialAvisosTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun muestraMensajeSinAvisos() {
        compose.setContent { MaterialTheme { HistorialAvisos(emptyList()) } }
        compose.onNodeWithText("Sin avisos todavía").assertExists()
    }

    @Test
    fun limitaLaListaALosVeinteMasRecientes() {
        val avisos = (25 downTo 1).map {
            Aviso(it, "Aviso $it", "Texto $it", Origen.BOTON, "12:00:00")
        }
        compose.setContent { MaterialTheme { HistorialAvisos(avisos) } }
        compose.onNodeWithText("Aviso 25").assertExists()
        compose.onNodeWithText("Aviso 6").assertExists()
        compose.onNodeWithText("Aviso 5").assertDoesNotExist()
    }
}
package com.clemente.avisoscampus.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.clemente.avisoscampus.MainActivity
import com.clemente.avisoscampus.viewmodel.AvisosViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PantallaAvisosTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun switchesIndependientesConservanSuEstadoAlRecrearActivity() {
        val avion = compose.onNodeWithContentDescription("Escuchar el modo avión")
        val cargador = compose.onNodeWithContentDescription("Escuchar el cargador")
        avion.performScrollTo().assertIsOff()
        cargador.performScrollTo().assertIsOff().performClick().assertIsOn()
        avion.performScrollTo().assertIsOff().performClick().assertIsOn()
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        cargador.performScrollTo().assertIsOn().performClick().assertIsOff()
        avion.performScrollTo().assertIsOn().performClick().assertIsOff()
    }

    @Test
    fun recrearActivityConservaHistorialYRecordatorio() {
        compose.onNodeWithText("Mostrar aviso").performClick()
        lateinit var original: AvisosViewModel
        compose.activityRule.scenario.onActivity {
            original = ViewModelProvider(it)[AvisosViewModel::class.java]
            assertEquals(1, original.avisos.value.size)
            original.recordarEn()
        }
        try {
            compose.activityRule.scenario.recreate()
            compose.waitForIdle()
            compose.activityRule.scenario.onActivity {
                val actual = ViewModelProvider(it)[AvisosViewModel::class.java]
                assertSame(original, actual)
                assertEquals(1, actual.avisos.value.size)
                assertNotNull(actual.faltan.value)
            }
        } finally {
            compose.activityRule.scenario.onActivity { original.cancelarRecordatorio() }
        }
    }
}
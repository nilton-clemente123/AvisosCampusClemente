package com.clemente.avisoscampus.viewmodel

import com.clemente.avisoscampus.data.AvisosRepositoryMemoria
import com.clemente.avisoscampus.model.PermisoUi
import org.junit.Assert.assertEquals
import org.junit.Test

class AvisosViewModelTest {
    @Test
    fun permisoEmpiezaSinPedirYCambiaSegunRespuesta() {
        val vm = AvisosViewModel(AvisosRepositoryMemoria())
        assertEquals(PermisoUi.SinPedir, vm.permiso.value)
        vm.permisoRespondido(false)
        assertEquals(PermisoUi.Denegado, vm.permiso.value)
        vm.permisoRespondido(true)
        assertEquals(PermisoUi.Concedido, vm.permiso.value)
    }
}
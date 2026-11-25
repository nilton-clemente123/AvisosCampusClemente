package com.clemente.avisoscampus.viewmodel

import com.clemente.avisoscampus.data.AvisosRepositoryMemoria
import com.clemente.avisoscampus.model.PermisoUi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AvisosViewModelTest {
    @Test
    fun mensajeSeLimpiaSoloCuandoSeConcedeElPermiso() {
        val vm = AvisosViewModel(AvisosRepositoryMemoria())
        vm.noSePudoMostrar()
        val esperado = "Sin permiso: el aviso se guardó en el historial, pero no se mostró"
        assertEquals(esperado, vm.mensaje.value)
        vm.permisoRespondido(false)
        assertEquals(esperado, vm.mensaje.value)
        vm.permisoRespondido(true)
        assertNull(vm.mensaje.value)
    }

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
package com.clemente.avisoscampus.data

import com.clemente.avisoscampus.model.Aviso
import com.clemente.avisoscampus.model.Origen
import com.clemente.avisoscampus.model.PrioridadAviso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AvisosRepository {
    val avisos: StateFlow<List<Aviso>>
    fun registrar(
        titulo: String,
        texto: String,
        origen: Origen,
        prioridad: PrioridadAviso = PrioridadAviso.NORMAL
    ): Aviso
}

class AvisosRepositoryMemoria : AvisosRepository {
    private val _avisos = MutableStateFlow<List<Aviso>>(emptyList())
    override val avisos: StateFlow<List<Aviso>> = _avisos.asStateFlow()
    private var siguiente = 1

    override fun registrar(
        titulo: String,
        texto: String,
        origen: Origen,
        prioridad: PrioridadAviso
    ): Aviso {
        val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val aviso = Aviso(siguiente++, titulo, texto, origen, hora, prioridad)
        _avisos.value = listOf(aviso) + _avisos.value
        return aviso
    }
}
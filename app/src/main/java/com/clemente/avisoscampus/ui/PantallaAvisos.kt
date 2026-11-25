package com.clemente.avisoscampus.ui

import android.Manifest
import android.os.Build
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.clemente.avisoscampus.model.PermisoUi
import com.clemente.avisoscampus.notificacion.Notificador
import com.clemente.avisoscampus.receiver.ModoAvionReceiver
import com.clemente.avisoscampus.viewmodel.AvisosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAvisos(vm: AvisosViewModel) {
    val contexto = LocalContext.current
    val permiso by vm.permiso.collectAsState()
    val faltan by vm.faltan.collectAsState()
    val escuchando by vm.escuchandoAvion.collectAsState()
    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        vm.permisoRespondido(it)
    }
    LaunchedEffect(Unit) {
        if (Notificador.tienePermiso(contexto)) vm.permisoRespondido(true)
    }
    LaunchedEffect(vm) {
        vm.eventos.collect { aviso -> Notificador.mostrar(contexto, aviso) }
    }
    DisposableEffect(contexto, escuchando) {
        val receptor = if (escuchando) {
            ModoAvionReceiver(vm::alCambiarModoAvion).also {
                ContextCompat.registerReceiver(
                    contexto, it, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED),
                    ContextCompat.RECEIVER_NOT_EXPORTED
                )
            }
        } else null
        onDispose {
            receptor?.let(contexto::unregisterReceiver)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AvisosCampus") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaAvisos("Permiso de notificaciones") {
                val (etiqueta, color) = when (permiso) {
                    PermisoUi.SinPedir -> "Sin pedir" to Color.Gray
                    PermisoUi.Concedido -> "Permitido" to Color(0xFF1B7A3A)
                    PermisoUi.Denegado -> "Denegado" to MaterialTheme.colorScheme.error
                }
                Text(etiqueta, color = color)
                Button(onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        vm.permisoRespondido(true)
                    }
                }) { Text("Permitir notificaciones") }
            }
            TarjetaAvisos("Avisos") {
                Button(onClick = vm::avisarAhora) { Text("Mostrar aviso") }
            }
            TarjetaAvisos("Recordatorio") {
                Button(onClick = { vm.recordarEn() }, enabled = faltan == null) {
                    Text("Recordar en 10 s")
                }
                OutlinedButton(onClick = vm::cancelarRecordatorio, enabled = faltan != null) {
                    Text("Cancelar")
                }
                faltan?.let { Text("Faltan $it s") }
            }
            TarjetaAvisos("Receptor de anuncios") {
                Text("Escuchar el modo avión")
                Switch(checked = escuchando, onCheckedChange = vm::cambiarEscucha)
            }
            TarjetaAvisos("Historial") { Text("Sin avisos todavía") }
        }
    }
}

@Composable
internal fun TarjetaAvisos(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            contenido()
        }
    }
}
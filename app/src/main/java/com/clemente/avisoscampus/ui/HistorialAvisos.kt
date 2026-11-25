package com.clemente.avisoscampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.clemente.avisoscampus.model.Aviso

@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    TarjetaAvisos("Historial") {
        if (avisos.isEmpty()) {
            Text("Sin avisos todavía")
        } else {
            avisos.take(20).forEachIndexed { indice, aviso ->
                if (indice > 0) HorizontalDivider()
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        aviso.origen.texto,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(aviso.titulo, style = MaterialTheme.typography.titleSmall)
                    Text(aviso.texto, style = MaterialTheme.typography.bodyMedium)
                    Text(aviso.hora, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
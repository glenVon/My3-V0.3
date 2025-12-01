package com.egon.my3.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DebugScreen(errorMessage: String, stackTrace: String?, onContinueInMemory: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        val expanded = remember { mutableStateOf(false) }
        var showDev by remember { mutableStateOf(false) }
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Error al inicializar la base de datos:")
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            if (!stackTrace.isNullOrEmpty()) {
                Text(text = if (expanded.value) stackTrace else "(pulsa Mostrar para ver stacktrace)", modifier = Modifier.padding(top = 8.dp))
            }
            Button(onClick = { expanded.value = !expanded.value }, modifier = Modifier.padding(top = 12.dp)) {
                Text(if (expanded.value) "Ocultar" else "Mostrar")
            }
            Button(onClick = onContinueInMemory, modifier = Modifier.padding(top = 12.dp)) {
                Text("Continuar con repositorio en memoria")
            }
            Button(onClick = { showDev = !showDev }, modifier = Modifier.padding(top = 12.dp)) {
                Text(if (showDev) "Ocultar Ajustes Dev" else "Ajustes de red (Dev)")
            }
            if (showDev) {
                Spacer(modifier = Modifier.height(8.dp))
                DevSettingsContent(onClose = { showDev = false })
            }
        }
    }
}

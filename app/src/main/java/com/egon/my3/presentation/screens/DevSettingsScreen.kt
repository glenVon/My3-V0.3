package com.egon.my3.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Row
import com.egon.my3.data.network.RetrofitClient
import com.egon.my3.network.NetworkConfig

@Composable
fun DevSettingsScreen(navController: NavController?) {
    DevSettingsContent(onClose = { navController?.navigateUp() })
}

@Composable
fun DevSettingsContent(onClose: (() -> Unit)? = null, onSaved: (() -> Unit)? = null) {
    val context = LocalContext.current
    var url by remember { mutableStateOf(NetworkConfig.BASE_URL) }
    var useGateway by remember { mutableStateOf(NetworkConfig.useGatewayDefault) }
    var usePoke by remember { mutableStateOf(com.egon.my3.network.PokeApiClient.useNetworkConfigForPoke) }
    var message by remember { mutableStateOf<String?>(null) }
    var effectiveBaseUrl by remember { mutableStateOf<String?>(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Ajustes de red (Dev)")
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = url, onValueChange = { url = it }, label = { Text("Base URL (completa, ej. http://10.0.2.2:8080/)") })
            Spacer(modifier = Modifier.height(8.dp))
            RowWithSwitch(label = "Usar Gateway por defecto", checked = useGateway, onCheckedChange = { useGateway = it })
            Spacer(modifier = Modifier.height(16.dp))
            RowWithSwitch(label = "Poke API use NetworkConfig", checked = usePoke, onCheckedChange = { usePoke = it })
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                // save
                NetworkConfig.setOverride(url)
                NetworkConfig.useGatewayDefault = useGateway
                NetworkConfig.saveToPreferences(context)
                RetrofitClient.recreate()
                com.egon.my3.network.PokeApiClient.resetInstance()
                com.egon.my3.network.PokeApiClient.useNetworkConfigForPoke = usePoke
                message = "Guardado y Retrofit recreado"
                effectiveBaseUrl = com.egon.my3.data.network.RetrofitClient.getCurrentBaseUrlForTesting()
                onSaved?.invoke()
            }) {
                Text("Guardar y recrear Retrofit")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                // clear override
                NetworkConfig.clearOverride()
                NetworkConfig.useGatewayDefault = true
                NetworkConfig.saveToPreferences(context)
                RetrofitClient.recreate()
                com.egon.my3.network.PokeApiClient.resetInstance()
                com.egon.my3.network.PokeApiClient.useNetworkConfigForPoke = false
                url = NetworkConfig.BASE_URL
                useGateway = NetworkConfig.useGatewayDefault
                message = "Override limpiado y Retrofit recreado"
                onSaved?.invoke()
            }) {
                Text("Limpiar override y restaurar por defecto")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onClose?.invoke() }) {
                Text("Cerrar")
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (message != null) {
                Text(message!!)
            }
            if (effectiveBaseUrl != null) {
                Text("Base actual usada por Retrofit: ${effectiveBaseUrl}")
            }
        }
    }
}

@Composable
fun RowWithSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Text(label)
        Spacer(modifier = Modifier.height(8.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

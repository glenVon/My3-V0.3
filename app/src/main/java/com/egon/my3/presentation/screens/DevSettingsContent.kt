package com.egon.my3.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.egon.my3.data.network.RetrofitClient
import com.egon.my3.network.NetworkConfig
import com.egon.my3.network.PokeApiClient

@Composable
fun DevSettingsContent(onClose: () -> Unit) {
    val context = LocalContext.current
    var baseUrl by remember { mutableStateOf(NetworkConfig.BASE_URL) }
    var useGateway by remember { mutableStateOf(NetworkConfig.useGatewayDefault) }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text("Ajustes de Red", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = baseUrl,
            onValueChange = { baseUrl = it },
            label = { Text("Base URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = useGateway, onCheckedChange = { useGateway = it })
            Text("Usar Gateway (puerto 8080)")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                // Save settings
                NetworkConfig.setOverride(baseUrl)
                NetworkConfig.useGatewayDefault = useGateway
                NetworkConfig.saveToPreferences(context)
                
                // Recreate clients immediately so changes take effect
                try {
                    RetrofitClient.recreate()
                    PokeApiClient.resetInstance()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                
                onClose()
            }) {
                Text("Guardar y Reiniciar")
            }
            Button(onClick = {
                NetworkConfig.clearOverride()
                NetworkConfig.saveToPreferences(context)
                
                // Refresh UI and recreate clients
                try {
                    RetrofitClient.recreate()
                    PokeApiClient.resetInstance()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                baseUrl = NetworkConfig.BASE_URL 
            }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                Text("Resetear")
            }
        }
    }
}

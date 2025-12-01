package com.egon.my3

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.egon.my3.data.network.RetrofitClient
import com.egon.my3.network.DebugBroadcastReceiver
import com.egon.my3.network.NetworkConfig
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for NetworkConfig and runtime override behavior.
 */
@RunWith(AndroidJUnit4::class)
class NetworkConfigInstrumentedTest {
    @Test
    fun saveLoadPreferencesAndRetrofitRecreate() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Ensure clean state
        NetworkConfig.clearOverride()
        NetworkConfig.useGatewayDefault = true
        NetworkConfig.saveToPreferences(context)

        // Load to ensure no override
        NetworkConfig.loadFromPreferences(context)
        // Verificamos que empiece por http (puede ser gateway o user service default)
        assertTrue("Base URL should start with http", NetworkConfig.BASE_URL.startsWith("http"))

        // Set an override and persist
        val testBase = "http://192.168.1.42:8080/"
        NetworkConfig.setOverride(testBase)
        NetworkConfig.useGatewayDefault = false
        NetworkConfig.saveToPreferences(context)

        // Re-load in memory and recreate retrofit
        NetworkConfig.loadFromPreferences(context)
        RetrofitClient.recreate()
        val current = RetrofitClient.getCurrentBaseUrlForTesting()
        assertEquals("Base URL should match the override", testBase, current)
    }

    @Test
    fun debugBroadcastReceiver_changesBaseUrl() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Clear first
        NetworkConfig.clearOverride()
        NetworkConfig.saveToPreferences(context)
        RetrofitClient.recreate()

        // Prepare intent
        val expectedUrl = "http://10.123.123.12:8080/"
        val intent = Intent(DebugBroadcastReceiver.ACTION_SET_BASE_URL).apply {
            putExtra(DebugBroadcastReceiver.EXTRA_BASE_URL, expectedUrl)
            putExtra(DebugBroadcastReceiver.EXTRA_USE_GATEWAY, false)
        }

        // Directamente instanciamos y llamamos al receiver para que la prueba sea síncrona
        // Evitamos context.sendBroadcast(intent) porque es asíncrono y el test fallaría por condiciones de carrera
        val receiver = DebugBroadcastReceiver()
        receiver.onReceive(context, intent)

        // Assert
        // El receiver ya debió actualizar NetworkConfig y llamar a RetrofitClient.recreate()
        val current = RetrofitClient.getCurrentBaseUrlForTesting()
        assertEquals("Broadcast receiver should update Retrofit URL", expectedUrl, current)
    }
}

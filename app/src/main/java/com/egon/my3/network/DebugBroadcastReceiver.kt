package com.egon.my3.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.egon.my3.data.network.RetrofitClient
// import com.egon.my3.BuildConfig // Comentado temporalmente para permitir compilación

class DebugBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_SET_BASE_URL = "com.egon.my3.action.SET_BASE_URL"
        const val EXTRA_BASE_URL = "base_url"
        const val EXTRA_USE_GATEWAY = "use_gateway"
        const val EXTRA_CLEAR = "clear"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            // Verificamos si es DEBUG. Comentado temporalmente para solucionar error de build.
            // if (!BuildConfig.DEBUG) return
            
            if (intent == null || intent.action != ACTION_SET_BASE_URL) return
            
            val baseUrl = intent.getStringExtra(EXTRA_BASE_URL)
            val shouldClear = intent.getBooleanExtra(EXTRA_CLEAR, false)
            val useGateway = intent.getBooleanExtra(EXTRA_USE_GATEWAY, NetworkConfig.useGatewayDefault)

            if (context != null) {
                if (shouldClear) {
                    NetworkConfig.clearOverride()
                } else if (!baseUrl.isNullOrBlank()) {
                    NetworkConfig.setOverride(baseUrl)
                }
                NetworkConfig.useGatewayDefault = useGateway
                NetworkConfig.saveToPreferences(context)
                
                // Reiniciamos clientes de red
                try {
                    RetrofitClient.recreate()
                    PokeApiClient.resetInstance()
                } catch (e: Exception) {
                    Log.e("DebugBroadcastReceiver", "Error resetting clients", e)
                }
                
                Log.i("DebugBroadcastReceiver", "NetworkConfig updated: ${NetworkConfig.BASE_URL}")
            }
        } catch (t: Throwable) {
            Log.e("DebugBroadcastReceiver", "Error processing debug broadcast", t)
        }
    }
}

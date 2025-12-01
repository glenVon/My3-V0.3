package com.egon.my3.network

object NetworkConfig {
    // Gateway base url (recommended for development with gateway-service)
    const val GATEWAY_BASE = "http://10.0.2.2:8080/"
    // Direct service urls (if you need to hit services without gateway)
    const val USER_SERVICE_BASE = "http://10.0.2.2:8081/"
    const val PRODUCT_SERVICE_BASE = "http://10.0.2.2:8082/"

    // Toggle for default selection
    var useGatewayDefault = true

    // Runtime override (in-memory); persisted via SharedPreferences by helper methods
    private var overrideBaseUrl: String? = null

    val BASE_URL: String
        get() = normalizeUrl(overrideBaseUrl ?: if (useGatewayDefault) GATEWAY_BASE else USER_SERVICE_BASE) ?: GATEWAY_BASE

    // Setter - runtime override (non-persistent)
    fun setOverride(url: String?) {
        overrideBaseUrl = if (url.isNullOrBlank()) null else normalizeUrl(url)
    }

    fun clearOverride() {
        overrideBaseUrl = null
    }

    fun normalizeUrl(url: String?): String? {
        if (url == null) return null
        var u = url.trim()
        if (!u.endsWith("/")) u += "/"
        return u
    }

    // Persist override in SharedPreferences
    fun loadFromPreferences(context: android.content.Context) {
        try {
            val prefs = context.getSharedPreferences("network_prefs", android.content.Context.MODE_PRIVATE)
            val url = prefs.getString("override_base_url", null)
            overrideBaseUrl = normalizeUrl(url)
            useGatewayDefault = prefs.getBoolean("use_gateway_default", true)
        } catch (_: Exception) {
            // ignore
        }
    }

    fun saveToPreferences(context: android.content.Context) {
        try {
            val prefs = context.getSharedPreferences("network_prefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().putString("override_base_url", overrideBaseUrl).putBoolean("use_gateway_default", useGatewayDefault).apply()
        } catch (_: Exception) {
            // ignore
        }
    }
}

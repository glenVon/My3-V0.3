package com.egon.my3.data.network

import com.egon.my3.network.NetworkConfig
import com.egon.my3.network.OkHttpProvider
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.egon.my3.data.network.ApiService

object RetrofitClient {
    private const val API_SUFFIX = ""

    @Volatile
    private var retrofit: Retrofit? = null

    @Volatile
    private var apiService: ApiService? = null

    // Propiedad pública para acceder a la instancia
    val instance: ApiService
        get() = getServiceInstance()

    private fun normalizeBaseUrl(url: String?): String {
        var u = url ?: NetworkConfig.BASE_URL
        if (!u.endsWith("/")) u += "/"
        return u
    }

    private fun buildRetrofit(): Retrofit {
        val base = normalizeBaseUrl(NetworkConfig.BASE_URL)
        val okHttp = OkHttpProvider.create()
        return Retrofit.Builder()
            .baseUrl(base)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Renombrado para evitar conflicto de firma con el getter generado de 'instance'
    @Synchronized
    private fun getServiceInstance(): ApiService {
        if (apiService == null) {
            retrofit = buildRetrofit()
            apiService = retrofit!!.create(ApiService::class.java)
        }
        return apiService!!
    }

    @Synchronized
    fun recreate() {
        retrofit = buildRetrofit()
        apiService = retrofit!!.create(ApiService::class.java)
    }

    // For testing: return the base url used by the current retrofit instance
    fun getCurrentBaseUrlForTesting(): String? = retrofit?.baseUrl()?.toString()
}

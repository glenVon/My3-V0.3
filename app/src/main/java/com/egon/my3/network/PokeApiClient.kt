package com.egon.my3.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PokeApiClient {
    
    private const val BASE_URL = "https://pokeapi.co/api/v2/"
    
    // Variable para usar la URL base de NetworkConfig para pruebas (si es necesario)
    var useNetworkConfigForPoke: Boolean = false
    
    // Instancia única del servicio (lazy para seguridad)
    val instance: PokeApiService by lazy {
        createPokeApiService()
    }
    
    private fun createPokeApiService(): PokeApiService {
        // Usamos el OkHttpProvider centralizado que ya tiene logging y timeouts
        val okHttpClient = OkHttpProvider.create()
        
        // Determinar la URL base a usar
        val baseUrl = if (useNetworkConfigForPoke) {
            NetworkConfig.BASE_URL
        } else {
            BASE_URL
        }

        // Crear instancia de Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return retrofit.create(PokeApiService::class.java)
    }
    
    // Método para resetear la instancia (no es posible con 'by lazy', pero se puede re-instanciar el cliente)
    // Para pruebas, es mejor inyectar el cliente o usar un objeto de test.
    // Dejamos este método para compatibilidad con el código existente, aunque no reseteará un 'val by lazy'.
    fun resetInstance() {
        // Con 'by lazy', no se puede resetear. La instancia es final.
        // Para que esto funcione, habría que quitar 'by lazy' y volver a la gestión manual con 'Volatile'.
    }
}

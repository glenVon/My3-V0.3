package com.egon.my3.network

// import com.egon.my3.BuildConfig // Comentado temporalmente si no resuelve
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpProvider {
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    fun create(additionalInterceptors: List<Interceptor> = emptyList()): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            // Usamos nivel BODY por defecto para depuración, cambiar a NONE para prod manualmente si BuildConfig falla
            level = HttpLoggingInterceptor.Level.BODY 
        }

        val builder = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "My3-App/1.0")
                chain.proceed(requestBuilder.build())
            }

        additionalInterceptors.forEach { builder.addInterceptor(it) }

        return builder.build()
    }
}

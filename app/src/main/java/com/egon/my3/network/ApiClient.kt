package com.egon.my3.network

import com.egon.my3.data.network.RetrofitClient
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    /**
     * Create a Retrofit instance using a specific baseUrl or falling back to NetworkConfig.BASE_URL
     */
    fun create(baseUrl: String = NetworkConfig.BASE_URL, additionalInterceptors: List<Interceptor> = emptyList()): Retrofit {
        val okHttpClient = OkHttpProvider.create(additionalInterceptors)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

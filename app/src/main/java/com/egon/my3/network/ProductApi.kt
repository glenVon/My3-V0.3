package com.egon.my3.network

import com.egon.my3.data.models.Product
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProductApi {
    @GET("/products")
    suspend fun getAll(): List<Product>

    @GET("/products/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Product>

    @POST("/products")
    suspend fun create(@Body product: Product): Product

    @DELETE("/products/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Unit>
}

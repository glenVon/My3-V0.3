package com.egon.my3.data.network

import com.egon.my3.data.models.Product
import com.egon.my3.data.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// For login, it's common to have a specific data class for credentials
data class LoginRequest(val email: String, val password: String)

interface ApiService {

    // Product endpoints
    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Product?

    @POST("products")
    suspend fun addProduct(@Body product: Product): Product

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: Product): Product

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>

    // User endpoints
    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): User?

    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): User?

    @GET("users/byEmail/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): User?

    @POST("users")
    suspend fun addUser(@Body user: User): User

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: User): User

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>
}

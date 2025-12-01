package com.egon.my3.network

import com.egon.my3.data.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @GET("/users")
    suspend fun getAll(): List<User>

    @GET("/users/{id}")
    suspend fun getById(@Path("id") id: Long): Response<User>

    @POST("/users")
    suspend fun create(@Body user: User): User

    @POST("/users/login")
    suspend fun login(@Body credentials: Map<String, String>): Response<User>

    @PUT("/users/{id}")
    suspend fun update(@Path("id") id: Int, @Body user: User): User

    @DELETE("/users/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Void>

    @GET("/users/byEmail/{email}")
    suspend fun getByEmail(@Path("email") email: String): Response<User>
}

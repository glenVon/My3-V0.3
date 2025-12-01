package com.egon.my3.network

import com.egon.my3.data.models.Pokemon
import com.egon.my3.data.models.PokemonListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    
    // Obtener lista de Pokémon (paginada)
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<PokemonListResponse>
    
    // Obtener Pokémon por ID
    @GET("pokemon/{id}")
    suspend fun getPokemonById(@Path("id") id: Int): Response<Pokemon>
    
    // Obtener Pokémon por nombre
    @GET("pokemon/{name}")
    suspend fun getPokemonByName(@Path("name") name: String): Response<Pokemon>
    
    // Buscar Pokémon (puedes expandir según necesites)
    @GET("pokemon")
    suspend fun searchPokemon(
        @Query("limit") limit: Int = 1000 // Para búsqueda, obtener muchos
    ): Response<PokemonListResponse>
}

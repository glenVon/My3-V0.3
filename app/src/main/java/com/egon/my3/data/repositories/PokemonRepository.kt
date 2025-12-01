package com.egon.my3.data.repositories

import android.util.Log
import com.egon.my3.data.models.Pokemon
import com.egon.my3.data.models.PokemonListResponse
import com.egon.my3.data.models.PokemonListItem
import com.egon.my3.network.PokeApiClient
import com.egon.my3.network.PokeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PokemonRepository(
    // Corregido: Usar la propiedad 'instance' en lugar del método 'getInstance()'
    private val pokeApiService: PokeApiService = PokeApiClient.instance
) {
    private val TAG = "PokemonRepository"
    
    // Obtener lista de Pokémon paginada
    suspend fun getPokemonList(limit: Int = 20, offset: Int = 0): Result<PokemonListResponse> = 
        withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching Pokémon list: limit=$limit, offset=$offset")
            val response = pokeApiService.getPokemonList(limit, offset)
            
            if (response.isSuccessful) {
                val pokemonList = response.body()
                if (pokemonList != null) {
                    Log.d(TAG, "Successfully fetched ${pokemonList.results.size} Pokémon")
                    Result.success(pokemonList)
                } else {
                    Log.e(TAG, "Empty response body")
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                Log.e(TAG, "API Error: ${response.code()} - ${response.message()}")
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Obtener Pokémon por ID
    suspend fun getPokemonById(pokemonId: Int): Result<Pokemon> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching Pokémon by ID: $pokemonId")
            val response = pokeApiService.getPokemonById(pokemonId)
            
            if (response.isSuccessful) {
                val pokemon = response.body()
                if (pokemon != null) {
                    Log.d(TAG, "Successfully fetched Pokémon: ${pokemon.name}")
                    Result.success(pokemon)
                } else {
                    Result.failure(Exception("Pokémon not found"))
                }
            } else {
                Log.e(TAG, "API Error: ${response.code()} - ${response.message()}")
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Obtener Pokémon por nombre
    suspend fun getPokemonByName(pokemonName: String): Result<Pokemon> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching Pokémon by name: $pokemonName")
            val response = pokeApiService.getPokemonByName(pokemonName.lowercase())
            
            if (response.isSuccessful) {
                val pokemon = response.body()
                if (pokemon != null) {
                    Log.d(TAG, "Successfully fetched Pokémon: ${pokemon.name}")
                    Result.success(pokemon)
                } else {
                    Result.failure(Exception("Pokémon '$pokemonName' not found"))
                }
            } else {
                Log.e(TAG, "API Error: ${response.code()} - ${response.message()}")
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Buscar Pokémon por nombre (en lista)
    suspend fun searchPokemon(query: String): Result<List<PokemonListItem>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Searching Pokémon: $query")
            
            // Primero obtenemos una lista grande para buscar localmente
            val response = pokeApiService.searchPokemon(limit = 1000)
            
            if (response.isSuccessful) {
                val pokemonList = response.body()
                if (pokemonList != null) {
                    // Filtrar localmente por nombre
                    val filteredResults = pokemonList.results.filter { 
                        it.name.contains(query.lowercase(), ignoreCase = true) 
                    }
                    Log.d(TAG, "Found ${filteredResults.size} Pokémon matching '$query'")
                    Result.success(filteredResults)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Search error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Obtener múltiples Pokémon por IDs
    suspend fun getMultiplePokemon(ids: List<Int>): Result<List<Pokemon>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching multiple Pokémon: $ids")
            val results = mutableListOf<Pokemon>()
            
            for (id in ids) {
                getPokemonById(id)
                    .onSuccess { pokemon -> results.add(pokemon) }
                    .onFailure { e ->
                        Log.w(TAG, "Failed to fetch Pokémon ID $id: ${e.message}")
                    }
            }
            
            Log.d(TAG, "Successfully fetched ${results.size} out of ${ids.size} Pokémon")
            Result.success(results)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching multiple Pokémon: ${e.message}", e)
            Result.failure(e)
        }
    }
}

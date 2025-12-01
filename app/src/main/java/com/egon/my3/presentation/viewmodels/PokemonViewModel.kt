package com.egon.my3.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egon.my3.data.models.Pokemon
import com.egon.my3.data.models.PokemonListItem
import com.egon.my3.data.models.PokemonListResponse
import com.egon.my3.data.repositories.PokemonRepository
import kotlinx.coroutines.launch

class PokemonViewModel(
    private val pokemonRepository: PokemonRepository
) : ViewModel() {
    
    // Estados de la UI
    var pokemonList by mutableStateOf<List<PokemonListItem>>(emptyList())
        private set
    
    var selectedPokemon by mutableStateOf<Pokemon?>(null)
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var searchResults by mutableStateOf<List<PokemonListItem>>(emptyList())
        private set
    
    var currentOffset by mutableStateOf(0)
        private set
    
    val limit = 20
    
    // Cargar lista de Pokémon
    fun loadPokemonList(loadMore: Boolean = false) {
        if (isLoading) return
        
        isLoading = true
        errorMessage = null
        
        val offset = if (loadMore) currentOffset else 0
        
        viewModelScope.launch {
            val result = pokemonRepository.getPokemonList(limit = limit, offset = offset)
            isLoading = false
            
            result.onSuccess { response ->
                if (loadMore) {
                    // Agregar a la lista existente
                    pokemonList = pokemonList + response.results
                    currentOffset += limit
                } else {
                    // Nueva lista
                    pokemonList = response.results
                    currentOffset = limit
                }
                errorMessage = null
            }.onFailure { exception ->
                errorMessage = "Error al cargar Pokémon: ${exception.message}"
                if (!loadMore) {
                    pokemonList = emptyList()
                }
            }
        }
    }
    
    // Cargar más Pokémon (paginación)
    fun loadMorePokemon() {
        loadPokemonList(loadMore = true)
    }
    
    // Cargar Pokémon por ID
    fun loadPokemonById(pokemonId: Int) {
        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            val result = pokemonRepository.getPokemonById(pokemonId)
            isLoading = false
            
            result.onSuccess { pokemon ->
                selectedPokemon = pokemon
                errorMessage = null
            }.onFailure { exception ->
                errorMessage = "Error al cargar Pokémon: ${exception.message}"
                selectedPokemon = null
            }
        }
    }
    
    // Cargar Pokémon por nombre
    fun loadPokemonByName(pokemonName: String) {
        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            val result = pokemonRepository.getPokemonByName(pokemonName)
            isLoading = false
            
            result.onSuccess { pokemon ->
                selectedPokemon = pokemon
                errorMessage = null
            }.onFailure { exception ->
                errorMessage = "Pokémon '$pokemonName' no encontrado"
                selectedPokemon = null
            }
        }
    }
    
    // Buscar Pokémon
    fun searchPokemon(query: String) {
        if (query.length < 2) {
            searchResults = emptyList()
            return
        }
        
        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            val result = pokemonRepository.searchPokemon(query)
            isLoading = false
            
            result.onSuccess { results ->
                searchResults = results
                errorMessage = null
            }.onFailure { exception ->
                errorMessage = "Error en búsqueda: ${exception.message}"
                searchResults = emptyList()
            }
        }
    }
    
    // Limpiar estados
    fun clearError() {
        errorMessage = null
    }
    
    fun clearSelectedPokemon() {
        selectedPokemon = null
    }
    
    fun clearSearch() {
        searchResults = emptyList()
    }
}

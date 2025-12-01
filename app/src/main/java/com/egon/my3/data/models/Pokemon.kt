package com.egon.my3.data.models

import com.google.gson.annotations.SerializedName

// Respuesta de lista de Pokémon
data class PokemonListResponse(
    @SerializedName("count")
    val count: Int,
    
    @SerializedName("next")
    val next: String?,
    
    @SerializedName("previous")
    val previous: String?,
    
    @SerializedName("results")
    val results: List<PokemonListItem>
)

// Item de lista de Pokémon
data class PokemonListItem(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("url")
    val url: String
) {
    // Extraer ID desde la URL
    fun extractId(): Int {
        return url.split("/").filter { it.isNotEmpty() }.last().toIntOrNull() ?: 0
    }
    
    // Obtener URL de imagen oficial
    fun getImageUrl(): String {
        val id = extractId()
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
    }
}

// Detalles completos de un Pokémon
data class Pokemon(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("height")
    val height: Int, // en decímetros
    
    @SerializedName("weight")
    val weight: Int, // en hectogramos
    
    @SerializedName("base_experience")
    val baseExperience: Int,
    
    @SerializedName("types")
    val types: List<PokemonType>,
    
    @SerializedName("abilities")
    val abilities: List<PokemonAbility>,
    
    @SerializedName("stats")
    val stats: List<PokemonStat>,
    
    @SerializedName("sprites")
    val sprites: PokemonSprites
) {
    // Propiedades computadas
    val heightInMeters: Double get() = height / 10.0
    val weightInKg: Double get() = weight / 10.0
    
    // Obtener tipos como lista de strings
    fun getTypeNames(): List<String> = types.map { it.type.name }
    
    // Obtener URL de imagen principal
    fun getOfficialArtwork(): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
    }
}

// Tipo de Pokémon
data class PokemonType(
    @SerializedName("slot")
    val slot: Int,
    
    @SerializedName("type")
    val type: NamedApiResource
)

// Habilidad de Pokémon
data class PokemonAbility(
    @SerializedName("is_hidden")
    val isHidden: Boolean,
    
    @SerializedName("slot")
    val slot: Int,
    
    @SerializedName("ability")
    val ability: NamedApiResource
)

// Estadística de Pokémon
data class PokemonStat(
    @SerializedName("base_stat")
    val baseStat: Int,
    
    @SerializedName("effort")
    val effort: Int,
    
    @SerializedName("stat")
    val stat: NamedApiResource
)

// Sprites (imágenes) del Pokémon
data class PokemonSprites(
    @SerializedName("front_default")
    val frontDefault: String?,
    
    @SerializedName("front_shiny")
    val frontShiny: String?,
    
    @SerializedName("other")
    val other: OtherSprites?
) {
    fun getBestImage(): String {
        return other?.officialArtwork?.frontDefault ?: frontDefault ?: ""
    }
}

data class OtherSprites(
    @SerializedName("official-artwork")
    val officialArtwork: OfficialArtwork?
)

data class OfficialArtwork(
    @SerializedName("front_default")
    val frontDefault: String?
)

// Recurso con nombre (usado en tipos, habilidades, etc.)
data class NamedApiResource(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("url")
    val url: String
)

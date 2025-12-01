package com.egon.my3.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.egon.my3.presentation.viewmodels.PokemonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonScreen(
    navController: NavHostController,
    pokemonViewModel: PokemonViewModel
) {
    // Estados locales
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    
    // Observar estados del ViewModel
    // Se usa '=' en lugar de 'by' porque las propiedades en el ViewModel ya están delegadas
    val pokemonList = pokemonViewModel.pokemonList
    val searchResults = pokemonViewModel.searchResults
    val isLoading = pokemonViewModel.isLoading
    val errorMessage = pokemonViewModel.errorMessage
    
    val listState = rememberLazyListState()
    
    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        pokemonViewModel.loadPokemonList()
    }
    
    // Detectar cuando llegar al final para cargar más
    LaunchedEffect(listState.layoutInfo.visibleItemsInfo) {
        if (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == pokemonList.size - 5) {
            pokemonViewModel.loadMorePokemon()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokédex") },
                actions = {
                    IconButton(
                        onClick = { pokemonViewModel.loadPokemonList() },
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recargar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de búsqueda
            SearchBar(
                query = searchQuery,
                onQueryChange = { query ->
                    searchQuery = query
                    if (query.isNotEmpty()) {
                        isSearching = true
                        pokemonViewModel.searchPokemon(query)
                    } else {
                        isSearching = false
                        pokemonViewModel.clearSearch()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            
            // Mostrar mensaje de error
            errorMessage?.let { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Mostrar loading
            if (isLoading && pokemonList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Lista de Pokémon
                val displayList = if (isSearching && searchQuery.isNotEmpty()) {
                    searchResults
                } else {
                    pokemonList
                }
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(displayList) { pokemonItem ->
                        PokemonListItem(
                            pokemonItem = pokemonItem,
                            onClick = {
                                // Navegar a detalles o mostrar en esta pantalla
                                pokemonViewModel.loadPokemonById(pokemonItem.extractId())
                            }
                        )
                    }
                    
                    // Loading al final para paginación
                    if (isLoading && pokemonList.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Mostrar detalles del Pokémon seleccionado
    pokemonViewModel.selectedPokemon?.let { pokemon ->
        PokemonDetailDialog(
            pokemon = pokemon,
            onDismiss = { pokemonViewModel.clearSelectedPokemon() }
        )
    }
}

@Composable
fun PokemonListItem(
    pokemonItem: com.egon.my3.data.models.PokemonListItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del Pokémon
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(pokemonItem.getImageUrl())
                        .crossfade(true)
                        .build()
                ),
                contentDescription = "Imagen de ${pokemonItem.name}",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del Pokémon
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "#${pokemonItem.extractId()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = pokemonItem.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Indicador de tipo (podrías personalizar por tipo real)
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF5350)) // Color rojo tipo fuego
            )
        }
    }
}

@Composable
fun PokemonDetailDialog(
    pokemon: com.egon.my3.data.models.Pokemon,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "#${pokemon.id} ${pokemon.name.replaceFirstChar { it.uppercase() }}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Imagen del Pokémon
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(pokemon.getOfficialArtwork())
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = "Imagen de ${pokemon.name}",
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Fit
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Información básica
                Text("Altura: ${pokemon.heightInMeters} m")
                Text("Peso: ${pokemon.weightInKg} kg")
                Text("Experiencia base: ${pokemon.baseExperience}")
                
                // Tipos
                Text(
                    text = "Tipos: ${pokemon.getTypeNames().joinToString(", ")}",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Buscar Pokémon...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { /* La búsqueda se hace en tiempo real */ }
        ),
        shape = RoundedCornerShape(16.dp)
    )
}

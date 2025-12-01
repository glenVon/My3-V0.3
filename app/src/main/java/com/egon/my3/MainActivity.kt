package com.egon.my3

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.egon.my3.data.database.AppDatabase
import com.egon.my3.data.repositories.PokemonRepository
import com.egon.my3.data.repositories.ProductRepository
import com.egon.my3.data.repositories.UserRepository
import com.egon.my3.presentation.screens.AddProductScreen
import com.egon.my3.presentation.screens.AdminScreen
import com.egon.my3.presentation.screens.CartScreen
import com.egon.my3.presentation.screens.EditProductScreen
import com.egon.my3.presentation.screens.EditUserScreen
import com.egon.my3.presentation.screens.LoginScreen
import com.egon.my3.presentation.screens.MainScreen
import com.egon.my3.presentation.screens.PokemonScreen
import com.egon.my3.presentation.screens.ProductListScreen
import com.egon.my3.presentation.screens.RegisterScreen
import com.egon.my3.presentation.viewmodels.CartViewModel
import com.egon.my3.presentation.viewmodels.PokemonViewModel
import com.egon.my3.presentation.viewmodels.ProductViewModel
import com.egon.my3.presentation.viewmodels.UserViewModel
import com.egon.my3.ui.theme.My3Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Log.d("MainActivity", "onCreate: Iniciando setContent")
            setContent {
                My3Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SafeAppLoader()
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e("MainActivity", "Error CRÍTICO en onCreate: ${e.message}", e)
            try {
                setContent {
                    ErrorScreen(errorMessage = "Error crítico al iniciar la UI:\n${e.localizedMessage}")
                }
            } catch (inner: Throwable) {
                Log.e("MainActivity", "Error en fallback UI", inner)
            }
        }
    }
}

@Composable
fun SafeAppLoader() {
    var appState by remember { mutableStateOf<AppState>(AppState.Loading) }
    val context = LocalContext.current.applicationContext

    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                try {
                    Log.d("SafeAppLoader", "Iniciando carga de dependencias...")
                    val db = AppDatabase.getInstance(context)
                    
                    val apiService = try {
                        com.egon.my3.data.network.RetrofitClient.instance
                    } catch (t: Throwable) {
                        Log.e("SafeAppLoader", "Error al crear Retrofit: ${t.message}")
                        null
                    }

                    val userRepo = UserRepository(db.userDao(), apiService)
                    val productRepo = ProductRepository(db.productDao(), apiService)
                    val pokemonRepo = PokemonRepository()

                    Log.d("SafeAppLoader", "Dependencias cargadas.")
                    appState = AppState.Success(userRepo, productRepo, pokemonRepo)
                } catch (e: Exception) {
                    Log.e("SafeAppLoader", "Error inicializando dependencias", e)
                    appState = AppState.Error(e.message ?: "Error desconocido")
                }
            }
        } catch (e: Throwable) {
            Log.e("SafeAppLoader", "Error fatal en corrutina", e)
            appState = AppState.Error("Error fatal: ${e.message}")
        }
    }

    when (val state = appState) {
        is AppState.Loading -> LoadingScreen()
        is AppState.Error -> ErrorScreen(state.message)
        is AppState.Success -> My3AppContent(state.userRepo, state.productRepo, state.pokemonRepo)
    }
}

sealed class AppState {
    object Loading : AppState()
    data class Error(val message: String) : AppState()
    data class Success(
        val userRepo: UserRepository,
        val productRepo: ProductRepository,
        val pokemonRepo: PokemonRepository
    ) : AppState()
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cargando...")
        }
    }
}

@Composable
fun My3AppContent(
    userRepo: UserRepository,
    productRepo: ProductRepository,
    pokemonRepo: PokemonRepository
) {
    var uiError by remember { mutableStateOf<String?>(null) }
    val navController = rememberNavController()

    val userViewModel = remember { try { UserViewModel(userRepo) } catch(e: Exception) { uiError = e.message; null } }
    val productViewModel = remember { try { ProductViewModel(productRepo) } catch(e: Exception) { uiError = e.message; null } }
    val cartViewModel = remember { CartViewModel() }
    val pokemonViewModel = remember { PokemonViewModel(pokemonRepo) }

    if (uiError != null) {
        ErrorScreen(errorMessage = uiError!!)
    } else if (userViewModel != null && productViewModel != null) {
        // Restauramos el startDestination a "login" para que la app inicie normal
        NavHost(navController = navController, startDestination = "login") {
            composable("test") {
                TestScreen(onContinue = { navController.navigate("login") })
            }
            composable("login") { LoginScreen(navController, userViewModel) }
            composable("register") { RegisterScreen(navController, userViewModel) }
            composable("main") { MainScreen(navController, userViewModel, cartViewModel) }
            composable("products") { ProductListScreen(navController, productViewModel, cartViewModel) }
            composable("cart") { CartScreen(navController, cartViewModel) }
            composable("admin") { AdminScreen(navController, userViewModel, productViewModel) }
            composable("addProduct") { AddProductScreen(navController, productViewModel) }
            composable("editUser/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
                EditUserScreen(navController, userViewModel, userId)
            }
            composable("editProduct/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                EditProductScreen(navController, productViewModel, productId)
            }
            composable("pokemon") { PokemonScreen(navController, pokemonViewModel) }
        }
    }
}

@Composable
fun TestScreen(onContinue: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Si ves esto, la app base funciona.", color = Color.Green)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onContinue) {
                Text("Ir al Login")
            }
        }
    }
}

@Composable
fun ErrorScreen(errorMessage: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "¡Error!\n\n$errorMessage",
            color = Color.Red,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

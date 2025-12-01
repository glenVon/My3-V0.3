package com.egon.my3.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.egon.my3.R
import com.egon.my3.presentation.viewmodels.CartViewModel
import com.egon.my3.presentation.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    cartViewModel: CartViewModel
) {
    val currentUser by remember { derivedStateOf { userViewModel.currentUser } }
    val cartItemCount by remember { derivedStateOf { cartViewModel.cartItemCount } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda Online", fontSize = 20.sp, color = Color.Black) },
                actions = {
                    Box {
                        IconButton(onClick = { navController.navigate("cart") }) {
                            Icon(Icons.Default.ShoppingCart, "Carrito", tint = Color.Black)
                        }
                        if (cartItemCount > 0) {
                            Text(
                                text = cartItemCount.toString(),
                                color = Color.Black,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                            )
                        }
                    }

                    // Botón de admin si es administrador
                    if (currentUser?.isAdmin == true) {
                        IconButton(onClick = { navController.navigate("admin") }) {
                            Icon(Icons.Default.Edit, "Administrar", tint = Color.Black)
                        }
                    }

                    // Botón de logout
                    IconButton(onClick = {
                        userViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, "Cerrar sesión", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Fondo con imagen a9
            AsyncImage(
                model = R.drawable.a9,
                contentDescription = "Fondo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
            // Información del usuario
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "¡Hola, ${currentUser?.name ?: "Usuario"}!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black
                    )
                    Text(
                        text = currentUser?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    if (currentUser?.isAdmin == true) {
                        Text(
                            text = "Modo Administrador",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "¡Bienvenido a la Tienda!",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("products") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Productos", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("cart") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Carrito ($cartItemCount items)", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para acceder a la Pokédex
            Button(
                onClick = { navController.navigate("pokemon") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF5350), // Rojo Pokémon
                    contentColor = Color.White
                )
            ) {
                Text("Pokédex", color = Color.White, fontSize = 16.sp)
            }

            // Botón de administración solo para admins
            if (currentUser?.isAdmin == true) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("admin") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Panel de Administración", color = Color.White, fontSize = 16.sp)
                }
            }
        }
        }
    }
}

package com.egon.my3.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import android.util.Log
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.egon.my3.R
import com.egon.my3.presentation.viewmodels.ProductViewModel
import com.egon.my3.presentation.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    productViewModel: ProductViewModel
) {
    val allUsers by userViewModel.allUsers.collectAsState()
    val allProducts by productViewModel.allProducts.collectAsState()
    var showAddUserDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
        ,
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Fondo con imagen a6
            AsyncImage(
                model = R.drawable.a6,
                contentDescription = "Fondo Admin",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
            // Tabs para seleccionar entre Usuarios y Productos
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Usuarios (${allUsers.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Productos (${allProducts.size})") }
                )
            }

            when (selectedTab) {
                0 -> UsersTab(
                    allUsers = allUsers,
                    userViewModel = userViewModel,
                    navController = navController,
                    onAddClick = { showAddUserDialog = true },
                    onDelete = { userId ->
                                Log.d("MY3_UI", "AdminScreen -> delete user requested id=$userId")
                                userViewModel.deleteUser(userId) {
                                    scope.launch { snackbarHostState.showSnackbar("Usuario eliminado") }
                                    Log.d("MY3_UI", "AdminScreen -> delete user callback executed id=$userId")
                                }
                    }
                )
                1 -> ProductsTab(
                    allProducts = allProducts,
                    productViewModel = productViewModel,
                    navController = navController,
                    onAddClick = { navController.navigate("addProduct") },
                    onDelete = { productId ->
                                Log.d("MY3_UI", "AdminScreen -> delete product requested id=$productId")
                                productViewModel.deleteProduct(productId) {
                                    scope.launch { snackbarHostState.showSnackbar("Producto eliminado") }
                                    Log.d("MY3_UI", "AdminScreen -> delete product callback executed id=$productId")
                                }
                    }
                )
            }
        }
        }
    }

    // Diálogo para agregar usuario
    if (showAddUserDialog) {
        AddUserDialog(
            onDismiss = { showAddUserDialog = false },
            onAddUser = { name, email, password, isAdmin ->
                userViewModel.addUser(name, email, password, isAdmin) {
                    showAddUserDialog = false
                    scope.launch {
                        snackbarHostState.showSnackbar("Usuario añadido")
                    }
                }
            }
        )
    }
}

@Composable
fun UsersTab(
    allUsers: List<com.egon.my3.data.models.User>,
    userViewModel: UserViewModel,
    navController: NavHostController,
    onAddClick: () -> Unit,
    onDelete: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Estadísticas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Gestión de Usuarios",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    "Total de usuarios: ${allUsers.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Administradores: ${allUsers.count { it.isAdmin }}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onAddClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, "Agregar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar Usuario")
                }
            }
        }

        // Lista de usuarios
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allUsers) { user ->
                UserCard(
                    user = user,
                    onEdit = { navController.navigate("editUser/${user.id}") },
                    onDelete = { onDelete(user.id) }
                )
            }
        }
    }
}

@Composable
fun ProductsTab(
    allProducts: List<com.egon.my3.data.models.Product>,
    productViewModel: ProductViewModel,
    navController: NavHostController,
    onAddClick: () -> Unit,
    onDelete: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Estadísticas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Gestión de Productos",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    "Total de productos: ${allProducts.size}",
                    style = MaterialTheme.typography.bodyMedium
                )

                val totalValue = allProducts.sumOf { it.price }
                Text(
                    "Valor total del inventario: $${String.format("%.2f", totalValue)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onAddClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, "Agregar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar Producto")
                }
            }
        }

        // Lista de productos
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allProducts) { product ->
                ProductAdminCard(
                    product = product,
                    onEdit = { navController.navigate("editProduct/${product.id}") },
                    onDelete = { onDelete(product.id) }
                )
            }
        }
    }
}

@Composable
fun ProductAdminCard(
    product: com.egon.my3.data.models.Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (product.imageUri != null) {
                AsyncImage(
                    model = product.imageUri,
                    contentDescription = product.name,
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text(product.description, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Precio: $${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Botones de acción
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Editar producto")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Eliminar producto")
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: com.egon.my3.data.models.User,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(user.name, style = MaterialTheme.typography.titleMedium)
                Text(user.email, style = MaterialTheme.typography.bodyMedium)
                if (user.isAdmin) {
                    Text(
                        "Administrador",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        "Usuario Normal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Botones de acción
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Editar usuario")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Eliminar usuario")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserDialog(
    onDismiss: () -> Unit,
    onAddUser: (String, String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Usuario") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Es administrador")
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isAdmin,
                        onCheckedChange = { isAdmin = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                        onAddUser(name, email, password, isAdmin)
                    }
                },
                enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

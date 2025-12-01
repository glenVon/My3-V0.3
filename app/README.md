# App  My3

Arquitectura: MVVM (Model-View-ViewModel)
Lenguaje Principal: Kotlin
UI Framework: Jetpack Compose
Base de Datos Local: Room
Comunicación Red: Retrofit + OkHttp
API Externa: PokeAPI (https://pokeapi.co)
Microservicios: Spring Boot (Kotlin)


CONTENIDO DE LA APLICACIÓN
1. Pantallas de Autenticación
✅ LoginScreen.kt - Inicio de sesión de usuarios

✅ RegisterScreen.kt - Registro de nuevos usuarios

2. Pantallas Principales
✅ MainScreen.kt - Pantalla principal después del login

✅ ProductListScreen.kt - Lista de productos disponibles

✅ CartScreen.kt - Carrito de compras

✅ AdminScreen.kt - Panel de administración

3. Pantallas de Gestión
✅ AddProductScreen.kt - Agregar nuevos productos

✅ EditProductScreen.kt - Editar productos existentes

✅ EditUserScreen.kt - Editar información de usuario

4. Pantallas Especiales
✅ PokemonScreen.kt - Consumo de API externa (PokeAPI)

✅ DebugScreen.kt - Configuración de desarrollo

✅ TestScreen.kt - Pantalla de prueba inicial

5. Pantallas de Utilidad
✅ LoadingScreen - Indicador de carga

✅ ErrorScreen - Manejo de errores

✅ SafeAppLoader - Cargador seguro con manejo de errores

COMPONENTES TÉCNICOS
1. ViewModels (Lógica de Presentación)
✅ UserViewModel.kt - Manejo de usuarios

✅ ProductViewModel.kt - Manejo de productos

✅ PokemonViewModel.kt - Manejo de datos Pokémon

✅ CartViewModel.kt - Manejo del carrito

2. Repositorios (Acceso a Datos)
✅ UserRepository.kt - Repositorio de usuarios

✅ ProductRepository.kt - Repositorio de productos

✅ PokemonRepository.kt - Repositorio de Pokémon

3. Modelos de Datos
✅ User.kt - Modelo de usuario

✅ Product.kt - Modelo de producto

✅ Pokemon.kt - Modelo de Pokémon

✅ CartItem.kt - Modelo de item del carrito

4. Base de Datos Local (Room)
✅ AppDatabase.kt - Base de datos principal

✅ UserDao.kt - DAO para usuarios

✅ ProductDao.kt - DAO para productos

CAPA DE RED
1. Clientes HTTP
✅ RetrofitClient.kt - Cliente para microservicios

✅ PokeApiClient.kt - Cliente para PokeAPI

✅ OkHttpProvider.kt - Configuración de OkHttp

2. Servicios API
✅ ApiService.kt - Interfaz para microservicios

✅ PokeApiService.kt - Interfaz para PokeAPI

✅ UserApi.kt - Endpoints de usuario

✅ ProductApi.kt - Endpoints de producto

3. Configuración de Red
✅ NetworkConfig.kt - Configuración centralizada

✅ DebugBroadcastReceiver.kt - Receiver para debugging

✅ NetworkConfig.kt - Gestión de URLs base

PRUEBAS UNITARIAS
1. Pruebas de ViewModel
✅ UserViewModelTest.kt - Pruebas de UserViewModel

2. Pruebas de Repositorio
✅ UserRepositoryTest.kt - Pruebas de UserRepository

✅ ProductRepositoryTest.kt - Pruebas de ProductRepository

3. Pruebas Instrumentadas
✅ RoomRepositoryInstrumentedTest.kt - Pruebas de Room


COMANDOS PARA ENCENDER EL MICRISERVICIO SPRING BOOT

EN EL TERMINAL DENTRO DE LA APLICACION EJECUTAR
.\scripts\run-backend.ps1


get

http://localhost:8081/users

http://localhost:8081/users/2

http://localhost:8082/products

https://pokeapi.co/api/v2/pokemon



post

http://localhost:8081/users

http://localhost:8082/products
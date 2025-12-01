package com.egon.my3.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.egon.my3.data.models.Product
import com.egon.my3.data.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class, Product::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Try to build a persistent DB; if something goes wrong, fall back to an in-memory DB
                val instance = try {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "my3-db"
                    ).fallbackToDestructiveMigration()
                        .build()
                } catch (e: Exception) {
                    // Fall back to an in-memory database so the app can continue to function
                    Room.inMemoryDatabaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java
                    ).build()
                }

                // Assign instance before seeding so DAOs are available
                INSTANCE = instance

                // Seed data asynchronously on IO dispatcher; don't block the caller
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val userDao = instance.userDao()
                            val productDao = instance.productDao()

                            // Seed users if empty
                            if (userDao.getAll().isEmpty()) {
                                val users = listOf(
                                    User(1, "Administrador", "admin@admin.com", "admin123", true),
                                    User(2, "Juan Pérez", "juan@test.com", "password", false),
                                    User(3, "María García", "maria@test.com", "password", false),
                                    User(4, "Carlos López", "carlos@test.com", "password123", false),
                                    User(5, "Ana Martínez", "ana@test.com", "pass1234", false)
                                )
                                users.forEach { userDao.insert(it) }
                            }

                            // Seed products if empty
                            if (productDao.getAll().isEmpty()) {
                                val products = listOf(
                                    Product(1, "Laptop Gaming", 999.99, "Laptop potente para gaming"),
                                    Product(2, "Smartphone", 499.99, "Teléfono inteligente"),
                                    Product(3, "Tablet", 299.99, "Tablet para trabajo"),
                                    Product(4, "Auriculares", 79.99, "Auriculares inalámbricos"),
                                    Product(5, "Smart Watch", 199.99, "Reloj inteligente"),
                                    Product(6, "Joystick", 19.99, "Manilla inalambrica"),
                                    Product(7, "Teclado Mecánico", 149.99, "Teclado mecánico RGB"),
                                    Product(8, "Mouse Inalámbrico", 49.99, "Mouse ergonómico inalámbrico")
                                )
                                products.forEach { productDao.insert(it) }
                            }
                        } catch (_: Exception) {
                            // Ignore seeding errors; the app can continue with empty DB
                        }
                    }
                } catch (_: Exception) {
                    // If seeding scheduling fails, ignore and continue; DB is still usable
                }

                instance
            }
        }
    }
}

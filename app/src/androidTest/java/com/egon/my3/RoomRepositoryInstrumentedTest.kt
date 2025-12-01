package com.egon.my3

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.egon.my3.data.database.AppDatabase
import com.egon.my3.data.database.ProductDao
import com.egon.my3.data.database.UserDao
import com.egon.my3.data.models.Product
import com.egon.my3.data.models.User
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomRepositoryInstrumentedTest {
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var productDao: ProductDao

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Importante: allowMainThreadQueries() es necesario para pruebas unitarias/instrumentadas simples
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
        productDao = db.productDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testUserCrud() = runBlocking {
        val user = User(1000, "TestUser", "test@local", "pwd", false)
        userDao.insert(user)

        val loaded = userDao.getById(1000)
        assertNotNull(loaded)
        assertEquals("TestUser", loaded?.name)

        userDao.update(user.copy(name = "UpdatedUser"))
        val updated = userDao.getById(1000)
        assertEquals("UpdatedUser", updated?.name)

        userDao.delete(updated!!)
        val after = userDao.getById(1000)
        assertNull(after)
    }

    @Test
    fun testProductCrud() = runBlocking {
        val product = Product(2000, "TestProduct", 1.23, "desc")
        productDao.insert(product)

        val loaded = productDao.getById(2000)
        assertNotNull(loaded)
        assertEquals("TestProduct", loaded?.name)

        productDao.update(product.copy(name = "UpdatedProduct"))
        val updated = productDao.getById(2000)
        assertEquals("UpdatedProduct", updated?.name)

        productDao.delete(updated!!)
        val after = productDao.getById(2000)
        assertNull(after)
    }
}

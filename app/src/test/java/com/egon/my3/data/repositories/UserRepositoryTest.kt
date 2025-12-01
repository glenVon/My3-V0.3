package com.egon.my3.data.repositories

import com.egon.my3.data.database.UserDao
import com.egon.my3.data.models.User
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UserRepositoryTest {

    private lateinit var userDao: UserDao
    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        userDao = mockk()
        userRepository = UserRepository(userDao)
    }

    @Test
    fun `addUser calls insert on dao`() = runTest {
        // Arrange
        val user = User(1, "Test User", "test@test.com", "password", false)
        coJustRun { userDao.insert(user) }

        // Act
        userRepository.addUser(user)

        // Assert
        coVerify(exactly = 1) { userDao.insert(user) }
    }

    @Test
    fun `updateUser calls update on dao`() = runTest {
        // Arrange
        val user = User(1, "Test User", "test@test.com", "password", false)
        coJustRun { userDao.update(user) }

        // Act
        userRepository.updateUser(user)

        // Assert
        coVerify(exactly = 1) { userDao.update(user) }
    }

    @Test
    fun `deleteUser calls delete on dao`() = runTest {
        // Arrange
        val userId = 1
        val user = User(userId, "Test User", "test@test.com", "password", false)
        coEvery { userDao.getById(userId) } returns user
        coJustRun { userDao.delete(user) }

        // Act
        userRepository.deleteUser(userId)

        // Assert
        coVerify(exactly = 1) { userDao.delete(user) }
    }

    @Test
    fun `validateCredentials with correct credentials returns user`() = runTest {
        // Arrange
        val email = "test@test.com"
        val password = "password"
        val testUser = User(1, "Test User", email, password, false)
        coEvery { userDao.getAll() } returns listOf(testUser)

        // Act
        val result = userRepository.validateCredentials(email, password)

        // Assert
        assertEquals(testUser, result)
    }

    @Test
    fun `validateCredentials with incorrect credentials returns null`() = runTest {
        // Arrange
        val email = "test@test.com"
        val password = "password"
        val wrongPassword = "wrongpassword"
        val testUser = User(1, "Test User", email, password, false)
        coEvery { userDao.getAll() } returns listOf(testUser)

        // Act
        val result = userRepository.validateCredentials(email, wrongPassword)

        // Assert
        assertNull(result)
    }

    @Test
    fun `emailExists returns true for existing email`() = runTest {
        // Arrange
        val email = "test@test.com"
        val testUser = User(1, "Test User", email, "password", false)
        coEvery { userDao.getAll() } returns listOf(testUser)

        // Act
        val result = userRepository.emailExists(email)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `emailExists returns false for non-existing email`() = runTest {
        // Arrange
        val email = "test@test.com"
        coEvery { userDao.getAll() } returns emptyList()

        // Act
        val result = userRepository.emailExists(email)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `getNextUserId returns max id plus one`() = runTest {
        // Arrange
        val maxId = 10
        coEvery { userDao.getMaxId() } returns (maxId as Int?)

        // Act
        val result = userRepository.getNextUserId()

        // Assert
        assertEquals(maxId + 1, result)
    }

    @Test
    fun `getNextUserId returns 1 when no users exist`() = runTest {
        // Arrange
        coEvery { userDao.getMaxId() } returns (null as Int?)

        // Act
        val result = userRepository.getNextUserId()

        // Assert
        assertEquals(1, result)
    }
}

package com.egon.my3.presentation.viewmodels

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.egon.my3.data.models.User
import com.egon.my3.data.repositories.UserRepository
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UserViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var userRepository: UserRepository
    private lateinit var userViewModel: UserViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock android.util.Log to avoid "Method ... not mocked" errors
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        userRepository = mockk()
        // Mock the flow that is collected on ViewModel init
        coEvery { userRepository.getAllUsersFlow() } returns flowOf(emptyList())
        userViewModel = UserViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with valid credentials updates state to success`() = runTest {
        // Arrange
        val email = "test@test.com"
        val password = "password"
        val testUser = User(1, "Test User", email, password, false)
        coEvery { userRepository.validateCredentials(email, password) } returns testUser

        // Act
        userViewModel.login(email, password)

        // Assert
        assertEquals("success", userViewModel.loginState)
        assertEquals(testUser, userViewModel.currentUser)
        assertEquals("", userViewModel.errorMessage)
    }

    @Test
    fun `login with invalid credentials updates state to error`() = runTest {
        // Arrange
        val email = "wrong@test.com"
        val password = "wrongpassword"
        coEvery { userRepository.validateCredentials(email, password) } returns null

        // Act
        userViewModel.login(email, password)

        // Assert
        assertEquals("error", userViewModel.loginState)
        assertNull(userViewModel.currentUser)
        assertEquals("Credenciales inválidas", userViewModel.errorMessage)
    }

    @Test
    fun `register with new user saves user and updates state to success`() = runTest {
        // Arrange
        val name = "New User"
        val email = "new@test.com"
        val password = "newpassword"
        // val newUser = User(name = name, email = email, password = password, isAdmin = false) // Not used directly

        coEvery { userRepository.emailExists(email) } returns false
        coEvery { userRepository.getNextUserId() } returns 1
        coJustRun { userRepository.addUser(any()) }

        // Act
        userViewModel.register(name, email, password)

        // Assert
        assertEquals("success", userViewModel.loginState)
        assertEquals(name, userViewModel.currentUser?.name)
        assertEquals(email, userViewModel.currentUser?.email)
        assertEquals("", userViewModel.errorMessage)
    }

    @Test
    fun `register with existing email updates state to error`() = runTest {
        // Arrange
        val name = "Existing User"
        val email = "existing@test.com"
        val password = "password"

        coEvery { userRepository.emailExists(email) } returns true

        // Act
        userViewModel.register(name, email, password)

        // Assert
        assertEquals("error", userViewModel.loginState)
        assertNull(userViewModel.currentUser)
        assertEquals("El usuario ya existe", userViewModel.errorMessage)
    }
}

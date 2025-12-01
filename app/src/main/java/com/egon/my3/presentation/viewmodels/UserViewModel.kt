package com.egon.my3.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egon.my3.data.models.User
import com.egon.my3.data.repositories.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val TAG = "UserViewModel"

    var currentUser: User? by mutableStateOf(null)
        private set

    var loginState by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf("")
        private set

    val allUsers: StateFlow<List<User>> = userRepository
        .getAllUsersFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun login(email: String, password: String) {
        loginState = "loading"
        errorMessage = ""
        viewModelScope.launch {
            try {
                Log.d(TAG, "login -> start email=$email")
                val user = userRepository.validateCredentials(email, password)
                if (user != null) {
                    currentUser = user
                    loginState = "success"
                } else {
                    errorMessage = "Credenciales inválidas"
                    loginState = "error"
                }
            } catch (t: Throwable) {
                Log.e(TAG, "Login error", t)
                errorMessage = "Credenciales inválidas"
                loginState = "error"
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        loginState = "loading"
        errorMessage = ""
        viewModelScope.launch {
            try {
                Log.d(TAG, "register -> start name=$name email=$email")
                if (userRepository.emailExists(email)) {
                    errorMessage = "El usuario ya existe"
                    loginState = "error"
                    return@launch
                }

                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    val newUser = User(name = name, email = email, password = password, isAdmin = false)
                    userRepository.addUser(newUser)
                    currentUser = newUser
                    loginState = "success"
                } else {
                    errorMessage = "Complete todos los campos"
                    loginState = "error"
                }
            } catch (t: Throwable) {
                Log.e(TAG, "Register error", t)
                errorMessage = "Error al registrar"
                loginState = "error"
            }
        }
    }

    fun logout() {
        currentUser = null
        loginState = ""
        errorMessage = ""
    }

    fun addUser(name: String, email: String, password: String, isAdmin: Boolean = false, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "addUser -> start name=$name email=$email")
                // Use custom ID logic if needed, or let Room auto-generate (0).
                // If you want to support manual ID assignment or logic from previous conversations,
                // ensure User model has id=0 by default for auto-generation.
                val newUser = User(name = name, email = email, password = password, isAdmin = isAdmin)
                userRepository.addUser(newUser)
                Log.d(TAG, "Added user: $newUser")
                Log.d(TAG, "addUser -> complete id=${newUser.id}")
                onComplete()
            } catch (t: Throwable) {
                Log.e(TAG, "Error adding user", t)
                onComplete()
            }
        }
    }

    fun updateUser(userId: Int, name: String, email: String, password: String, isAdmin: Boolean, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "updateUser -> start id=$userId name=$name email=$email")
                val updatedUser = User(userId, name, email, password, isAdmin)
                userRepository.updateUser(updatedUser)
                if (currentUser?.id == userId) currentUser = updatedUser
                Log.d(TAG, "Updated user: $updatedUser")
                Log.d(TAG, "updateUser -> complete id=${updatedUser.id}")
                onComplete()
            } catch (t: Throwable) {
                Log.e(TAG, "Error updating user", t)
                onComplete()
            }
        }
    }

    fun deleteUser(userId: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "deleteUser -> start id=$userId")
                userRepository.deleteUser(userId)
                if (currentUser?.id == userId) logout()
                Log.d(TAG, "Deleted user id: $userId")
                Log.d(TAG, "deleteUser -> complete id=$userId")
                onComplete()
            } catch (t: Throwable) {
                Log.e(TAG, "Error deleting user", t)
                onComplete()
            }
        }
    }

    fun getUserById(userId: Int): User? = allUsers.value.find { it.id == userId }

    fun clearError() {
        errorMessage = ""
        loginState = ""
    }
}

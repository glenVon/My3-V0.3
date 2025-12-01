package com.egon.my3.data.repositories

import com.egon.my3.data.database.UserDao
import com.egon.my3.data.network.ApiService
import com.egon.my3.data.models.User
import com.egon.my3.data.network.LoginRequest
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao, private val api: ApiService? = null) {

    fun getAllUsersFlow(): Flow<List<User>> = userDao.getAllFlow()

    suspend fun getUserById(userId: Int): User? = userDao.getById(userId)

    fun getUserByIdFlow(userId: Int): Flow<User?> = userDao.getByIdFlow(userId)

    suspend fun getUserByEmail(email: String): User? {
        try {
            api?.let { client ->
                val remote = client.getUserByEmail(email)
                if (remote != null) return remote
            }
        } catch (_: Exception) {
            // ignore and fallback
        }
        return userDao.getAll().find { it.email == email }
    }

    suspend fun addUser(user: User) {
        // Try remote create, fallback to local
        try {
            api?.let { client ->
                val created = client.addUser(user)
                // mirror locally
                userDao.insert(created)
                return
            }
        } catch (_: Exception) {
            // fallback
        }
        userDao.insert(user)
    }

    suspend fun updateUser(updatedUser: User) {
        try {
            api?.let { client ->
                val updated = client.updateUser(updatedUser.id, updatedUser)
                // mirror locally
                userDao.update(updated)
                return
            }
        } catch (_: Exception) {
            // fallback
        }
        userDao.update(updatedUser)
    }

    suspend fun deleteUser(userId: Int) {
        try {
            api?.let { client ->
                client.deleteUser(userId)
                // mirror locally
                val user = userDao.getById(userId)
                if (user != null) {
                    userDao.delete(user)
                }
                return
            }
        } catch (_: Exception) {
            // fallback
        }
        val user = userDao.getById(userId)
        if (user != null) {
            userDao.delete(user)
        }
    }

    suspend fun validateCredentials(email: String, password: String): User? {
        try {
            api?.let { client ->
                val user = client.login(LoginRequest(email, password))
                if (user != null) return user
            }
        } catch (_: Exception) {
            // ignore and fallback
        }
        return userDao.getAll().find { it.email == email && it.password == password }
    }

    suspend fun emailExists(email: String): Boolean {
        return getUserByEmail(email) != null
    }

    suspend fun getNextUserId(): Int {
        return (userDao.getMaxId() ?: 0) + 1
    }
}

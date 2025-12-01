package com.egon.userservice.repository

import com.egon.userservice.model.User
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
class UserRepository {
    private val data = ConcurrentHashMap<Long, User>()
    private val idSequence = AtomicLong(1)

    init {
        // Datos de ejemplo
        val admin = User(id = idSequence.getAndIncrement(), name = "Administrador", email = "admin@admin.com", password = "admin123", isAdmin = true)
        val u2 = User(id = idSequence.getAndIncrement(), name = "Juan Pérez", email = "juan@test.com", password = "password")
        data[admin.id!!] = admin
        data[u2.id!!] = u2
    }

    fun findAll(): List<User> = data.values.toList()

    fun findById(id: Long): User? = data[id]

    fun findByEmail(email: String): User? = data.values.find { it.email == email }

    fun save(user: User): User {
        val newId = user.id ?: idSequence.getAndIncrement()
        val newUser = user.copy(id = newId)
        data[newId] = newUser
        return newUser
    }

    fun delete(id: Long) {
        data.remove(id)
    }

    fun validateCredentials(email: String, password: String): User? {
        return data.values.find { it.email == email && it.password == password }
    }
}

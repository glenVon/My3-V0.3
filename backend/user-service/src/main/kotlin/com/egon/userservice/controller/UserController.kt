package com.egon.userservice.controller

import com.egon.userservice.model.User
import com.egon.userservice.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val repo: UserRepository) {

    @GetMapping
    fun all() = repo.findAll()

    @GetMapping("/{id}")
    fun byId(@PathVariable id: Long) = repo.findById(id)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()

    @PostMapping
    fun create(@RequestBody user: User) = ResponseEntity.ok(repo.save(user))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        repo.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/byEmail/{email}")
    fun byEmail(@PathVariable email: String): ResponseEntity<User> {
        val user = repo.findByEmail(email)
        return if (user != null) ResponseEntity.ok(user) else ResponseEntity.notFound().build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody user: User): ResponseEntity<User> {
        // Ensure the saved user has the requested id
        val saved = repo.save(user.copy(id = id))
        return ResponseEntity.ok(saved)
    }

    @PostMapping("/login")
    fun login(@RequestBody credentials: Map<String, String>): ResponseEntity<User> {
        val email = credentials["email"] ?: ""
        val password = credentials["password"] ?: ""
        val user = repo.validateCredentials(email, password)
        return if (user != null) ResponseEntity.ok(user) else ResponseEntity.status(401).build()
    }
}

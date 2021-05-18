package org.fintech.tinkoff.controller

import org.fintech.tinkoff.model.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.fintech.tinkoff.repository.DbUserDao

@RequestMapping("api/users")
interface UserController {
    @GetMapping
    @Operation(summary = "Get all users")
    fun getUsers(): List<User>

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "user found"),
        ]
    )
    fun getUser(@PathVariable id: Int): User

    @PostMapping
    @Operation(summary = "Add a user from telegrams chat id or / and email ")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "correct data format"),
            ApiResponse(responseCode = "404", description = "invalid data format")
        ]
    )
    fun addUser(@RequestBody user: User)

    @PutMapping("/{id}")
    @Operation(summary = "Update user information")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "user updated"),
        ]
    )
    fun updateUser(@PathVariable id: Int, @RequestBody user: User)

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove user by id")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "user deleted"),
        ]
    )
    fun deleteUser(@PathVariable id: Int)
}

@RestController
class UserControllerImpl(private val repository: DbUserDao) : UserController {
    override fun getUsers(): List<User> {
        return repository.get()
    }

    override fun getUser(@PathVariable id: Int): User {
        return repository.get(id) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Student with id: $id not found"
        )
    }

    override fun addUser(@RequestBody user: User) {
        log.info("Creating user: $user")

        repository.create(user)
    }

    override fun updateUser(@PathVariable id: Int, @RequestBody user: User) {
        log.info("Updating user: $user")

        repository.updateUser(id, user)
    }

    override fun deleteUser(@PathVariable id: Int) {
        log.info("Deleting user with id: $id")

        repository.delete(id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserControllerImpl::class.java)
    }
}
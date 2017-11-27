package nu.westlin.kotlin.kotlinrest

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@SpringBootApplication
class KotlinrestApplication

fun main(args: Array<String>) {
    runApplication<KotlinrestApplication>(*args)
}

@RestController("/")
class HomeController {

    @GetMapping
    fun home() = "Hello, I'm a controller!"
}

@RestController
class UsersController(private val userRepository: UserRepository) {

    val logger: Logger = LoggerFactory.getLogger(UsersController::class.java)

    @GetMapping("/users")
    fun findAll() = userRepository.findAll()

    @GetMapping("/users/{id}")
    fun findById(@PathVariable id: Int, servletResponse: HttpServletResponse): User? {
        val user = userRepository.findById(id)
        if (user == null) {
            servletResponse.status = HttpStatus.NOT_FOUND.value()
        }

        return user
    }

    @PostMapping("/users")
    fun add(@RequestBody name: String) = userRepository.add(name)

    @PutMapping("/users")
    fun update(@RequestBody user: User) = userRepository.update(user)

    @ExceptionHandler(IllegalArgumentException::class)
    fun illegalArgumentException(e: IllegalArgumentException, response: HttpServletResponse) {
        logger.error("Error", e)
        response.status = HttpStatus.NOT_ACCEPTABLE.value()
    }
}

@Repository
class UserRepository {
    val a = arrayListOf("")
    private val users = mutableListOf(
            User(1, "Camilla"),
            User(2, "Peter")
    )

    fun findAll() = users.toList().sortedBy { it.id }

    fun findById(id: Int) = users.firstOrNull { it.id == id }

    fun add(username: String): User {
        val user = User(generateId(), username)
        users.add(user)

        return user
    }

    fun update(user: User) {
        val currentUser = users.firstOrNull { it.id == user.id }
        if (currentUser != null) {
            users.remove(currentUser)
            users.add(user)
        } else {
            throw IllegalArgumentException("User $user not found")
        }
    }

    private fun generateId(): Int = (users.maxBy { it.id }?.id ?: 0) + 1
}

data class User(val id: Int, val name: String)
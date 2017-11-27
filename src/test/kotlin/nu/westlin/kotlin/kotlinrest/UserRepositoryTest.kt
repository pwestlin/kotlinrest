package nu.westlin.kotlin.kotlinrest

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Test

import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.util.ReflectionTestUtils
import org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage



@RunWith(SpringRunner::class)
@WebMvcTest(HomeController::class)
class UserRepositoryTest {

    val repository = UserRepository()

    private val users = mutableListOf(
            User(1, "Foo"),
            User(2, "Bar")
    )


    @Before
    fun init() {
        ReflectionTestUtils.setField(repository, "users", users)
    }

    @Test
    fun findAll() {
        assertThat(repository.findAll()).containsExactlyElementsOf(users)
    }

    @Test
    fun findById() {
        val user = users.last()

        assertThat(repository.findById(user.id)).isEqualTo(user)
    }

    @Test
    fun add() {
        val username = "Foobar"

        val addedUser = repository.add(username)

        assertThat(addedUser).isEqualTo(User(users.map { it.id }.max()!!, username))
        assertThat(users).contains(addedUser)
    }

    @Test
    fun update() {
        val originalUser = users.first()
        val updatedUser = originalUser.copy(name = "FooBar")

        repository.update(updatedUser)

        assertThat(users).contains(updatedUser)
        assertThat(users).doesNotContain(originalUser)
    }

    @Test
    fun updateNonExisitingUserShouldThrowIllegalArgumentException() {
        assertThatThrownBy { repository.update(User(users.last().id + 12345, "Foobar")) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }
}
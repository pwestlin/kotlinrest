package nu.westlin.kotlin.kotlinrest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup


@RunWith(SpringRunner::class)
@WebMvcTest(HomeController::class)
class UsersControllerTest {

    val users = mutableListOf(
            User(1, "Camilla"),
            User(2, "Peter")
    )


    val userRepository = mock(UserRepository::class.java)
    val mvc = standaloneSetup(UsersController(userRepository)).build()
    val mapper = ObjectMapper().registerModule(KotlinModule())

    @Test
    fun findAll() {
        `when`(userRepository.findAll()).thenReturn(users)

        val jsonResult = mvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

        val resultUsers =
                mapper.readValue<List<User>>(jsonResult, mapper.typeFactory.constructCollectionType(List::class.java, User::class.java))

        assertThat(resultUsers).containsExactlyElementsOf(users)
    }

    @Test
    fun findById() {
        val user = User(1, "Foo")
        `when`(userRepository.findById(eq(user.id))).thenReturn(user)

        val jsonResult = mvc.perform(get("/users/${user.id}").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

        val resultUser =
                mapper.readValue<User>(jsonResult)

        assertThat(resultUser).isEqualTo(user)
    }

    @Test
    fun findById_shouldReturn404WhenNotFound() {
        mvc.perform(get("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound)
                .andExpect(content().string(""))
    }

    @Test
    fun add() {
        val user = User(79, "Foo")
        `when`(userRepository.add(user.name)).thenReturn(user)

        val jsonResult = mvc.perform(post("/users").content(user.name).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

        val resultUser =
                mapper.readValue<User>(jsonResult)

        assertThat(resultUser).isEqualTo(user)
    }

    @Test
    fun update() {
        val exisitingUser = User(79, "Foo")
        val updatedUser = exisitingUser.copy(name = "Bar")
        `when`(userRepository.findById(exisitingUser.id)).thenReturn(exisitingUser)

        mvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedUser)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().string(""))
    }

    @Test
    fun update_userDoesNotExist() {
        val user = User(79, "Foo")
        `when`(userRepository.update(user)).thenThrow(IllegalArgumentException("Bla"))

        mvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(user)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable)
                .andExpect(content().string(""))
    }
}

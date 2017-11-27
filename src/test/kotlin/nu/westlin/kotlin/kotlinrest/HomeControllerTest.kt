package nu.westlin.kotlin.kotlinrest

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup


@RunWith(SpringRunner::class)
@WebMvcTest(HomeController::class)
class HomeControllerTest {

    val mvc = standaloneSetup(HomeController()).build()

    @Test
    fun root() {
        mvc.perform(get("/"))
                .andExpect(status().isOk)
                .andExpect(content().string("Hello, I'm a controller!"))
    }
}

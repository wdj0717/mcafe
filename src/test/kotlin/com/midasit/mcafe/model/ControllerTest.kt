package com.midasit.mcafe.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestCase
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter
import java.nio.charset.StandardCharsets

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["h2"])
abstract class ControllerTest: BehaviorSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    protected lateinit var mockMvc: MockMvc

    override suspend fun beforeTest(testCase: TestCase) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .addFilters<DefaultMockMvcBuilder>(CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
            .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
            .build()

        super.beforeTest(testCase)
    }

    protected fun MockMvc.getPerform(url: String, vararg vars: Any?) = this.get(url, *vars).andExpect { status { isOk() } }

    protected fun MockMvc.postPerform(url: String, vararg vars: Any?, body: Any?) = this.post(url, *vars){
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
    }.andExpect { status { isOk() } }

    protected fun getJsonString(obj: Any): String = objectMapper.writeValueAsString(obj)
    protected fun <T> getObject(json: String, clazz: Class<T>): T = objectMapper.readValue(json, clazz)

    companion object {
        protected val objectMapper = Jackson2ObjectMapperBuilder.json()
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .modules(
                listOf(
                    JavaTimeModule(), KotlinModule.Builder()
                        .configure(KotlinFeature.NullIsSameAsDefault, true)
                        .build()
                )
            ).build<ObjectMapper>()
    }
}
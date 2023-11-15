package com.midasit.mcafe.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestCase
import io.kotest.extensions.spring.SpringExtension
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@ActiveProfiles(value = ["h2"])
abstract class ControllerTest: BehaviorSpec() {

    override fun extensions() = listOf(SpringExtension)

    private lateinit var mockMvc: MockMvc

    abstract fun getController(): Any

    override suspend fun beforeTest(testCase: TestCase) {
        mockMvc = MockMvcBuilders
            .standaloneSetup(getController())
            .apply<StandaloneMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity(MockSpringSecurityFilter()))
            .setCustomArgumentResolvers(MockAuthenticationArgResolver())
            .build()

        super.beforeTest(testCase)
    }

    protected fun perform(requestBuilder: MockHttpServletRequestBuilder): ResultActions {
        return mockMvc.perform(
            requestBuilder
                .principal(UsernamePasswordAuthenticationToken(1L, null, listOf()))
                .accept(MediaType.APPLICATION_JSON)
        )
    }

    protected fun getJsonString(obj: Any): String = objectMapper.writeValueAsString(obj)
    protected fun <T> getObject(json: String, clazz: Class<T>): T = objectMapper.readValue(json, clazz)

    companion object {
        @JvmStatic
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
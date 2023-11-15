package com.midasit.mcafe.infra.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

@Configuration
class SwaggerConfig {

    @Bean
    fun getOpenApi(): OpenAPI {
        val server = Server().url("/")
        return OpenAPI()
            .components(Components().addSecuritySchemes(HttpHeaders.AUTHORIZATION, securityScheme()))
            .security(listOf(securityRequirement()))
            .info(getApiInfo())
            .addServersItem(server)
    }

    private fun getApiInfo(): Info {
        return Info()
            .title(TITLE)
            .description(DESCRIPTION)
            .version(VERSION)
    }

    private fun securityScheme() = SecurityScheme()
        .type(SecurityScheme.Type.APIKEY)
        .scheme(BEARER)
        .bearerFormat(JWT)
        .`in`(SecurityScheme.In.HEADER)
        .name(HttpHeaders.AUTHORIZATION)

    private fun securityRequirement() = SecurityRequirement().addList(HttpHeaders.AUTHORIZATION)

    companion object {
        private const val TITLE = "Mcafe V2 API"
        private const val DESCRIPTION = "Mcafe V2 API 명세서"
        private const val VERSION = "2.0"
        private const val BEARER = "Bearer"
        private const val JWT = "JWT"
    }

}
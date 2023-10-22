package com.midasit.mcafe.infra.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    companion object {
        private const val TITLE = "Mcafe V2 API"
        private const val DESCRIPTION = "Mcafe V2 API 명세서"
        private const val VERSION = "2.0"
    }

    @Bean
    fun getOpenApi(): OpenAPI {
        val server = Server().url("/")
        return OpenAPI()
                .info(getApiInfo())
                .addServersItem(server)
    }

    private fun getApiInfo(): Info {
        return Info()
                .title(TITLE)
                .description(DESCRIPTION)
                .version(VERSION)
    }


}
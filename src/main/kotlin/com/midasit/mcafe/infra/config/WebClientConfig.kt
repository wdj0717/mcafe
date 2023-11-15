package com.midasit.mcafe.infra.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import java.util.concurrent.TimeUnit

/**
 * WebClient 설정
 */
@Configuration
class WebClientConfig(
    private val objectMapper: ObjectMapper,
    @Value("\${web-client.connect-timeout}") private val connectTimeout: Int,
    @Value("\${web-client.read-timeout}") private val readTimeout: Int,
    @Value("\${web-client.write-timeout}") private val writeTimeout: Int
) {
    @Bean
    fun webClient(): WebClient {
        val clientMapper = objectMapper.copy()
        clientMapper.propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE

        val httpClient =
            HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout) // Connection Timeout
                .doOnConnected { connection: Connection ->
                    connection.addHandlerLast(
                        ReadTimeoutHandler(
                            readTimeout.toLong(),
                            TimeUnit.MILLISECONDS
                        )
                    ) // Read Timeout
                        .addHandlerLast(WriteTimeoutHandler(writeTimeout.toLong(), TimeUnit.MILLISECONDS))
                } // Write Timeout


        val connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient)

        val exchangeStrategies = ExchangeStrategies.builder()
            .codecs { it.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(clientMapper)) }.build()

        return WebClient.builder().defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .exchangeStrategies(exchangeStrategies).clientConnector(connector).build()
    }
}
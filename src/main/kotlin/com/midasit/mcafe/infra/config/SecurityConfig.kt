package com.midasit.mcafe.infra.config

import com.midasit.mcafe.infra.config.jwt.JwtAuthenticationFilter
import com.midasit.mcafe.infra.config.jwt.JwtTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class SecurityConfig(val jwtTokenProvider: JwtTokenProvider) {

    @Bean
    fun configure(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring().requestMatchers("v3/api-docs", "swagger-ui/**")
        }
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors { it.configurationSource(corsConfigure()) }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api-docs/**", "/health", "/swagger-ui.html",
                    "/swagger-ui/**", "/v3/**", "/actuator/**"
                )
                    .permitAll()
                    .requestMatchers("/test/**", "/order/menu/**", "/member/**")
                    .permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java
            )
            .formLogin { it.disable() }
            .logout { it.disable() }
            .build()
    }

    private fun corsConfigure(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.addAllowedOriginPattern(WILD_CARD)
        configuration.addAllowedHeader(WILD_CARD)
        configuration.addAllowedMethod(WILD_CARD)
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration(WILD_CARD_PATTERN, configuration)
        return source
    }

    companion object {
        private const val WILD_CARD = "*"
        private const val WILD_CARD_PATTERN = "/**"
    }
}

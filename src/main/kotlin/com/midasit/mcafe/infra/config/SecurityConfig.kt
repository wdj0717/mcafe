package com.midasit.mcafe.infra.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig {

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
            .csrf { it.disable() }
            .cors { it.disable() }
            .authorizeRequests {
                it.requestMatchers(
                    "/api-docs/**", "/health", "/swagger-ui.html",
                    "/swagger-ui/**", "/v3/**", "/actuator/**",
                    "/menu/**"
                )
                    .permitAll()
                    .requestMatchers("/member/signup", "/member/login")
                    .permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .build()
    }

    //TODO @Peter : 로그인 붙일때 수정
//    @Bean
//    fun userDetailsService(): UserDetailsService {
//        val userDetails = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build()
//        return InMemoryUserDetailsManager(userDetails)
//    }

}
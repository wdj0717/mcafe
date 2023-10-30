package com.midasit.mcafe.infra.config.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    val jwtTokenProvider: JwtTokenProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = jwtTokenProvider.resolveToken(request) ?: return filterChain.doFilter(request, response)
        if (jwtTokenProvider.validateAccessToken(token)) {
            jwtTokenProvider.getAuthentication(token).let {
                SecurityContextHolder.getContext().authentication = it
            }
        }
        filterChain.doFilter(request, response)
    }
}
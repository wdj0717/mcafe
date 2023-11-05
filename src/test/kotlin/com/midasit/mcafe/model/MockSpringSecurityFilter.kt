package com.midasit.mcafe.model

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.FilterConfig
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import java.io.IOException

class MockSpringSecurityFilter : Filter {
    override fun init(filterConfig: FilterConfig) {}

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val authentication = (req as HttpServletRequest).userPrincipal as Authentication
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(req, res)
    }

    override fun destroy() {
        SecurityContextHolder.clearContext()
    }
}
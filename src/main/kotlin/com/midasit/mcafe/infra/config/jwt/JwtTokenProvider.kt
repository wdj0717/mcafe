package com.midasit.mcafe.infra.config.jwt

import io.jsonwebtoken.*
import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.security.SignatureException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class JwtTokenProvider {

    @Value("\${jwt.secret.key}")
    private var secretKey: String = ""

    @Value("\${jwt.access.token.expiration}")
    private val accessTokenExpiration = 30

    @Value("\${jwt.refresh.token.expiration}")
    private val refreshTokenExpiration = 30

    init {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    }

    fun generateAccessToken(id: Long, authorities: List<String> = listOf()): String {
        return generateToken(id, authorities, accessTokenExpiration.toLong())
    }

    private fun generateToken(
        id: Long,
        authorities: List<String>,
        tokenExpiration: Long
    ): String {
        val now = Instant.now()
        return Jwts.builder()
            .setHeader(createHeader())
            .setClaims(createClaims(id, authorities))
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(tokenExpiration, ChronoUnit.MINUTES)))
            .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret 값 세팅
            .compact()
    }


    fun generateRefreshToken(id: Long, authorities: List<String>): String {
        return generateToken(id, authorities, refreshTokenExpiration.toLong())
    }

    private fun createHeader(): Map<String, Any> {
        val headers: MutableMap<String, Any> = HashMap()
        headers["typ"] = HEADER_TYP
        headers["alg"] = SignatureAlgorithm.HS256.value
        return headers
    }

    private fun createClaims(
        id: Long,
        authorities: List<String>
    ): Map<String, Any> {
        val claims: MutableMap<String, Any> = HashMap()
        claims[CLAIM_ID] = id
        claims[CLAIM_AUTHORITIES] = authorities
        return claims
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken: String = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        return if (!bearerToken.startsWith(TOKEN_SCHEME)) {
            null
        } else bearerToken.replace(TOKEN_SCHEME, StringUtils.EMPTY)
    }

    fun validateAccessToken(accessToken: String?): Boolean {
        return getClaims(accessToken).let { true }
    }

    fun getClaims(token: String?): Claims {
        return try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body
        } catch (e: SignatureException) {
            throw JwtException("Invalid JWT signature")
        } catch (e: MalformedJwtException) {
            throw JwtException("Invalid JWT token")
        } catch (e: UnsupportedJwtException) {
            throw JwtException("Unsupported JWT token")
        } catch (e: IllegalArgumentException) {
            throw JwtException("JWT claims string is empty.")
        } catch (e: ExpiredJwtException) {
            throw JwtException("Expired JWT token")
        }
    }

    fun getMemberSn(claims: Claims): Long {
        return claims[CLAIM_ID].toString().toLong()
    }

    fun getAuthentication(token: String): Authentication {
        val claims = this.getClaims(token)
        val memberSn: Long = this.getMemberSn(claims)

        return UsernamePasswordAuthenticationToken(memberSn, null, listOf())
    }

    companion object {
        const val TOKEN_SCHEME = "Bearer "
        const val HEADER_TYP = "JWT"
        const val CLAIM_ID = "id"
        const val CLAIM_AUTHORITIES = "authorities"
    }

}
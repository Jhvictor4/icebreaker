package com.wafflestudio.ai.icebreaker.api

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.time.Duration

object JwtProvider {
    private val validityInMilliseconds: Long =
        Duration.INFINITE.inWholeMilliseconds
    private val secretKey =
        Keys.hmacShaKeyFor("waffle-icebreaker-secret-key-fighting".toByteArray(StandardCharsets.UTF_8))

    fun createAccessToken(userId: Long): String {
        val now = Date()
        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(now)
            .setExpiration(Date(now.time + validityInMilliseconds))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(authorizationHeader: String): Boolean {
        return try {
            val claims = getClaimsJws(extractToken(authorizationHeader))
            !claims
                .body
                .expiration
                .before(Date())
        } catch (e: JwtException) {
            false
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private fun getClaimsJws(token: String): Jws<Claims> {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
    }

    fun getPayload(authorizationHeader: String): String {
        return getClaimsJws(
            extractToken(authorizationHeader)
        )
            .body
            .subject
    }

    private fun extractToken(header: String) = try {
        header.split(" ")
            .also { check(it.first() == TOKEN_TYPE) }
            .last()
    } catch (e: Exception) {
        throw IllegalArgumentException("Invalid token")
    }

    private const val TOKEN_TYPE = "Bearer"
}

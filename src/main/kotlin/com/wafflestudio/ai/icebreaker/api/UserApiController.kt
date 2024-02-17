package com.wafflestudio.ai.icebreaker.api

import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.user.User
import com.wafflestudio.ai.icebreaker.outbound.user.UserRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserApiController(
    private val userRepository: UserRepository
) {

    data class LoginResponse(val loginToken: String)

    @PostMapping("/login")
    suspend fun logIn(): LoginResponse {
        return LoginResponse(
            JwtProvider.createAccessToken(userRepository.create(User.create()).id)
        )
    }

    @GetMapping("/me")
    suspend fun userMe(
        user: User // leverages parameterHandlerArgumentResolver
    ): User {
        return user
    }

    @GetMapping("/{id}")
    suspend fun get(
        @PathVariable id: Long
    ): User {
        return userRepository.getUser(id) ?: throw IllegalArgumentException("User not found")
    }

    companion object : Log
}

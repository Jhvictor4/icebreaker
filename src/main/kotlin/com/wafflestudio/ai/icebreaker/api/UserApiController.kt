package com.wafflestudio.ai.icebreaker.api

import com.fasterxml.jackson.annotation.JsonFormat
import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.user.User
import com.wafflestudio.ai.icebreaker.application.user.UserInformation
import com.wafflestudio.ai.icebreaker.outbound.user.UserRepository
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

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
        return userRepository.getUser(id) ?: throw ApplicationException.Common("User not found")
    }

    @PostMapping("/basicInformation")
    suspend fun addBasicInformation(
        @RequestBody basicInformation: BasicInformation
    ): LoginResponse {
        logger.info { basicInformation }
        val user = userRepository.create(User.create())
        val userId = userRepository.create(user).id
        user.name = basicInformation.name ?: throw IllegalArgumentException("require name parameter")
        val newUserInfo = mutableListOf<UserInformation>()
        basicInformation.birthDay?.let {
            newUserInfo.add(UserInformation.Birthday(it))
        }
        basicInformation.mbti?.let {
            newUserInfo.add(it)
        }
        basicInformation.gender?.let {
            newUserInfo.add(it)
        }
        basicInformation.major?.let {
            newUserInfo.add(UserInformation.Major(it))
        }

        newUserInfo.addAll(user.information.filter { it is UserInformation.ImageSummary || it is UserInformation.ImageUrl })

        // 신규 데이터 삽입
        user.information = newUserInfo
        userRepository.create(user)
        return LoginResponse(
            JwtProvider.createAccessToken(userId)
        )
    }

    data class BasicInformation(
        var name: String? = null,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        var birthDay: LocalDateTime? = null,
        var gender: UserInformation.Gender? = null,
        var mbti: UserInformation.MBTI? = null,
        var major: String? = null,
    )

    companion object : Log

}

package com.wafflestudio.ai.icebreaker.application.user

interface UserPort {
    suspend fun create(user: User): User

    suspend fun getUser(id: Long): User?
}

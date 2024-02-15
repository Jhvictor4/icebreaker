package com.wafflestudio.ai.icebreaker.application.user

interface UserPort {

    fun getUser(id: Long): User?

}
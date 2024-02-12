package com.wafflestudio.ai.icebreaker.application.user

data class User(
    val id: Long,
    val name: String,
    val basics: Set<BasicInformation>
)
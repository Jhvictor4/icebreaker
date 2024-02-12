package com.wafflestudio.ai.icebreaker.application.user

data class User(
    val id: Long,
    val name: String,
    val information: List<UserInformation>
) {
    fun infoToPrompt(): String {
        return """
            "$name" 님의 정보:
            ${information.joinToString("\n") { it.toDescription() }}
        """.trimIndent()
    }
}
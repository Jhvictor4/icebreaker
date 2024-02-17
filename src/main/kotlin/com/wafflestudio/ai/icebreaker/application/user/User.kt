package com.wafflestudio.ai.icebreaker.application.user

data class User(
    val id: Long,
    var name: String,
    var information: List<UserInformation>,
    val imageSummaryText: String = "",
    var images: List<String> = emptyList()
) {
    fun infoToPrompt(): String {
        return """
            이름: "$name"
            ${information.joinToString("\n") { it.toDescription() }}
        """.trimIndent()
    }

    companion object {
        fun create(): User {
            return User(
                id = 0,
                name = "",
                information = emptyList()
            )
        }
    }
}

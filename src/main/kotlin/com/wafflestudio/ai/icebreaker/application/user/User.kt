package com.wafflestudio.ai.icebreaker.application.user

data class User(
    val id: Long,
    val name: String,
    val information: List<UserInformation>,
    val imageSummaryText: String = "",
    var images: List<String> = emptyList(),
) {
    fun infoToPrompt(): String {
        return """
            이름: "$name"
            ${information.joinToString("\n") { it.toDescription() }}
            나에 관한 사진에 대한 요약: "$imageSummaryText"
        """.trimIndent()
    }
}

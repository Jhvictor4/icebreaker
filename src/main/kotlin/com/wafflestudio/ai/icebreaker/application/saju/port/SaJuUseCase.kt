package com.wafflestudio.ai.icebreaker.application.saju.port

import com.wafflestudio.ai.icebreaker.application.saju.SaJu
import com.wafflestudio.ai.icebreaker.application.saju.십신
import com.wafflestudio.ai.icebreaker.application.user.User
import java.time.LocalDateTime

interface SaJuUseCase {

    fun explainSaJu(
        user: User
    ): String

    fun getSaJu(
        birth: LocalDateTime
    ): SaJuResponse

    fun getSaJuRelation(
        saJu: SaJu,
        another: SaJu
    ): SaJuRelationResponse
}

data class SaJuResponse(
    val saJu: SaJu
)

data class SaJuRelationResponse(
    val tenSpirit: 십신,
    val relation: String,
    val description: String
)

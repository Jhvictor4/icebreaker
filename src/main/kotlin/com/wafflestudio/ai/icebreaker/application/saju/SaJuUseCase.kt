package com.wafflestudio.ai.icebreaker.application.saju

import java.time.LocalDateTime

interface SaJuUseCase {

    fun getSaJu(
        birth: LocalDateTime
    ): SaJuResponse

    fun getSaJuRelation(
        saJu: SaJu,
        another: SaJu,
    ): SaJuRelationResponse

}

data class SaJuResponse(
    val saJu: SaJu,
)

data class SaJuRelationResponse(
    val tenSpirit: 십신,
    val relation: String,
    val description: String,
)
package com.wafflestudio.ai.icebreaker.application.saju

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SaJuUseCaseImpl: SaJuUseCase {
    override fun getSaJu(birth: LocalDateTime): SaJuResponse {
        val saJu = SaJu.from(birth)
        return SaJuResponse(saJu)
    }

    override fun getSaJuRelation(saJu: SaJu, another: SaJu): SaJuRelationResponse {
        val (dominantTenSpirit, _) = saJu.음양오행_8가지.zip(another.음양오행_8가지)
            .map { (a, b) -> 십신.calculate(a, b) }
            .groupingBy { it }
            .eachCount()
            .maxBy { it.value }

        return SaJuRelationResponse(dominantTenSpirit, "둘은 ${dominantTenSpirit.name} 관계에요.", "${dominantTenSpirit.name} 관계는 ~~~ 에요.")
    }
}
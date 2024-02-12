package com.wafflestudio.ai.icebreaker.application.icebreaking

import com.wafflestudio.ai.icebreaker.application.saju.SaJuService
import com.wafflestudio.ai.icebreaker.application.user.BasicInformation
import com.wafflestudio.ai.icebreaker.application.user.BasicInformationMatch
import kotlin.reflect.KClass

typealias Clue = String

enum class IceBreakingTools(
    val informationType: KClass<out BasicInformation>,
    val function: IceBreakingToolFactory.(BasicInformationMatch) -> Clue,
) {
    FETCH_SAJU(
        BasicInformation.Birthday::class,
        { birth -> getSaJu(birth) },
    ),
    SEARCH_KEYWORD(
        BasicInformation.UnderstandingInformation::class,
        { "SEARCH_KEYWORD" }
    );

    companion object {
        fun asPrompt(): String {
            return values()
                .groupBy { it.informationType }
                .map { (type, tools) ->
                    "{\"${type.simpleName}\": [${tools.joinToString(",") { "\"${it.name}\"" }}]"
                }.joinToString("\n")
        }
    }
}

object IceBreakingToolFactory {

    private lateinit var saJuService: SaJuService

    fun init(
        saJuService: SaJuService
    ) {
        this.saJuService = saJuService
    }

    fun getSaJu(birth: BasicInformationMatch): Clue {
        val birthdayA = birth.userA as BasicInformation.Birthday
        val birthdayB = birth.userB as BasicInformation.Birthday
        return """
            userA 사주 : ${saJuService.getSaJu(birthdayA.date)}
            userB 사주 : ${saJuService.getSaJu(birthdayB.date)}
        """.trimIndent()
    }
}

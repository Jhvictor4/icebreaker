package com.wafflestudio.ai.icebreaker.application.icebreaking

import com.wafflestudio.ai.icebreaker.application.saju.SaJuService
import com.wafflestudio.ai.icebreaker.application.user.BasicInformationMatch
import com.wafflestudio.ai.icebreaker.application.user.UserInformation
import kotlin.reflect.KClass

typealias Clue = String

enum class IceBreakingTools(
    val informationType: KClass<out UserInformation>,
    val function: IceBreakingToolFactory.(BasicInformationMatch) -> Clue
) {
    FETCH_SAJU(
        UserInformation.Birthday::class,
        { birth -> getSaJu(birth) }
    ),
    SEARCH_KEYWORD(
        UserInformation.UnderstandingInformation::class,
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
        IceBreakingToolFactory.saJuService = saJuService
    }

    fun getSaJu(birth: BasicInformationMatch): Clue {
        val birthdayA = birth.userA as UserInformation.Birthday
        val birthdayB = birth.userB as UserInformation.Birthday
        return """
            userA 사주 : ${saJuService.getSaJu(birthdayA.date)}
            userB 사주 : ${saJuService.getSaJu(birthdayB.date)}
        """.trimIndent()
    }
}

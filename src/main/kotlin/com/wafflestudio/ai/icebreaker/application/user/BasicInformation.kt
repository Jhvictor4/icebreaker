package com.wafflestudio.ai.icebreaker.application.user

import com.wafflestudio.ai.icebreaker.application.understanding.Understanding
import java.time.LocalDateTime

sealed interface BasicInformation {

    fun toPrompt(): String

    data class Birthday(val date: LocalDateTime) : BasicInformation {
        override fun toPrompt(): String {
            return "{\"Birthday\": \"$date\"}"
        }
    }
    enum class Gender : BasicInformation {
        MALE, FEMALE;

        override fun toPrompt(): String {
            return "{\"Gender\": \"$this\"}"
        }
    }

    data class Major(val major: String) : BasicInformation {
        override fun toPrompt(): String {
            return "{\"Major\": \"$major\"}"
        }
    }

    enum class MBTI : BasicInformation {
        ENFP, ENFJ, ENTJ, ENTP, ESFJ, ESFP, ESTJ, ESTP,
        INFJ, INFP, INTJ, INTP, ISFJ, ISFP, ISTJ, ISTP;

        override fun toPrompt(): String {
            return "{\"MBTI\": \"$this\"}"
        }
    }

    data class UnderstandingInformation(
        val understanding: Understanding,
        val value: String
    ): BasicInformation {
        override fun toPrompt(): String {
            return "{\"${understanding.name}\": \"$value\"}"
        }
    }
}

data class BasicInformationMatch(
    val userA: BasicInformation,
    val userB: BasicInformation
)

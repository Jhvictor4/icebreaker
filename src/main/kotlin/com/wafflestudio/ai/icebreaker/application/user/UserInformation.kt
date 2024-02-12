package com.wafflestudio.ai.icebreaker.application.user

import com.wafflestudio.ai.icebreaker.application.understanding.Understanding
import java.time.LocalDateTime

sealed interface UserInformation {

    // not used
    fun toPrompt(): String
    fun toDescription(): String

    data class Birthday(val date: LocalDateTime) : UserInformation {
        override fun toPrompt(): String {
            return "{\"Birthday\": \"$date\"}"
        }

        override fun toDescription(): String {
            return "생년월일: ${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일에 태어났어요."
        }
    }
    enum class Gender : UserInformation {
        MALE, FEMALE;

        override fun toPrompt(): String {
            return "{\"Gender\": \"$this\"}"
        }

        override fun toDescription(): String {
            return "성별: 성별은 ${this.name}에요."
        }
    }

    data class Major(val major: String) : UserInformation {
        override fun toPrompt(): String {
            return "{\"Major\": \"$major\"}"
        }

        override fun toDescription(): String {
            return "전공: 대학교에서는 $major 전공을 하고 있어요."
        }
    }

    enum class MBTI : UserInformation {
        ENFP, ENFJ, ENTJ, ENTP, ESFJ, ESFP, ESTJ, ESTP,
        INFJ, INFP, INTJ, INTP, ISFJ, ISFP, ISTJ, ISTP;

        override fun toPrompt(): String {
            return "{\"MBTI\": \"$this\"}"
        }

        override fun toDescription(): String {
            return "MBTI: MBTI는 ${this.name}에요."
        }
    }

    data class UnderstandingInformation(
        val understanding: Understanding,
        val value: String
    ) : UserInformation {
        override fun toPrompt(): String {
            return "{\"${understanding.name}\": \"$value\"}"
        }

        override fun toDescription(): String {
            return "이해하는 데 도움이 될만한 글의 발췌: $value"
        }
    }
}

data class BasicInformationMatch(
    val userA: UserInformation,
    val userB: UserInformation
)

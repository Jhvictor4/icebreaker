package com.wafflestudio.ai.icebreaker.application.user

import com.wafflestudio.ai.icebreaker.application.understanding.Understanding
import net.minidev.json.annotate.JsonIgnore
import java.time.LocalDateTime

sealed interface UserInformation {

    enum class UserInformationType {
        BIRTHDAY, GENDER, MAJOR, MBTI, UNDERSTANDING, IMAGE_SUMMARY, IMAGE_URL, LOCATION,
    }

    val type: UserInformationType
    val value: Any

    // not used
    fun toPrompt(): String
    fun toDescription(): String

    data class Birthday(val date: LocalDateTime) : UserInformation {

        override val value: String
            get() = date.toString()

        override fun toPrompt(): String {
            return "{\"Birthday\": \"$date\"}"
        }

        override fun toDescription(): String {
            return "생년월일: ${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일에 태어났어요."
        }

        @JsonIgnore
        override val type: UserInformationType = UserInformationType.BIRTHDAY
    }
    enum class Gender : UserInformation {
        MALE, FEMALE;

        override val value: String
            get() = this.name

        override fun toPrompt(): String {
            return "{\"Gender\": \"$this\"}"
        }

        override fun toDescription(): String {
            return "성별: 성별은 ${this.name}에요."
        }

        @JsonIgnore
        override val type: UserInformationType = UserInformationType.GENDER
    }

    data class Major(val major: String) : UserInformation {

        override val value: String
            get() = major

        override fun toPrompt(): String {
            return "{\"Major\": \"$major\"}"
        }

        override fun toDescription(): String {
            return "전공: 대학교에서는 $major 전공을 하고 있어요."
        }

        @JsonIgnore
        override val type: UserInformationType = UserInformationType.MAJOR
    }

    enum class MBTI : UserInformation {
        ENFP, ENFJ, ENTJ, ENTP, ESFJ, ESFP, ESTJ, ESTP,
        INFJ, INFP, INTJ, INTP, ISFJ, ISFP, ISTJ, ISTP;

        override val value: String
            get() = name

        override fun toPrompt(): String {
            return "{\"MBTI\": \"$this\"}"
        }

        override fun toDescription(): String {
            return "MBTI: MBTI는 ${this.name}에요."
        }

        @JsonIgnore
        override val type: UserInformationType = UserInformationType.MBTI
    }

    data class Location(val location: String) : UserInformation {
        @get:JsonIgnore
        override val type: UserInformationType
            get() = UserInformationType.LOCATION

        override val value: Any
            get() = location

        override fun toPrompt(): String {
            return "{\"LOCATION\": \"$this\"}"
        }

        override fun toDescription(): String {
            return "LOCATION: 사는 지역은 ${location}에요."
        }

    }


    data class ImageSummary(val text: String) : UserInformation {

        override val value: String
            get() = text

        override fun toPrompt(): String {
            return "{\"ImageSummaryText\": \"${text}\"}"
        }

        override fun toDescription(): String {
            return "나에 관한 사진에 대한 요약: $text"
        }

        @JsonIgnore
        override val type: UserInformationType = UserInformationType.IMAGE_SUMMARY
    }

    data class ImageUrl(val imageUrl: List<String>) : UserInformation {

        override val value: List<String>
            get() = imageUrl

        override fun toPrompt(): String {
            return ""
        }

        override fun toDescription(): String {
            return ""
        }

        @JsonIgnore
        override val type: UserInformationType = UserInformationType.IMAGE_URL
    }

    data class UnderstandingInformation(
        val understanding: Understanding,
        override val value: String
    ) : UserInformation {
        override fun toPrompt(): String {
            return "{\"${understanding.name}\": \"$value\"}"
        }

        override fun toDescription(): String {
            return "이해하는 데 도움이 될만한 글의 발췌: $value"
        }

        @JsonIgnore
        override val type: UserInformationType = UserInformationType.UNDERSTANDING
    }
}

data class BasicInformationMatch(
    val userA: UserInformation,
    val userB: UserInformation
)

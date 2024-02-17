package com.wafflestudio.ai.icebreaker.outbound.user

import com.wafflestudio.ai.icebreaker.application.common.objectMapper
import com.wafflestudio.ai.icebreaker.application.user.User
import com.wafflestudio.ai.icebreaker.application.user.UserInformation
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("ice_breaking_users")
data class UserEntity(
    @Id
    @Column("id")
    val id: Long,
    @Column("name")
    val name: String,
    @Column("detail")
    val detail: UserInformationEntity
) {
    fun toUser(): User {
        return User(
            id = id,
            name = name,
            information = detail.detail.map {
                val javaClass = when (it.type) {
                    UserInformation.UserInformationType.MBTI -> UserInformation.MBTI::class.java
                    UserInformation.UserInformationType.GENDER -> UserInformation.Gender::class.java
                    UserInformation.UserInformationType.BIRTHDAY -> UserInformation.Birthday::class.java
                    UserInformation.UserInformationType.MAJOR -> UserInformation.Major::class.java
                    UserInformation.UserInformationType.UNDERSTANDING -> UserInformation.UnderstandingInformation::class.java
                    UserInformation.UserInformationType.IMAGE_SUMMARY -> UserInformation.ImageSummary::class.java
                    UserInformation.UserInformationType.IMAGE_URL -> UserInformation.ImageUrl::class.java
                }

                objectMapper.readValue(it.value, javaClass)
            }
        )
    }

    companion object {
        fun from(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                name = user.name,
                detail = UserInformationEntity(
                    detail = user.information.map {
                        UserInformationEntity.UserInformationStringWithType(
                            type = it.type,
                            value = objectMapper.writeValueAsString(it)
                        )
                    }
                )
            )
        }
    }
}

// converter 에서 콜렉션 직접 변환을 지원하지 않아서 wrapping. entity level 에서만
data class UserInformationEntity(
    val detail: List<UserInformationStringWithType>
) {
    data class UserInformationStringWithType(
        val type: UserInformation.UserInformationType,
        val value: String
    )
}

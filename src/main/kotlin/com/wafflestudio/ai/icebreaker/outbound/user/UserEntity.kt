package com.wafflestudio.ai.icebreaker.outbound.user

import com.wafflestudio.ai.icebreaker.application.user.User
import com.wafflestudio.ai.icebreaker.application.user.UserInformation
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("icebreaking_user")
data class UserEntity(
    @Id
    @Column("id")
    val id: Long,
    @Column("name")
    val name: String,
    @Column("detail")
    val detail: List<UserInformation>
) {
    fun toUser(): User {
        return User(
            id = id,
            name = name,
            information = detail
        )
    }

    companion object {
        fun from(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                name = user.name,
                detail = user.information
            )
        }
    }
}

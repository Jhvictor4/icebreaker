package com.wafflestudio.ai.icebreaker.outbound.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.ai.icebreaker.outbound.user.UserInformationEntity
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@ReadingConverter
class UserDetailReadConverter(
    private val objectMapper: ObjectMapper
) : Converter<String, UserInformationEntity> {

    override fun convert(source: String): UserInformationEntity {
        return objectMapper.readValue(source, UserInformationEntity::class.java)
    }
}

@WritingConverter
class UserDetailWriteConverter(
    private val objectMapper: ObjectMapper
) : Converter<UserInformationEntity, String> {

    override fun convert(source: UserInformationEntity): String {
        return objectMapper.writeValueAsString(source)
    }
}

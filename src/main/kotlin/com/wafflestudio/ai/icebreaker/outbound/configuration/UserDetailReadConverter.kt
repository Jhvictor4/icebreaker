package com.wafflestudio.ai.icebreaker.outbound.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.ai.icebreaker.application.user.User
import com.wafflestudio.ai.icebreaker.application.user.UserInformation
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@ReadingConverter
class UserDetailReadConverter(
    private val objectMapper: ObjectMapper
) : Converter<String, List<UserInformation>> {

    override fun convert(source: String): List<UserInformation> {
        return objectMapper.readValue(source, objectMapper.typeFactory.constructCollectionType(List::class.java, UserInformation::class.java))
    }
}

@WritingConverter
class UserDetailWriteConverter(
    private val objectMapper: ObjectMapper
) : Converter<List<UserInformation>, String> {

    override fun convert(source: List<UserInformation>): String {
        return objectMapper.writeValueAsString(source)
    }
}

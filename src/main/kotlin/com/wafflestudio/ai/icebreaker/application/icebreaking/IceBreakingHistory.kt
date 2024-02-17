package com.wafflestudio.ai.icebreaker.application.icebreaking

import com.fasterxml.jackson.module.kotlin.readValue
import com.wafflestudio.ai.icebreaker.application.common.ObjectMapper

data class IceBreakingHistory(
    val id: Long,
    val meetUpId: String,
    val type: Type,
    val json: String,
) {
    fun toResponse(): IceBreakingStreamResponse {
        return when (type) {
            Type.THOUGHT -> ObjectMapper.readValue<IceBreakingStreamResponse.Thought>(json)
            Type.RESPONSE -> ObjectMapper.readValue<IceBreakingStreamResponse.FinalQuestion>(json)
        }
    }

    enum class Type {
        THOUGHT,
        RESPONSE
    }

    companion object {
        fun from(meetUpId: String, response: IceBreakingStreamResponse): IceBreakingHistory {
            return when (response) {
                is IceBreakingStreamResponse.Thought -> IceBreakingHistory(
                    id = 0,
                    meetUpId = meetUpId,
                    type = Type.THOUGHT,
                    json = ObjectMapper.writeValueAsString(response)
                )
                is IceBreakingStreamResponse.FinalQuestion -> IceBreakingHistory(
                    id = 0,
                    meetUpId = meetUpId,
                    type = Type.RESPONSE,
                    json = ObjectMapper.writeValueAsString(response)
                )
            }
        }
    }
}

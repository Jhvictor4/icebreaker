package com.wafflestudio.ai.icebreaker.application.icebreaking

import com.aallam.openai.api.chat.ToolCall
import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.common.ChatGptPort
import com.wafflestudio.ai.icebreaker.application.common.ChatGptResponseDto
import com.wafflestudio.ai.icebreaker.application.common.WeaviatePort
import com.wafflestudio.ai.icebreaker.application.saju.SaJuService
import com.wafflestudio.ai.icebreaker.application.user.User
import org.springframework.stereotype.Component

@Component
class IceBreakingToolsCallableRegistry(
    private val saJuService: SaJuService,
    private val weaviatePort: WeaviatePort,
    private val objectMapper: ObjectMapper,
    private val gptPort: ChatGptPort
) {

    operator fun get(functionName: String): suspend (ToolCallWrapper) -> String {
        return when (functionName) {
            "getSaJu" -> { data ->
                val userA = saJuService.explainSaJu(data.userA)
                val userB = saJuService.explainSaJu(data.userB)
                "사주팔자 of ${data.userA.name}: $userA \n 사주팔자 of ${data.userB.name}: $userB" // TODO
            }
            "searchByKeyword" -> { data ->
                val keyword = objectMapper.readValue(data.toolCall.function.argumentsOrNull.toString(), Map::class.java)["keyword"] as? String
                "Information of ${data.userA.name} about $keyword: Nothing useful. \n Information of ${data.userB.name} about $keyword: Nothing useful." // TODO
            }
            "askGpt" -> { data ->
                val query = objectMapper.readValue(data.toolCall.function.argumentsOrNull.toString(), Map::class.java)["query"] as? String
                if (query.orEmpty().isEmpty()) {
                    ""
                } else {
                    val response = gptPort.createChat(query!!) as? ChatGptResponseDto.Message
                    response?.message ?: ""
                }
            }
            "generateResult" -> { data ->
                val query = objectMapper.readValue(data.toolCall.function.argumentsOrNull.toString(), Map::class.java)["query"] as? String
                if (query.orEmpty().isEmpty()) {
                    ""
                } else {
                    val response = gptPort.createChat(
                        """
                          Format the result of the ice breaking questions as a json:
                          
                          --- Example ---
                          Result:
                          
                          <RESPONSE>
                          {"result": ["컴퓨터공학에 관심을 가진 이유와 현재 진행 중인 프로젝트에 대해 이야기해보세요.", "두 분 모두에게 현재 가장 열정적으로 하는 일과 목표에 대해 얘기해보세요."]}
                          </RESPONSE>

                          다음과 같은 질문으로 대화를 시작해보세요:
                          
                          Extracted:
                          {"result": ["컴퓨터공학에 관심을 가진 이유와 현재 진행 중인 프로젝트에 대해 이야기해보세요.", "두 분 모두에게 현재 가장 열정적으로 하는 일과 목표에 대해 얘기해보세요."]}
                          --- Example ---

                            
                          Result:
                          $query
                          
                          Extracted:
                        """.trimIndent()
                    ) as? ChatGptResponseDto.Message
                    response?.message ?: ""
                }
            }
            else -> { _ ->
                logger.error { "Unknown function name: $functionName" }
                "Couldn't find anything useful."
            }
        }
    }

    companion object : Log
}

data class ToolCallWrapper(
    val toolCall: ToolCall.Function,
    val userA: User,
    val userB: User
)

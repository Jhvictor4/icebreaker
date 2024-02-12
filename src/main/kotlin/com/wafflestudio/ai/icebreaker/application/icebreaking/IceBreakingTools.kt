package com.wafflestudio.ai.icebreaker.application.icebreaking

import com.aallam.openai.api.chat.FunctionTool
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.chat.ToolType
import com.aallam.openai.api.core.Parameters
import kotlinx.serialization.json.*

enum class IceBreakingTools {
    FORTUNE_TELLING {
        override fun toChatGptTool(): Tool {
            return Tool(
                ToolType.Function,
                "can fetch 사주팔자 of each user based on their birthday, and interpretation of it.",
                FunctionTool(
                    name = "getSaJu",
                    parameters = Parameters.Empty
                )
            )
        }
    },
    SEARCH_KEYWORD {
        override fun toChatGptTool(): Tool {
            return Tool(
                ToolType.Function,
                "can fetch more information about the user with certain keyword.",
                FunctionTool(
                    name = "searchByKeyword",
                    parameters = Parameters.buildJsonObject {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("keyword") {
                                put("type", "string")
                                put("description", "The keyword to search for")
                            }
                        }
                        putJsonArray("required") {
                            add("keyword")
                        }
                    }
                )
            )
        }
    };

    abstract fun toChatGptTool(): Tool
}

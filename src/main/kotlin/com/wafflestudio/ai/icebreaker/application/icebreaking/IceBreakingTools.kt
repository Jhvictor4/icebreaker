package com.wafflestudio.ai.icebreaker.application.icebreaking

import com.aallam.openai.api.chat.FunctionTool
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.chat.ToolType
import com.aallam.openai.api.core.Parameters
import kotlinx.serialization.json.*

enum class IceBreakingTools(
    val functionName: String
) {
    // TODO MBTI 추가
    FORTUNE_TELLING("getSaJu") {
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
    SEARCH_KEYWORD("searchByKeyword") {
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
    },
    IMAGE_COMPARISON("image_comparison") {
        override fun toChatGptTool(): Tool {
            return Tool(
                ToolType.Function,
                "compare actual images, not just summarize the content of the images.",
                FunctionTool(
                    name = "image_comparison",
                    parameters = Parameters.Empty
                )
            )
        }
    },
    ASK_GPT("askGpt") {
        override fun toChatGptTool(): Tool {
            return Tool(
                ToolType.Function,
                "useful for when you want to generate text with ChatGPT",
                FunctionTool(
                    name = "askGpt",
                    parameters = Parameters.buildJsonObject {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("query") {
                                put("type", "string")
                                put("description", "Query to ask GPT")
                            }
                        }
                        putJsonArray("required") {
                            add("query")
                        }
                    }
                )
            )
        }
    },
    GENERATE_RESULT("generateResult") {
        override fun toChatGptTool(): Tool {
            return Tool(
                ToolType.Function,
                "when you are done making suggestions and want to format the result",
                FunctionTool(
                    name = "askGpt",
                    parameters = Parameters.buildJsonObject {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("query") {
                                put("type", "string")
                                put("description", "Query to format")
                            }
                        }
                        putJsonArray("required") {
                            add("query")
                        }
                    }
                )
            )
        }
    };

    abstract fun toChatGptTool(): Tool

    companion object {
        val entriesAsChatGptTools = entries.map { it.toChatGptTool() }
    }
}

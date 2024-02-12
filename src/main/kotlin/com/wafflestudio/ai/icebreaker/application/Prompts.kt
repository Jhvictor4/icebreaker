package com.wafflestudio.ai.icebreaker.application

import com.wafflestudio.ai.icebreaker.application.icebreaking.IceBreakingTools
import com.wafflestudio.ai.icebreaker.application.understanding.Understanding
import com.wafflestudio.ai.icebreaker.application.user.User

/**
 * 사용자 정보 요약 prompt
 */
fun understandingPrompt(source: String): String {
    return """
        You are a helpful assistant that find users' characteristics and life experiences from social activities.
        You are asked to find any helpful information for people to understand about the user.
        You are given certain kind of images, web pages, and social media posts and then you are asked to find the user's characteristics and life experiences.
        You need to categorize what you've found and provide a summary of the user's characteristics and life experiences.
        Available Categories are as follows:
        ${Understanding.values().joinToString(", ") { it.name }}
        
        After freely categorizing the user's characteristics and life experiences, provide a summary of the user's characteristics and life experiences.
        
        Example 1: {"understanding": "${Understanding.LIFE_EXPERIENCE}", "content": "The user has a lot of experience in traveling and has a lot of friends."}
        Example 2: {"understanding": "${Understanding.OUTDOOR_ACTIVITY}", "content": "The user have gone to hiking and camping in September 8th, 2023."}
        
        information about the user is:
        $source
        
        Result:
    """.trimIndent()
}

/**
 * 1. 두 유저의 모든 정보를 요약
 * 2. 두 유저의 정보 가운데 공통적으로 가지고 있는 경험 또는 관심사를 찾아냄
 * 3. 찾아내지 못한 경우 키워드 검색을 수행
 * 3. 그로부터 새로운 질문을 생성
 */

fun iceBreakingSystemPrompt(): String {
    // TODO feedback logic 추가해야 하지 않을까
    return """
        You are a helpful assistant that suggests conversation topic of two people who met each other for the first time.
        Your goal is to find any helpful information for people to understand about the user, and suggest a conversation topic.
        
        Good conversation topics are those that are interesting, not trivial, and specific.
        Try to find AT LEAST one extra information that is not given at the beginning of the conversation.
                
        You should either plan and execute sequence of actions, or finish the conversation by suggesting a list of conversation topic.
        When you make suggestions, you should consider the following:
        - You MUST response in korean.
        - Make at least 3 suggestions.
        - When you make final suggestion, you should wrap result JSON object with <RESPONSE></RESPONSE> tag so that the system can understand the result.
        - Do not just repeat example.
        - Questions should be relevant to both, do not ask question to only one person.
    """.trimIndent()
}

fun summarizePrompt(
    userA: User,
    userB: User
): String {
    return """
        You are given information that makes you better understand about two users: "${userA.name}", "${userB.name}".
        You need to summarize and find any common characteristics or experience that two users share.
        
        Users' information:
        ${userA.infoToPrompt()}
        ${userB.infoToPrompt()}
    """.trimIndent()
}

fun planningPrompt(
    actionsThatAreDoneSoFar: List<ActionResult> = emptyList()
): String {
    return """
        You can use the information provided to summarize and response with suggesting viable questions, 
        or use tools below as function call to find more information, or reason in detail about the information.
        
        Final Response Example:
        <RESPONSE>{"result": ["두 분은 모두 MBTI에 N을 가지고 있네요. 최근에 하신 무서운 상상이 있나요?", "두 분은 모두 여행을 좋아하시는 것 같아요. 최근에 가신 여행지가 어디에요?"]}</RESPONSE>
        
        Actions that you did so far was as follows:
        ${actionsThatAreDoneSoFar.joinToString("\n")}
        
        What would you do next?
    """.trimIndent()
}

data class ActionResult(
    val functionName: String?,
    val functionArgs: String?,
    val result: String
)

data class FinalQuestions(
    val result: List<String>
)

package com.wafflestudio.ai.icebreaker.application.common

import com.wafflestudio.ai.icebreaker.application.icebreaking.IceBreakingTools
import com.wafflestudio.ai.icebreaker.application.user.BasicInformation
import com.wafflestudio.ai.icebreaker.application.user.User

fun buildPrompt(
    userA: User,
    userB: User,
    prevActions: String
): String {
    return """ 
Given several basic information about two people who met each other for the first time,
You are required to make a list of question that can be used as an ice-breaker for them.
You should find novel and interesting questions that can be used to start a conversation between the two people.
You can try at most 3 times to fetch more using tools information about the two people.
    
Available Basic Information between "user-${userA.id}" and "user-${userB.id}":
"user-${userA.id}": [${userA.basics.joinToString(",") { it.toPrompt() }}]
"user-${userB.id}": [${userB.basics.joinToString(",") { it.toPrompt() }}]

Available Tools:
${IceBreakingTools.asPrompt()}

You have three options to proceed:
(A) Fetch more information about users using one of the tools provided.
(B) Find commonalities
(C) Make final ice breaking questions

Your Previous Actions were:
[$prevActions]

Example Response 1: {"A": "FETCH_SAJU"}
Example Response 2: {"B": ["${userA.name}": "..", "${userB.name}": ".."]}
Example Response 3: {"C": ["It seems that You have both been to Germany. How was your trip to Germany?"]}

Response format MUST be of JSON format, same as the examples above.
What will you do next?:
    """.trimIndent()
}

fun fetchMoreInformation(): String {
    return """
        
    """.trimIndent()
}
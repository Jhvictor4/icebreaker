package com.wafflestudio.ai.icebreaker.application.understanding

import com.wafflestudio.ai.icebreaker.application.common.ChatGptResponseDto

interface UnderstandingUseCase {

    fun understandByUri(uri: String): ChatGptResponseDto?
}

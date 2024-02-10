package com.wafflestudio.ai.icebreaker.application.understanding

import com.wafflestudio.ai.icebreaker.application.common.ChatGptMessageResponseDto

interface UnderstandingUseCase {

    fun understandByUri(uri: String): ChatGptMessageResponseDto?
}

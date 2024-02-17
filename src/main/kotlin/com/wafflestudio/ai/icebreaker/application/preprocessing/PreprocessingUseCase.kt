package com.wafflestudio.ai.icebreaker.application.preprocessing

import com.wafflestudio.ai.icebreaker.application.common.ChatGptResponseDto

interface PreprocessingUseCase {

    fun summarizeImages(
        images: List<String>
    ): ChatGptResponseDto?
}

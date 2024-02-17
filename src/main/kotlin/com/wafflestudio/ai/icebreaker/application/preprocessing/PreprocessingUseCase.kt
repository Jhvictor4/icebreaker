package com.wafflestudio.ai.icebreaker.application.preprocessing

interface PreprocessingUseCase {

    fun summarizeImages(
        id: Long,
        images: List<String>
    )
}

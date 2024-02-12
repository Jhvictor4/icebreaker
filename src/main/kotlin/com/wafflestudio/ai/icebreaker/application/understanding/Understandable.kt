package com.wafflestudio.ai.icebreaker.application.understanding

enum class Understandable {
    URI, TEXT, IMAGE
}

enum class Understanding(val desc: String) {
    LIFE_EXPERIENCE("경험"),
    OUTDOOR_ACTIVITY(""),
    PLACES_VISITED("가본 장소"),
    JOB("직업")
}

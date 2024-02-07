package com.wafflestudio.ai.icebreaker.application

import mu.KotlinLogging

interface Log {
    val logger get() = KotlinLogging.logger { }
}

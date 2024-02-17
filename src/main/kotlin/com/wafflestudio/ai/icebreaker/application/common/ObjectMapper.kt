package com.wafflestudio.ai.icebreaker.application.common

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val objectMapper = jacksonObjectMapper()
    .registerModules(JavaTimeModule())

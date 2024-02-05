package com.wafflestudio.ai.icebreaker.application.configuration

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlin.coroutines.CoroutineContext

@OptIn(DelicateCoroutinesApi::class)
val coroutineContext: CoroutineContext = newFixedThreadPoolContext(50, "wafflestudio") + SupervisorJob()

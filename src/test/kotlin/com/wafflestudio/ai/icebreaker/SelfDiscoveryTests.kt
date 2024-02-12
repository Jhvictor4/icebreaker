package com.wafflestudio.ai.icebreaker

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wafflestudio.ai.icebreaker.application.common.ChatGptPort
import com.wafflestudio.ai.icebreaker.application.common.ICE_BREAKING_TASK
import com.wafflestudio.ai.icebreaker.application.user.BasicInformation
import com.wafflestudio.ai.icebreaker.application.user.User
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class SelfDiscoveryTests @Autowired constructor(
    private val chatGptPort: ChatGptPort
) {

    @Test
    fun selfDiscoveryTest(): Unit = runBlocking {
        val userA = User(1, setOf(BasicInformation.Birthday(LocalDate.parse("1995-01-01").atStartOfDay())))
        val userB = User(2, setOf(BasicInformation.Birthday(LocalDate.parse("1996-01-01").atStartOfDay())))
        val task = ICE_BREAKING_TASK(userA.basics, userB.basics)
        val result = chatGptPort.selfDiscovery(task)
        val parsed = jacksonObjectMapper().readValue(result?.message, Any::class.java)
        assert(parsed != null)
    }
}

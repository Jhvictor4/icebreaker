package com.wafflestudio.ai.icebreaker.application.configuration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
class SecretsManagerConfig : BeanFactoryPostProcessor {

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val secretNames = listOf("dev/jihyeok.kang")
        val region: Region = Region.AP_NORTHEAST_2
        val objectMapper = jacksonObjectMapper()

        secretNames.forEach { secretName ->
            val secretString = getSecretString(secretName, region)
            val map = objectMapper.readValue<Map<String, String>>(secretString)
            map.forEach { (key, value) -> System.setProperty(key, value) }
        }
    }

    fun getSecretString(secretName: String, region: Region): String {
        val client = SecretsManagerClient.builder().region(region).build()
        val request = GetSecretValueRequest.builder().secretId(secretName).build()
        return client.getSecretValue(request).secretString()
    }
}

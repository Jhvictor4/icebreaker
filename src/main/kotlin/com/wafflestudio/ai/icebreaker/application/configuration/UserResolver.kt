package com.wafflestudio.ai.icebreaker.application.configuration

import com.wafflestudio.ai.icebreaker.api.ApplicationException
import com.wafflestudio.ai.icebreaker.api.JwtProvider
import com.wafflestudio.ai.icebreaker.application.user.User
import com.wafflestudio.ai.icebreaker.outbound.user.UserRepository
import kotlinx.coroutines.reactor.mono
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class UserResolver(
    private val userRepository: UserRepository
) : WebFluxConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedMethods("*")
            .allowedOrigins("*")
    }

    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(UserHandlerMethodArgumentResolver())
        super.configureArgumentResolvers(configurer)
    }

    private inner class UserHandlerMethodArgumentResolver : HandlerMethodArgumentResolver {
        override fun supportsParameter(parameter: MethodParameter): Boolean {
            return parameter.parameterType == User::class.java
        }

        override fun resolveArgument(
            parameter: MethodParameter,
            bindingContext: BindingContext,
            exchange: ServerWebExchange
        ): Mono<Any> {
            val header = requireNotNull(exchange.request.headers["Authorization"]?.firstOrNull()) {
                "Authorization header is required"
            }

            JwtProvider.validateToken(header)
            val userId = JwtProvider.getPayload(header).toLongOrNull()
                ?: throw ApplicationException.Common("Invalid token")

            return mono {
                userRepository.getUser(userId)
                    ?: throw ApplicationException.Common("User not found")
            }
        }
    }
}

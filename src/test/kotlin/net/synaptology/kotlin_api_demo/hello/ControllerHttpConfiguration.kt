package net.synaptology.kotlin_api_demo.hello

import org.springframework.context.annotation.Bean

class ControllerHttpConfiguration {

    @Bean
    fun controllerHttpImpl(): ControllerHttp {
        return ControllerHttp()
    }

}

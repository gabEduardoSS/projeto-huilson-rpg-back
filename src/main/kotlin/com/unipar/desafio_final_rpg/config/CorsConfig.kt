package com.unipar.desafio_final_rpg.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

// Arquivo de configuração do CORS, que bloqueia requisições HTTP vinda de origens não aceitas por ele
@Configuration
class CorsConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*") // Aceita todas as origens
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Aceita esses métodos
            .allowedHeaders("*") // Aceita todos os headers
    }
}
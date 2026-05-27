package com.unipar.desafio_final_rpg.config

import com.unipar.desafio_final_rpg.ChatHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


// Arquivo de configuração do WebSocket (Túnel de conexão)
@Configuration
@EnableWebSocket
class WebSocketConfig(private val chatHandler: ChatHandler) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        // Define onde ele vai ser gerenciado e o caminho para ele, além das origens permitidas
        registry.addHandler(chatHandler, "/api/log").setAllowedOrigins("*")
    }
}
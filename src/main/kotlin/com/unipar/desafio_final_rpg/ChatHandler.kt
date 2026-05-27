package com.unipar.desafio_final_rpg

import com.unipar.desafio_final_rpg.service.BatalhaService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue
import java.util.concurrent.CopyOnWriteArrayList

// 1. Criamos um modelo para a mensagem.
// O Jackson usará isso para transformar o JSON do React em Objeto Kotlin e vice-versa.
data class Mensagem(
    val id: Long?,
    val tipo: String?,
    val action: String?,
    val text: String?,
    val userName: String
)

@Component
class ChatHandler(private val batalhaService: BatalhaService) : TextWebSocketHandler() {

    private val sessions = CopyOnWriteArrayList<WebSocketSession>()
    private val mapper = jacksonObjectMapper()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            val dados = mapper.readValue<Mensagem>(message.payload)
            when (dados.tipo) {
                "BATTLE_JOIN" -> {
                    val log = batalhaService.entrarNaArena(dados.userName, dados.id?:0)
                    broadcast(Mensagem(tipo = "BATTLE_LOG", text = log, userName = "Servidor", action = "", id=null))
                }
                "BATTLE_ACTION" -> {
                    val (log, battleEnded) = batalhaService.executarAcao(nomeAtacante = dados.userName, acao = dados.action?:"")
                    broadcast(Mensagem(tipo = "BATTLE_LOG", text = log, userName = "Servidor", action = null, id=null))
                    if(battleEnded){
                        broadcast(Mensagem(tipo = "BATTLE_END", text = log, userName = "Servidor", action = null, id=null))
                    }
                }
                "CHAT" -> {
                    // Repassa o chat livremente
                    broadcast(dados)
                }
            }
        } catch (e: Exception) {
            println("Erro no WebSocket: ${e.message}")
        }
    }

    private fun broadcast(obj: Mensagem) {
        val json = mapper.writeValueAsString(obj)
        sessions.forEach { s ->
            if (s.isOpen) s.sendMessage(TextMessage(json))
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
    }
}
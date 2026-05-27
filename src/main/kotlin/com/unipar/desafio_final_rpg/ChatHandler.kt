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

// O arquivo que controla as requisições do WebSocket da batalha e do chat, que necessitam um tempo de resposta melhor que requisições HTTP normal
// O WebSocket abre um túnel que permite que o front-end e o back-end troquem mensagens livremente, sem depender de um requisitar e outro responder,
//  como em requisições normais
data class Mensagem(
    val id: Long?,
    val tipo: String?,
    val action: String?,
    val text: String?,
    val userName: String
) // Define a estrutura da mensagem que vem do front-end

@Component
class ChatHandler(private val batalhaService: BatalhaService) : TextWebSocketHandler() {

    private val sessions = CopyOnWriteArrayList<WebSocketSession>() // Lista de sessões ativas no site
    private val mapper = jacksonObjectMapper()  // Mapeador de Json para objetos kotlin

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session) // Adiciona a sessão à lista se a conexão for bem sucedida
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            val dados = mapper.readValue<Mensagem>(message.payload) // Mapeia os dados vindo do front para o objeto
            when (dados.tipo) { // Confere qual o tipo de dado(Entrar na batalha, sair da batalha, ação ou chat)
                "BATTLE_JOIN" -> {
                    val log = batalhaService.entrarNaArena(dados.userName, dados.id?:0) // Chama a função para inserir o usuário na arena
                    broadcast(Mensagem(tipo = "BATTLE_LOG", text = log, userName = "Servidor", action = "", id=null)) // Retorna o log para o front-end adicionar aos logs
                }
                "BATTLE_ACTION" -> {
                    val (log, battleEnded) = batalhaService.executarAcao(nomeAtacante = dados.userName, acao = dados.action?:"") // Chama a função para executar ação ou acabar a batalha
                    broadcast(Mensagem(tipo = "BATTLE_LOG", text = log, userName = "Servidor", action = null, id=null)) // Retorna o log para o front-end adicionar aos logs
                    if(battleEnded){ // Caso a batalha tenha acabado
                        broadcast(Mensagem(tipo = "BATTLE_END", text = log, userName = "Servidor", action = null, id=null)) // Retorna o log para o front-end adicionar aos logs
                    }
                }
                "CHAT" -> {
                    broadcast(dados) // Envia a mensagem para o front-end atualizar o chat para os usuários
                }
            }
        } catch (e: Exception) {
            println("Erro no WebSocket: ${e.message}")
        }
    }

    private fun broadcast(obj: Mensagem) { // Envia para TODAS as sessões no site a mensagem que foi recebida, após realizar as ações relativas à ela
        val json = mapper.writeValueAsString(obj)
        sessions.forEach { s ->
            if (s.isOpen) s.sendMessage(TextMessage(json))
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session) // Remove a sessão da lista quando um usuário desloga
    }
}
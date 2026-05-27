package com.unipar.desafio_final_rpg.service

import com.unipar.desafio_final_rpg.model.* // Importe suas entidades Mago, Guerreiro, etc.
import com.unipar.desafio_final_rpg.repository.PersonagemRepository
import org.springframework.stereotype.Service
import java.util.Optional
import kotlin.random.Random

@Service
class BatalhaService(private val repository: PersonagemRepository) {

    // Guarda quem está na arena
    private var jogador1: Personagem? = null
    private var jogador2: Personagem? = null
    private var nomeJogadorDaVez: String = ""

    // Função para mostrar os atributos dos personagens no chat
    fun mostrarStats(): String{
        return "${jogador1?.nome}:  ❤️${jogador1?.vida} | 🏃${jogador1?.velocidade} | 💪${jogador1?.forca}\n${jogador2?.nome}:  ❤️${jogador2?.vida} | 🏃${jogador2?.velocidade} | 💪${jogador2?.forca}"

    }

    // Função para resetar a vida dos personagens pra 100(padrão)
    fun resetStats(personagem: Personagem){
        personagem.vida = 100
        repository.save(personagem)
    }

    // Função que insere os personagens na arena
    fun entrarNaArena(nomeJogador: String, id: Long): String {
        val personagem: Personagem = repository.findById(id).orElse(null) // Verifica se o personagem está no banco
            ?: return "Erro: Personagem [$nomeJogador] não encontrado no banco!"

        if (jogador1?.id == id || jogador2?.id == id) { // Verifica se o jogador já está na arena(caso tentar entrar novamente)
            return "$nomeJogador já está na arena!"
        }

        return if (jogador1 == null) {
            jogador1 = personagem
            "⚔️ $nomeJogador entrou na arena e aguarda um oponente!" // Quando o primeiro jogador entrar ele entra em espera
        } else if (jogador2 == null) {
            jogador2 = personagem
            nomeJogadorDaVez = jogador1!!.nome
            "⚔️ $nomeJogador entrou na arena! A BATALHA VAI COMEÇAR!\n${mostrarStats()}\nÉ a vez de $nomeJogadorDaVez." // Quando o segundo jogador entrar começa a batalha
        } else {
            "A arena já está lotada! Aguarde a batalha terminar."
        }
    }

    fun executarAcao(nomeAtacante: String, acao: String): Pair<String, Boolean> {
        if(jogador2 == null && acao.uppercase() == "FUGIR"){ // Caso um jogador em espera fuja da arena
            limparArena() // tira os jogadores da arena e deixa ela vazia
            return Pair("🏃 $nomeAtacante correu antes mesmo da batalha começar", true) // Retorna um log e define que a batalha terminou
        }
        if (jogador1 == null || jogador2 == null) return Pair("Aguardando mais guerreiros na arena...", false) // Caso não haja algum do dois na arena retorna um log e define que a luta ainda não acabou
        if (nomeAtacante != nomeJogadorDaVez) return Pair("Calma, $nomeAtacante! Ainda não é a sua vez.", false) // Retorna um log caso o jogador que não está na vez tenta atacar e define que a batalha continua rolando

        val atacante = if (jogador1!!.nome == nomeAtacante) jogador1!! else jogador2!!
        val oponente = if (jogador1!!.nome == nomeAtacante) jogador2!! else jogador1!!

        var log = "" // log que vai sendo atualizado conforme as batalhas acontecem
        var acabou = false // Variável que define quando a batalha acaba

        when (acao.uppercase()) { // Verifica quão ação foi realizada
            "ATACAR" -> log = atacar(atacante, oponente)
            "DEFENDER" -> log = defender(atacante)
            "PODER" -> log = usarPoder(atacante, oponente)
            "FUGIR" -> {
                limparArena() // Limpa a arena
                resetStats(atacante) // Reseta a vida do atacante no banco após a luta terminar
                resetStats(oponente) // Reseta a vida do oponente no banco após a luta terminar
                return Pair("🏃 $nomeAtacante fugiu acovardado! A arena está livre novamente.", true) // Retorna um log e define que a luta acabou
            }
            else -> return Pair("Ação desconhecida!", false) // caso a ação não seja encontrada
        }

        // Salva a vida atualizada de ambos no banco de dados a cada rodada
        repository.save(atacante)
        repository.save(oponente)

        // Verifica se alguém morreu
        if (oponente.vida <= 0) {
            log += "\n💀 ${oponente.nome} FOI DERROTADO! 🏆 ${atacante.nome} VENCEU!"
            limparArena()
            acabou = true
            resetStats(atacante)
            resetStats(oponente)
        } else {
            // Passa a vez para o outro
            nomeJogadorDaVez = oponente.nome
            log += "\nÉ a vez de $nomeJogadorDaVez." // Adiciona ao log
        }

        return Pair(log, acabou) // Retorna o log e se a batalha acabou
    }

    private fun atacar(atacante: Personagem, oponente: Personagem): String {
        val dano = atacante.forca
        val velOponente = oponente.velocidade + Random.nextInt(0, 26)
        val velAtacante = atacante.velocidade + Random.nextInt(0, 26)
        return if (velAtacante > velOponente) { // Verifica se a velocidade do atacante é maior do que a do oponente
            oponente.vida -= dano // Se sim o oponente toma dano
            "🗡️ ${atacante.nome} atacou ${oponente.nome} causando $dano de dano! (Vida de ${oponente.nome}: ${oponente.vida})"
        } else { // Se não ele desvia
            "💨 ${oponente.nome} foi muito rápido e desviou do ataque!"
        }
    }

    private fun defender(personagem: Personagem): String {
        personagem.vida += 5 // Cura 5 de vida
        return "🛡️ ${personagem.nome} assumiu postura defensiva e recuperou 5 de vida! (Vida atual: ${personagem.vida})"
    }

    private fun usarPoder(atacante: Personagem, oponente: Personagem): String {
        return when (atacante) {
            is Mago -> {
                atacante.magia = atacante.forca + atacante.vida
                oponente.vida -= atacante.magia
                "🔮 ${atacante.nome} lançou um feitiço poderoso causando ${atacante.magia} de dano(${oponente.nome}: ${oponente.vida} vida)!"
            }
            is Guerreiro -> {
                atacante.vida += atacante.defesa
                "⚔️ ${atacante.nome} ativou sua fúria e aumentou sua vida para ${atacante.vida}!"
            }
            is Ladino -> {
                oponente.vida -= atacante.sagacidade
                "🗡️ ${atacante.nome} usou sagacidade para causar ${atacante.sagacidade} de dano ao oponente(${oponente.nome}: ${oponente.vida} vida)!"
            }
            else -> "O poder falhou!"
        }
    }

    private fun limparArena() { // Limpa a arena para permitir novas batalhas
        jogador1 = null
        jogador2 = null
        nomeJogadorDaVez = ""
    }
}
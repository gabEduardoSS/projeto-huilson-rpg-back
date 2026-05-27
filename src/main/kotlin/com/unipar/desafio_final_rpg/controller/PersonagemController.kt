package com.unipar.desafio_final_rpg.controller

import com.unipar.desafio_final_rpg.model.Guerreiro
import com.unipar.desafio_final_rpg.model.Mago
import com.unipar.desafio_final_rpg.model.Ladino
import com.unipar.desafio_final_rpg.model.Personagem
import com.unipar.desafio_final_rpg.service.PersonagemService
import org.springframework.boot.jackson.autoconfigure.JacksonProperties
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import tools.jackson.databind.JsonNode

// @RestController combina @Controller + @ResponseBody
// Indica que essa classe é um controlador HTTP e que todos os métodos
// retornam dados direto no corpo da resposta (não renderiza páginas HTML)
@RestController
class PersonagemController (
    // Injeção de dependência via construtor (padrão do Spring/Kotlin)
    // O Spring cria e gerencia as instâncias de PersonagemService e RestClient
    val personagemService: PersonagemService,

    // RestClient é o cliente HTTP do Spring (substituto moderno do RestTemplate)
    // Usado para fazer requisições HTTP para outros servidores
    val restClient: RestClient
) {
    /*@GetMapping("/Atacar")
    fun atacar(forca: Int){
        println("Estou atacando meu rival")
        try {
            restClient.post()                      // Define que será uma requisição HTTP POST
                .uri(rivalUrl)                     // Define o destino: URL do rival (application.properties)
                .contentType(MediaType.TEXT_PLAIN) // Informa ao servidor rival que o corpo é texto puro (text/plain)
                .body(forca.toString())                    // Define o corpo da requisição com a mensagem
                .retrieve()                        // Dispara a requisição e prepara para ler a resposta
                .toBodilessEntity()                // Lê apenas os headers/status, ignora o corpo da resposta
        } catch (e: Exception){
            // Captura qualquer erro de rede ou HTTP (ex: rival offline, connection refused)
            println("Deu erro: ${e.message}")
        }
    }*/

    @PostMapping("/apanhar", consumes = [MediaType.APPLICATION_JSON_VALUE]) //Consumes passa o parametro, só aceita o parametro
    fun apanhar(@RequestBody forca: Int){
        println("Seu personagem perdeu: $forca de vida")
    }

    @PostMapping("/character/save")
    fun salvarPersonagem(@RequestBody body: JsonNode): String{
        try{
            var personagem: Personagem = when(body.get("charClass").asString()){
                "guerreiro" -> personagemService.salvarPersonagem(Guerreiro(nome=body.get("name").asString(), forca=body.get("forca").asInt(), velocidade=body.get("velocidade").asInt(), vida=body.get("vida").asInt()))
                "mago" -> personagemService.salvarPersonagem(Mago(nome=body.get("name").asString(), forca=body.get("forca").asInt(), velocidade=body.get("velocidade").asInt(), vida=body.get("vida").asInt()))
                "ladino" -> personagemService.salvarPersonagem(Ladino(nome=body.get("name").asString(), forca=body.get("forca").asInt(), velocidade=body.get("velocidade").asInt(), vida=body.get("vida").asInt()))
                else -> throw IllegalArgumentException("Classe inválida!")
            }
        } catch(e: Exception){
            return "Erro"
        }
        return "Personagem atualizado"
    }

    @GetMapping("character/getall")
    fun listPersonagem(): List<Personagem>{
        return personagemService.buscarTodos()
    }
}
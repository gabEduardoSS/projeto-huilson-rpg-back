package com.unipar.desafio_final_rpg.controller

import com.unipar.desafio_final_rpg.model.Guerreiro
import com.unipar.desafio_final_rpg.model.Mago
import com.unipar.desafio_final_rpg.model.Ladino
import com.unipar.desafio_final_rpg.model.Personagem
import com.unipar.desafio_final_rpg.service.PersonagemService
import org.springframework.boot.jackson.autoconfigure.JacksonProperties
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import tools.jackson.databind.JsonNode

// @RestController combina @Controller + @ResponseBody
// Indica que essa classe é um controlador HTTP e que todos os métodos
// retornam dados direto no corpo da resposta (não renderiza páginas HTML)
@RestController
@RequestMapping("/character")
class PersonagemController (
    // Injeção de dependência via construtor (padrão do Spring/Kotlin)
    // O Spring cria e gerencia as instâncias de PersonagemService e RestClient
    val personagemService: PersonagemService,

    // RestClient é o cliente HTTP do Spring (substituto moderno do RestTemplate)
    // Usado para fazer requisições HTTP para outros servidores
    val restClient: RestClient
) {
    @PostMapping("/save")
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

    @DeleteMapping("/delete/{id}")
    fun deletarPersonagem(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val personagem = personagemService.buscarId(id)

            if (personagem != null) {
                personagemService.excluirPersonagem(id)
                ResponseEntity.ok(mapOf(
                    "status" to "sucesso",
                    "mensagem" to "Personagem ${personagem.nome}, com o ID ${personagem.id}, deletado!"
                ))
            } else {
                ResponseEntity.status(404).body(mapOf(
                    "status" to "erro",
                    "mensagem" to "Personagem não encontrado"
                ))
            }
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf(
                "status" to "erro",
                "mensagem" to (e.message ?: "Erro desconhecido")
            ))
        }
    }

    @GetMapping("/getall")
    fun listPersonagem(): List<Personagem>{
        return personagemService.buscarTodos()
    }
}
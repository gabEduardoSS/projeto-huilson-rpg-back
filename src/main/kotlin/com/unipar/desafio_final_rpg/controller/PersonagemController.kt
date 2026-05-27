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
    // O Spring cria e gerencia as instâncias de PersonagemService e RestClient
    val personagemService: PersonagemService,

    // RestClient é o cliente HTTP do Spring (substituto moderno do RestTemplate)
    // Usado para fazer requisições HTTP para outros servidores
    val restClient: RestClient
) {

    // @PostMapping: este endpoint responde a requisições HTTP POST em "/save"
    // É aqui que esta máquina RECEBE mensagens enviadas pelo sistema
    // A função recebe uma requisição para salvar um personagem no banco
    @PostMapping("/save")
    fun salvarPersonagem(@RequestBody body: JsonNode): String{ // body contendo as informações do personagem vindo do front-end
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

    // @DeleteMapping: este endpoint responde a requisições HTTP DELETE em "/delete/{id}"
    // É aqui que esta máquina RECEBE mensagens enviadas pelo sistema
    // A função recebe uma requisição para deletar um personagem no banco
    @DeleteMapping("/delete/{id}")
    fun deletarPersonagem(@PathVariable id: Long): ResponseEntity<Any> { // o ID do personagem vem pela URL no método DELETE
        return try {
            val personagem = personagemService.buscarId(id) // Buscar o personagem no banco pelo ID

            if (personagem != null) {
                personagemService.excluirPersonagem(id) // Tenta excluir o personagem
                ResponseEntity.ok(mapOf( // Responde que o personagem foi excluido com sucesso
                    "status" to "sucesso",
                    "mensagem" to "Personagem ${personagem.nome}, com o ID ${personagem.id}, deletado!"
                ))
            } else {
                ResponseEntity.status(404).body(mapOf( // Responde que o personagem não foi encontrado
                    "status" to "erro",
                    "mensagem" to "Personagem não encontrado"
                ))
            }
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf( // Responde que o sistema encontrou um erro desconhecido
                "status" to "erro",
                "mensagem" to (e.message ?: "Erro desconhecido")
            ))
        }
    }

    // @GetMapping: este endpoint responde a requisições HTTP Get em "/getall"
    // É aqui que esta máquina RETORNA mensagens enviadas pelo sistema
    // A função recebe uma requisição para procurar todos os personagens no banco e retorna uma lista com eles
    @GetMapping("/getall")
    fun listPersonagem(): List<Personagem>{
        return personagemService.buscarTodos()
    }
}
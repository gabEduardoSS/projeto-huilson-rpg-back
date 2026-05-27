package com.unipar.desafio_final_rpg.service

import com.unipar.desafio_final_rpg.model.Personagem
import com.unipar.desafio_final_rpg.repository.PersonagemRepository
import org.springframework.stereotype.Service

@Service
class PersonagemService (
    val personagemRepository: PersonagemRepository
){

    // Salva o personagem no banco
    fun salvarPersonagem(personagem: Personagem) : Personagem {
            //Se não tenho um Primary Key vou criar uma nova entidade
            //Se tenho um Primary Key somente vou editar
            return personagemRepository.save(personagem)
    }

    // Busca todos os personagens no banco
    fun buscarTodos(): List<Personagem>{
        return personagemRepository.findAll()
    }

    // Busca um personagem específico pelo ID no banco
    fun buscarId(id: Long): Personagem {
        return personagemRepository.findById(id).orElseThrow()
    }


    // Exclui um personagem específico no banco pelo ID
    fun excluirPersonagem(id: Long) {
        personagemRepository.deleteById(id)
    }

}
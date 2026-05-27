package com.unipar.desafio_final_rpg.service

import com.unipar.desafio_final_rpg.model.Personagem
import com.unipar.desafio_final_rpg.repository.PersonagemRepository
import org.springframework.stereotype.Service

@Service
class PersonagemService (
    val personagemRepository: PersonagemRepository
){

    fun salvarPersonagem(personagem: Personagem) : Personagem {
            //Se não tenho um Primary Key vou criar uma nova entidade
            //se já tiver um nome igual no banco, somente vai editar
            //Se tenho um Primary Key somente vou editar
            return personagemRepository.save(personagem)
    }

    //Buscar
    fun buscarTodos(): List<Personagem>{
        return personagemRepository.findAll()
    }


    //Excluir
    fun excluirPersonagem(id: Long) {
        personagemRepository.deleteById(id)
    }

}
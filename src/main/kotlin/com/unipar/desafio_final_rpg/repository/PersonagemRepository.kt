package com.unipar.desafio_final_rpg.repository

import com.unipar.desafio_final_rpg.model.Personagem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonagemRepository : JpaRepository<Personagem, Long>
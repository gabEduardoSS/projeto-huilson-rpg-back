package com.unipar.desafio_final_rpg.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Guerreiro(

    nome: String,
    forca: Int,
    velocidade: Int,
    vida: Int = 100,

     // Defesa reduz dano recebido.
    var defesa: Int = 30

) : Personagem(
    nome = nome,
    forca = forca,
    velocidade = velocidade,
    vida = vida
) {}
package com.unipar.desafio_final_rpg.model

import jakarta.persistence.Entity

@Entity
class Ladino(

    nome: String,
    forca: Int,
    velocidade: Int,
    vida: Int = 100,

    // Define dano crítico
    var sagacidade: Int = 35

) : Personagem(
    nome = nome,
    forca = forca,
    velocidade = velocidade,
    vida = vida
) {}
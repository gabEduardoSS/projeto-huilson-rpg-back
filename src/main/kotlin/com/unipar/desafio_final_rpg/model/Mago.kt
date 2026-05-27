package com.unipar.desafio_final_rpg.model

import jakarta.persistence.Entity

@Entity
class Mago(

    nome: String,
    forca: Int,
    velocidade: Int,
    vida: Int = 100,
    var magia: Int = vida + forca

) : Personagem(
    nome = nome,
    forca = forca,
    velocidade = velocidade,
    vida = vida
) {}
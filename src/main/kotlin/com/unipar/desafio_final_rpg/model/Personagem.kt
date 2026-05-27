package com.unipar.desafio_final_rpg.model

import jakarta.persistence.*
import kotlin.random.Random

// Base dos personagens do RPG
// @Inheritance permite que Mago, Guerreiro e Ladino sejam armazenados no banco como extensões da classe

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
open class Personagem(

// ID gerado automaticamente pelo banco.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long = 0,

    @Column(unique = true)
    open var nome: String = "",
    open var forca: Int = 0,
    open var velocidade: Int = 0,
    open var vida: Int = 100
) {}
package com.unipar.desafio_final_rpg.model

data class Turno(

    val atacanteId: Long,
    val oponenteId: Long,
    val acao: String,

    var jogadorDaVezId: Long
)
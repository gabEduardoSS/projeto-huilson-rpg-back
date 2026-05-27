package com.unipar.desafio_final_rpg.service

import com.unipar.desafio_final_rpg.model.Personagem
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")

class ApiService {
    var fightLog: MutableList<String> = mutableListOf()
    @PostMapping("/action")
    fun teste(@RequestBody action: Map<String, String>) {
        fightLog.add("O usuário: teste realizou a ação: ${action["action"]}")
        println(fightLog)
    }

}
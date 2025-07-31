package com.nikitahohulia.cqrses

import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import com.typesafe.config.ConfigFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class CqrsesApplication {

    @Bean
    fun actorSystem(): ActorSystem<Nothing> {
        val cfg = ConfigFactory.load()
        return ActorSystem.create(Behaviors.empty(), "hub-system", cfg)
    }
}

fun main(args: Array<String>) {
    runApplication<CqrsesApplication>(*args)
}

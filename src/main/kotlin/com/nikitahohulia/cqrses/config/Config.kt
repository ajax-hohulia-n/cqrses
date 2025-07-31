package com.nikitahohulia.cqrses.config

import akka.actor.typed.ActorRef
import com.nikitahohulia.cqrses.model.Command
import com.nikitahohulia.cqrses.model.Message
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ConcurrentHashMap

@Configuration
class Config {

    @Bean
    fun hubsRefMap() = ConcurrentHashMap<String, ActorRef<Command>>()
}
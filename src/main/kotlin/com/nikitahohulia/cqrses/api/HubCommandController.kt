package com.nikitahohulia.cqrses.api

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Props
import akka.actor.typed.javadsl.AskPattern
import com.nikitahohulia.cqrses.domain.HubSettings
import com.nikitahohulia.cqrses.entity.HubEntity
import com.nikitahohulia.cqrses.model.Ack
import com.nikitahohulia.cqrses.model.Command
import com.nikitahohulia.cqrses.model.command.UpdateSettings
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@RestController
@RequestMapping("/hub")
class HubCommandController(
    private val system: ActorSystem<Nothing>,
    private val hubs: ConcurrentHashMap<String, ActorRef<Command>>,
) {

    private fun entityRef(id: String): ActorRef<Command> =
        hubs.computeIfAbsent(id) {
            system.systemActorOf(HubEntity.create(id), "hub-$id", Props.empty())
        }

    @PostMapping("{id}/settings")
    suspend fun update(
        @PathVariable id: String,
        @RequestBody settings: HubSettings,
    ): Ack =
        AskPattern.ask(
            entityRef(id),
            { reply: ActorRef<Ack> -> UpdateSettings(settings, reply) },
            Duration.ofSeconds(3),
            system.scheduler()
        ).toCompletableFuture().get()
}

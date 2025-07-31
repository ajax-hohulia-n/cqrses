package com.nikitahohulia.cqrses.entity

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.javadsl.CommandHandler
import akka.persistence.typed.javadsl.CommandHandlerBuilderByState
import akka.persistence.typed.javadsl.EventHandler
import akka.persistence.typed.javadsl.EventSourcedBehavior
import akka.persistence.typed.javadsl.RetentionCriteria
import com.nikitahohulia.cqrses.domain.HubState
import com.nikitahohulia.cqrses.model.Ack
import com.nikitahohulia.cqrses.model.Command
import com.nikitahohulia.cqrses.model.Event
import com.nikitahohulia.cqrses.model.command.UpdateSettings
import com.nikitahohulia.cqrses.model.event.SettingsUpdated

object HubEntity {

    private val RETENTION = RetentionCriteria.snapshotEvery(5, 3)
    private const val SETTINGS_TAG = "settings-updated"

    fun create(hubId: String): Behavior<Command> {
        println("Initialized actor creation for $hubId")

        return object : EventSourcedBehavior<Command, Event, HubState>(
            PersistenceId.of("HubEntity", hubId)
        ) {

            /* ────────────── INITIAL STATE ────────────── */
            override fun emptyState(): HubState = HubState(hubId)

            /* ───────────── COMMAND HANDLER ───────────── */
            override fun commandHandler(): CommandHandler<Command, Event, HubState> =
                newCommandHandlerBuilder()
                    .forAnyState()
                    .registerCommands()
                    .build()

            /* ───────────── EVENT  HANDLER ────────────── */
            override fun eventHandler(): EventHandler<HubState, Event> =
                newEventHandlerBuilder()
                    .forAnyState()
                    .onEvent(SettingsUpdated::class.java) { state, evt -> state.apply(evt) }
                    .build()

            override fun retentionCriteria(): RetentionCriteria = RETENTION

            override fun tagsFor(event: Event): Set<String> =
                when (event) {
                    is SettingsUpdated -> setOf(SETTINGS_TAG)
                    else -> emptySet()
                }

            /* ───── WRITE ───── */
            private fun CommandHandlerBuilderByState<Command, Event, HubState, HubState>.registerCommands() =
                this.apply {
                    println("Going to handle UpdateSettings")
                    onCommand(UpdateSettings::class.java) { _, cmd ->
                        Effect().persist(SettingsUpdated(cmd.newSettings))
                            .thenReply(cmd.replyTo) { Ack(success = true) }
                    }
                }
        }
    }
}

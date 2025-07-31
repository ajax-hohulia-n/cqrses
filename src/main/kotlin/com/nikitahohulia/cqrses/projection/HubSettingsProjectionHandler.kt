package com.nikitahohulia.cqrses.projection

import akka.Done
import akka.projection.eventsourced.EventEnvelope
import akka.projection.javadsl.Handler
import akka.stream.alpakka.cassandra.javadsl.CassandraSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.nikitahohulia.cqrses.model.Event
import com.nikitahohulia.cqrses.model.event.SettingsUpdated
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class HubSettingsProjectionHandler(
    private val cassandra: CassandraSession,
) : Handler<EventEnvelope<Event>>() {

    override fun process(env: EventEnvelope<Event>): CompletionStage<Done> {
        val event = env.event() as? SettingsUpdated ?: return CompletableFuture.completedFuture(null)

        val hubId = env.persistenceId().removePrefix("HubEntity|")
        val settings = event.newSettings

        println("Projection handler is handling SettingUpdated for $hubId in hub settings projection")
        val stmt = SimpleStatement.newInstance(
            """
            INSERT INTO cqrs_projection.hub_settings
              (hub_id, name, owner_id, active, settings)
            VALUES (?, ?, ?, ?, ?)
            """.trimIndent(),
            env.persistenceId().removePrefix("HubEntity|"),
            settings.name,
            settings.ownerId,
            settings.active,
            settings.settings,
        )
        return cassandra.executeWrite(stmt)
    }
}
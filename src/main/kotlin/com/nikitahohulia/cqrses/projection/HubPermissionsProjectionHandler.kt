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

class HubPermissionsProjectionHandler(
    private val cassandra: CassandraSession,
) : Handler<EventEnvelope<Event>>() {

    override fun process(envelope: EventEnvelope<Event>): CompletionStage<Done> {
        val event = envelope.event() as? SettingsUpdated ?: return CompletableFuture.completedFuture(null)

        val hubId = envelope.persistenceId().removePrefix("HubEntity|")
        val settings = event.newSettings

        println("Projection handler is handling SettingUpdated for $hubId in hub permissions projection")
        val stmt = SimpleStatement.newInstance(
            """
            INSERT INTO cqrs_projection.hub_permissions (hub_id, name, owner_id, permission, active)
            VALUES (?, ?, ?, ?, ?)
            """.trimIndent(),
            hubId,
            settings.name,
            settings.ownerId,
            settings.settings?.getOrElse("permission", { null }) ?: "DEFAULT",
            settings.active
        )

        return cassandra.executeWrite(stmt)
    }
}

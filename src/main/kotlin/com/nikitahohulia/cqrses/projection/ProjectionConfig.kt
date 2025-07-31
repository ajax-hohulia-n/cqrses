package com.nikitahohulia.cqrses.projection


import akka.actor.typed.ActorSystem
import akka.actor.typed.Props
import akka.persistence.cassandra.query.javadsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.projection.Projection
import akka.projection.ProjectionBehavior
import akka.projection.ProjectionId
import akka.projection.cassandra.javadsl.CassandraProjection
import akka.projection.eventsourced.EventEnvelope
import akka.projection.eventsourced.javadsl.EventSourcedProvider
import akka.projection.javadsl.Handler
import akka.stream.alpakka.cassandra.javadsl.CassandraSessionRegistry
import com.nikitahohulia.cqrses.model.Event
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class ProjectionConfig(private val system: ActorSystem<Nothing>) {

    @PostConstruct
    fun start() {
        CassandraProjection.createTablesIfNotExists(system).toCompletableFuture().join()

        val permissionsProjection = buildProjection(
            "permissions-projection",
            { HubPermissionsProjectionHandler(cassandraSession(it)) }
        )
        system.systemActorOf(
            ProjectionBehavior.create(permissionsProjection),
            "permissions-projection",
            Props.empty()
        )

        val settingsProjection = buildProjection(
            "settings-projection",
            { HubSettingsProjectionHandler(cassandraSession(it)) }
        )
        system.systemActorOf(
            ProjectionBehavior.create(settingsProjection),
            "settings-projection",
            Props.empty()
        )
    }

    private fun buildProjection(
        projectionName: String,
        handlerFactory: (ActorSystem<Nothing>) -> Handler<EventEnvelope<Event>>
    ): Projection<EventEnvelope<Event>> {

        val readJournal = PersistenceQuery.get(system.classicSystem())
            .getReadJournalFor(CassandraReadJournal::class.java, CassandraReadJournal.Identifier())

        val sourceProvider = EventSourcedProvider.eventsByTag<Event>(
            system,
            readJournal,
            "settings-updated",
        )

        return CassandraProjection.atLeastOnce(
            ProjectionId.of(projectionName, "settings-updated"),
            sourceProvider,
            { handlerFactory(system) }
        )
    }

    private fun cassandraSession(system: ActorSystem<Nothing>) =
        CassandraSessionRegistry.get(system)
            .sessionFor("akka.projection.cassandra.session-config")
}

package com.nikitahohulia.cqrses.projection

import akka.actor.typed.ActorSystem
import com.datastax.oss.driver.api.core.CqlSession
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class HubPermissionsSchemaCreator(
    private val system: ActorSystem<*>,
    private val cqlSession: CqlSession,
) {

    @PostConstruct
    fun ddl() {
        cqlSession.execute(
            """
            CREATE KEYSPACE IF NOT EXISTS cqrs_projection
            WITH replication = {'class':'SimpleStrategy','replication_factor':1}
            """.trimIndent()
        )

        cqlSession.execute(
            """
            CREATE TABLE IF NOT EXISTS cqrs_projection.hub_permissions (
              hub_id     text PRIMARY KEY,
              name       text,
              owner_id   text,
              permission text,
              active     boolean
            )
            """.trimIndent()
        )
    }
}

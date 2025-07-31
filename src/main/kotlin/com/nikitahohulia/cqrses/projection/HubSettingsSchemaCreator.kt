package com.nikitahohulia.cqrses.projection

import com.datastax.oss.driver.api.core.CqlSession
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class HubSettingsSchemaCreator(
    private val cql: CqlSession,
) {
    @PostConstruct
    fun ddl() {
        cql.execute(
            """
            CREATE TABLE IF NOT EXISTS cqrs_projection.hub_settings (
              hub_id   text PRIMARY KEY,
              name     text,
              owner_id text,
              active   boolean,
              settings map<text,text>
            )
            """.trimIndent()
        )
    }
}

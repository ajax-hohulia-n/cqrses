package com.nikitahohulia.cqrses.api

import com.datastax.oss.driver.api.core.CqlSession
import com.nikitahohulia.cqrses.domain.HubSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hub")
class HubQueryController(
    private val cqlSession: CqlSession,
) {

    @GetMapping("{id}/settings")
    suspend fun get(@PathVariable id: String): HubSettings =
        withContext(Dispatchers.IO) {
            val row = cqlSession.execute(selectSettings.bind(id)).one()
            row?.let {
                HubSettings(
                    name = it.getString("name") ?: "",
                    ownerId = it.getString("owner_id") ?: "",
                    active = it.getBoolean("active"),
                    settings = it.getMap("settings", String::class.java, String::class.java)
                )
            } ?: HubSettings("Unnamed", "unknown")
        }

    @GetMapping("{id}/permissions")
    suspend fun permissions(@PathVariable id: String): PermissionDto =
        withContext(Dispatchers.IO) {
            val rows = cqlSession.execute(selectPerm.bind(id)).all()
            rows.map { row ->
                PermissionDto(
                    name = row.getString("name") ?: "",
                    ownerId = row.getString("owner_id") ?: "",
                    permission = row.getString("permission") ?: "",
                    active = row.getBoolean("active"),
                )
            }.toList().firstOrNull() ?: PermissionDto()
        }

    private val selectSettings by lazy {
        cqlSession.prepare(
            """
            SELECT name, owner_id, active, settings
            FROM cqrs_projection.hub_settings
            WHERE hub_id = ?
            """.trimIndent()
        )
    }

    private val selectPerm by lazy {
        cqlSession.prepare(
            """
            SELECT name, owner_id, permission, active
            FROM cqrs_projection.hub_permissions
            WHERE hub_id = ?
            """.trimIndent()
        )
    }

    companion object {

        data class PermissionDto(
            val name: String = "",
            val ownerId: String = "",
            val permission: String = "",
            val active: Boolean = false,
        )
    }
}

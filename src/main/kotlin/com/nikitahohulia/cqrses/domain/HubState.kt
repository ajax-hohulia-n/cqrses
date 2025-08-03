package com.nikitahohulia.cqrses.domain

import com.nikitahohulia.cqrses.model.Event
import com.nikitahohulia.cqrses.model.event.SettingsUpdated
import java.io.Serializable

data class HubState(
    val hubId: String,
    val settings: HubSettings = HubSettings("Unnamed", "unknown"),
    private var appliedEventsCount: Int = 0,
) : Serializable {

    fun apply(event: Event): HubState = when (event) {
        is SettingsUpdated -> copy(settings = event.newSettings, appliedEventsCount = this.appliedEventsCount + 1)

        else -> throw IllegalStateException("Unknown event $event")
    }
}

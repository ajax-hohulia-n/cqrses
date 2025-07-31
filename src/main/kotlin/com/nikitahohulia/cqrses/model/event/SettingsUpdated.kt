package com.nikitahohulia.cqrses.model.event

import com.nikitahohulia.cqrses.domain.HubSettings
import com.nikitahohulia.cqrses.model.Event

data class SettingsUpdated(val newSettings: HubSettings) : Event

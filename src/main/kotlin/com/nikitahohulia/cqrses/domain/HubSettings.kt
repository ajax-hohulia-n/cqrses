package com.nikitahohulia.cqrses.domain

import java.io.Serializable

data class HubSettings(
    val name: String,
    val ownerId: String,
    val settings: Map<String, String>? = emptyMap(),
    val active: Boolean = false,
) : Serializable

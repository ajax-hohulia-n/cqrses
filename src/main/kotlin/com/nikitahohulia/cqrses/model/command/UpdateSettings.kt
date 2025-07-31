package com.nikitahohulia.cqrses.model.command

import akka.actor.typed.ActorRef
import com.nikitahohulia.cqrses.domain.HubSettings
import com.nikitahohulia.cqrses.model.Ack
import com.nikitahohulia.cqrses.model.Command

data class UpdateSettings(
    val newSettings: HubSettings,
    val replyTo: ActorRef<Ack>,
) : Command

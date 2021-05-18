package org.fintech.tinkoff.model

import java.time.Instant

data class Schedule(
    val id: Int,
    val userId: Int,
    val time: Instant,
    val message: String,
    val dispatches: Boolean
)

data class ScheduleTemplate(
    val userId: Int,
    val time: Instant,
    val message: String
)
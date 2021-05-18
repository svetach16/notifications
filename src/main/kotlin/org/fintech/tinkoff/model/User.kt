package org.fintech.tinkoff.model

data class User(
    val name: String,
    val surname: String,
    val telegramId: String?,
    val email: String?,
    val slackId: String?
)
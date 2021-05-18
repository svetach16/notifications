package org.fintech.tinkoff.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

@Service
class TelegramSender(
    @Value("\${telegram.bot.token}") private val token: String
){
    fun send(chatId: Long, text: String) {
        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .version(HttpClient.Version.HTTP_2)
            .build()
        val builder = UriComponentsBuilder
            .fromUriString("https://api.telegram.org")
            .path("/{token}/sendMessage")
            .queryParam("chat_id", chatId)
            .queryParam("text", text)
        val request: HttpRequest = HttpRequest.newBuilder()
            .GET()
            .uri(builder.build(token))
            .timeout(Duration.ofSeconds(3))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        require(response.statusCode() == 200) { "Telegram message sending failed" }
    }
}
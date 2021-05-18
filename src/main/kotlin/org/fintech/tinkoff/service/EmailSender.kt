package org.fintech.tinkoff.service

import org.fintech.tinkoff.controller.UserControllerImpl
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailSender(private val mailService: JavaMailSender) {
    fun send(to: String, body: String) {
        val message = SimpleMailMessage().apply {
            from = "sveta.chuinyshena@yandex.ru"
            setTo(to)
            subject = "notification from the application"
            text = body
        }

        log.info("Sending notification from: ${message.from}, to: ${message.to}")

        mailService.send(message)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserControllerImpl::class.java)
    }
}
package org.fintech.tinkoff

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@SpringBootApplication
class Application {
    @Bean
    fun getJavaMailSender(
        @Value("\${mail.user}") mailUser: String,
        @Value("\${mail.password}") mailPassword: String
    ): JavaMailSender {
        return JavaMailSenderImpl().apply {
            host = "smtp.yandex.ru"
            port = 465
            username = mailUser
            password = mailPassword
            protocol = "smtps"
            javaMailProperties.apply {
                this["mail.smtp.auth"] = "true"
                this["mail.smtp.starttls.required"] = "true"
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
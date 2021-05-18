package org.fintech.tinkoff.service

import org.fintech.tinkoff.controller.UserControllerImpl
import org.fintech.tinkoff.model.Schedule
import org.fintech.tinkoff.repository.DbScheduleDao
import org.fintech.tinkoff.repository.DbUserDao
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service
class ScheduleService(
    private val scheduleDao: DbScheduleDao,
    private val userDao: DbUserDao,
    private val emailSender: EmailSender,
    private val telegramSender: TelegramSender
) {
    private val scheduledExecutor = Executors.newScheduledThreadPool(5)

    fun submit(schedule: Schedule) {
        val now = Instant.now()
        if (schedule.dispatches && schedule.time.isBefore(now)) return
        val user = userDao.get(schedule.userId) ?: error("Can't get user with id: ${schedule.userId}")

        if (user.email != null) {
            scheduledExecutor.schedule(
                {
                    try {
                        emailSender.send(user.email, schedule.message)
                        scheduleDao.setDone(schedule.id)
                    } catch (e: Exception) {
                        log.info("Error sending message in email")
                    }
                },
                Duration.between(now, schedule.time).seconds,
                TimeUnit.SECONDS
            )
        }

        if (user.telegramId != null) {
            scheduledExecutor.schedule(
                {
                    try {
                        telegramSender.send(user.telegramId.toLong(), schedule.message)
                        scheduleDao.setDone(schedule.id)
                    } catch (e: Exception) {
                        log.info("Error sending message in telegram")
                    }
                },
                Duration.between(now, schedule.time).seconds,
                TimeUnit.SECONDS
            )
        }
    }

    @PreDestroy
    fun stop() {
        scheduledExecutor.shutdown()
    }

    @PostConstruct
    fun init() {
        scheduleDao.getAllSchedule().forEach(this::submit)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserControllerImpl::class.java)
    }
}
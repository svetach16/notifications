package org.fintech.tinkoff.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import org.fintech.tinkoff.model.Schedule
import org.fintech.tinkoff.model.ScheduleTemplate
import org.fintech.tinkoff.repository.DbScheduleDao
import org.fintech.tinkoff.service.ScheduleService

@RequestMapping("api/schedule")
interface ScheduleController {
    @GetMapping
    @Operation(summary = "Get all schedule")
    fun getSchedule(): List<Schedule>

    @PostMapping
    @Operation(summary = "Add event")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "correct data format"),
            ApiResponse(responseCode = "404", description = "invalid data format")
        ]
    )
    fun addEvent(@RequestBody scheduleTemplate: ScheduleTemplate)
}

@RestController
class ScheduleControllerImpl(
    private val repository: DbScheduleDao,
    private val scheduleService: ScheduleService
) : ScheduleController {
    override fun getSchedule(): List<Schedule> {
        return repository.getAllSchedule()
    }

    override fun addEvent(@RequestBody scheduleTemplate: ScheduleTemplate) {
        log.info("Creating event: $scheduleTemplate")

        val id = repository.create(scheduleTemplate)
        scheduleService.submit(
            Schedule(id, scheduleTemplate.userId, scheduleTemplate.time, scheduleTemplate.message, false)
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserControllerImpl::class.java)
    }
}
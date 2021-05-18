package org.fintech.tinkoff.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.fintech.tinkoff.model.Schedule
import org.fintech.tinkoff.model.ScheduleTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import java.sql.ResultSet
import java.sql.Timestamp

interface ScheduleDao {
    fun create(scheduleTemplate: ScheduleTemplate): Int
    fun getAllSchedule(): List<Schedule>
    fun setDone(id: Int)
}

@Repository
class DbScheduleDao(
    val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
    val template: JdbcTemplate
) : ScheduleDao {
    override fun create(scheduleTemplate: ScheduleTemplate): Int {
        val generatedKeyHolder = GeneratedKeyHolder()
        val params = MapSqlParameterSource().apply {
            addValue("user_id", scheduleTemplate.userId)
            addValue("time", Timestamp.from(scheduleTemplate.time))
            addValue("message", scheduleTemplate.message)
        }
        namedParameterJdbcTemplate.update(INSERT_SCHEDULE_SQL, params, generatedKeyHolder, arrayOf("id"))

        return generatedKeyHolder.getKeyAs(Int::class.javaObjectType)
    }

    override fun getAllSchedule(): List<Schedule> {
        return template.query(GET_SCHEDULE_SQl, ScheduleMapper)
    }

    override fun setDone(id: Int) {
        template.update(UPDATE_SCHEDULE_SQL, id)
    }

    companion object {
        const val INSERT_SCHEDULE_SQL =
            "INSERT INTO Schedule (user_id, time, message, dispatches) VALUES (:user_id,:time,:message,false)"
        const val UPDATE_SCHEDULE_SQL = "UPDATE Schedule SET dispatches = true WHERE id = ?"
        const val GET_SCHEDULE_SQl = "SELECT * FROM Schedule"
    }
}

object ScheduleMapper : RowMapper<Schedule> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = Schedule(
        id = rs.getInt("id"),
        userId = rs.getInt("user_id"),
        time = rs.getTimestamp("time").toInstant(),
        message = rs.getString("message"),
        dispatches = rs.getBoolean("dispatches")
    )
}
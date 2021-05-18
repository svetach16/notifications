package org.fintech.tinkoff.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.fintech.tinkoff.model.User
import java.sql.ResultSet
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional
import org.springframework.jdbc.support.GeneratedKeyHolder

interface UserDao {
    fun create(user: User)
    fun get(id: Int): User?
    fun get(): List<User>
    fun delete(id: Int)
    fun updateUser(id: Int, user: User)
}

@Repository
class DbUserDao(
    val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
    val template: JdbcTemplate
) : UserDao {
    @Transactional
    override fun create(user: User) {
        val generatedKeyHolder = GeneratedKeyHolder()
        val params = MapSqlParameterSource().apply {
            addValue("name", user.name)
            addValue("surname", user.surname)
        }
        namedParameterJdbcTemplate.update(INSERT_USER_SQL, params, generatedKeyHolder, arrayOf("id"))

        user.telegramId?.let { template.update(INSERT_TELEGRAM_SQL, generatedKeyHolder.key, it) }
        user.email?.let { template.update(INSERT_EMAIL_SQL, generatedKeyHolder.key, it) }
        user.slackId?.let { template.update(INSERT_SLACK_SQL, generatedKeyHolder.key, it) }
    }

    override fun get(id: Int): User? {
        return template.queryForObject(GET_USER_BY_ID, UserMapper, id)
    }

    override fun get(): List<User> {
        return template.query(GET_USERS_SQL, UserMapper)
    }

    @Transactional
    override fun updateUser(id: Int, user: User) {
        template.update(UPDATE_USER_SQL, user.name, user.surname, id)
        user.telegramId?.let { template.update(UPDATE_TELEGRAM_SQL, it, id) }
        user.email?.let { template.update(UPDATE_EMAIL_SQL, it, id) }
    }

    @Transactional
    override fun delete(id: Int) {
        require(template.update(REMOVE_USER_SQL, id) == 1) { "User with id: $id doesn't exist" }
    }

    companion object {
        const val INSERT_USER_SQL = "INSERT INTO \"User\" (name, surname) VALUES (:name,:surname)"
        const val INSERT_TELEGRAM_SQL = "INSERT INTO telegram (user_id, telegram_id) VALUES (?,?)"
        const val INSERT_EMAIL_SQL = "INSERT INTO email (user_id, email) VALUES (?,?)"
        const val INSERT_SLACK_SQL = "INSERT INTO slack (user_id, slack_id) VALUES (?,?)"

        const val REMOVE_USER_SQL = "DELETE FROM \"User\" WHERE id = ?"

        const val UPDATE_USER_SQL = "UPDATE \"User\" SET name = ?, surname = ? WHERE id = ?"
        const val UPDATE_TELEGRAM_SQL = "UPDATE telegram SET telegram_id = ? WHERE user_id = ?"
        const val UPDATE_EMAIL_SQL = "UPDATE email SET email = ? WHERE user_id = ?"

        const val GET_USERS_SQL =
            """
            SELECT u.id, u.name, u.surname, t.telegram_id, e.email , s.slack_id                   
            FROM "User" as u
                LEFT JOIN telegram as t ON u.id = t.user_id
                LEFT JOIN email as e ON u.id = e.user_id
                LEFT JOIN slack as s ON u.id = s.user_id
            """

        const val GET_USER_BY_ID =
            """
                SELECT u.id, u.name, u.surname, t.telegram_id, e.email, s.slack_id                   
                FROM "User" as u 
                    LEFT JOIN telegram as t ON u.id = t.user_id
                    LEFT JOIN email as e ON u.id = e.user_id
                    LEFT JOIN slack as s ON u.id = s.user_id
                WHERE u.id = ?
            """
    }
}

object UserMapper : RowMapper<User> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = User(
        name = rs.getString("name"),
        surname = rs.getString("surname"),
        telegramId = rs.getString("telegram_id"),
        email = rs.getString("email"),
        slackId = rs.getString("slack_id")
    )
}
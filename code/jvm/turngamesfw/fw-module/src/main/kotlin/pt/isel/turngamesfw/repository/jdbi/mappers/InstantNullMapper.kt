package pt.isel.turngamesfw.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.time.Instant
import java.time.temporal.ChronoUnit

class InstantNullMapper : ColumnMapper<Instant?> {
    override fun map(rs: ResultSet, columnNumber: Int, ctx: StatementContext): Instant? {
        if (rs.getTimestamp(columnNumber) == null)
            return null
        return rs.getTimestamp(columnNumber).toInstant()?.truncatedTo(ChronoUnit.SECONDS) ?: Instant.EPOCH
    }
}
package pt.isel.turngamesfw.repository.jdbi.mappers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class InfoMapper : ColumnMapper<JsonNode> {
    override fun map(rs: ResultSet, columnNumber: Int, ctx: StatementContext): JsonNode {
        val json = rs.getString(columnNumber)
        return ObjectMapper().readTree(json)
    }
}
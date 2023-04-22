package pt.isel.turngamesfw

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import pt.isel.turngamesfw.domain.GameProvider
import pt.isel.turngamesfw.repository.jdbi.JdbiTransactionManager
import pt.isel.turngamesfw.repository.jdbi.configure
import pt.isel.turngamesfw.services.GameServices

@SpringBootApplication
class TurnGamesFwApplication {

	@Bean
	fun jdbi() = Jdbi.create(
		PGSimpleDataSource().apply {
			setURL("jdbc:postgresql://localhost:5432/dbBattleship?user=dbuser&password=123entrei.")
		}
	).configure()

	@Bean
	fun gameProvider() = GameProvider()

	@Bean
	fun transactionManager() = JdbiTransactionManager(jdbi())

}

@Component
class StartSpring(private val gameServices: GameServices): CommandLineRunner {

	override fun run(vararg args: String?) {
		gameServices.checkAndSaveAllGames()
	}

}

fun main(args: Array<String>) {
	runApplication<TurnGamesFwApplication>(*args)
}

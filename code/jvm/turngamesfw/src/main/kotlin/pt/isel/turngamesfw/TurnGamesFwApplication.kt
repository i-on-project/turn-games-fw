package pt.isel.turngamesfw

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import pt.isel.ExampleClass
import pt.isel.turngamesfw.domain.GameProvider
import pt.isel.turngamesfw.repository.jdbi.JdbiTransactionManager
import pt.isel.turngamesfw.repository.jdbi.configure
import pt.isel.turngamesfw.services.GameServices

val gameProvider = GameProvider()

@SpringBootApplication
class TurnGamesFwApplication {

	@Bean
	fun jdbi() = Jdbi.create(
		PGSimpleDataSource().apply {
			setURL("jdbc:postgresql://localhost:5432/dbTurnGamesFW?user=dbuser&password=12345")
		}
	).configure()

	@Bean
	fun gameProvider() = gameProvider

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
	ExampleClass.sayHello()
	runApplication<TurnGamesFwApplication>(*args)
}

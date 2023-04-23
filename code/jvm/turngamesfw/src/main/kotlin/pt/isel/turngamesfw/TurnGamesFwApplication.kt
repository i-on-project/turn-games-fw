package pt.isel.turngamesfw

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import pt.isel.turngamesfw.domain.GameProvider
import pt.isel.turngamesfw.domain.UserLogic
import pt.isel.turngamesfw.repository.jdbi.JdbiTransactionManager
import pt.isel.turngamesfw.repository.jdbi.configure
import pt.isel.turngamesfw.services.GameServices
import pt.isel.turngamesfw.utils.RealClock
import pt.isel.turngamesfw.utils.Sha256TokenEncoder
import java.time.Clock

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

	@Bean
	fun userLogic() = UserLogic()

	@Bean
	fun passwordEncoder() = BCryptPasswordEncoder()

	@Bean
	fun tokenEncoder() = Sha256TokenEncoder()

	@Bean
	fun clock() = RealClock
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

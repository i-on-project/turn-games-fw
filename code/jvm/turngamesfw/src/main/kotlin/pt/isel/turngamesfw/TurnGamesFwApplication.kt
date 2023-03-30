package pt.isel.turngamesfw

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import pt.isel.turngamesfw.domain.GameProvider

@SpringBootApplication
class TurnGamesFwApplication {

	@Bean
	fun gameProvider() = GameProvider()

}

fun main(args: Array<String>) {
	runApplication<TurnGamesFwApplication>(*args)
}

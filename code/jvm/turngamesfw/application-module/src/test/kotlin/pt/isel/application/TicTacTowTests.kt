package pt.isel.application

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.tictactow.Position
import pt.isel.tictactow.TicTacTowGameLogic
import pt.isel.turngamesfw.TurnGamesFwApplication
import pt.isel.turngamesfw.gameProvider
import pt.isel.turngamesfw.http.Uris
import pt.isel.turngamesfw.http.model.SirenModel
import pt.isel.turngamesfw.http.model.TurnInputModel
import pt.isel.turngamesfw.services.GameServices
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [TurnGamesFwApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TicTacTowTests {

    @LocalServerPort
    var port: Int = 0

    @Autowired
    private lateinit var gameServices: GameServices

    private val gameName = "TicTacTow"

    @BeforeAll
    @Order(1)
    fun `setup gameProvider`() {
        gameProvider.addGame(gameName, TicTacTowGameLogic())
        gameServices.checkAndSaveAllGames()
    }

    private lateinit var user1Token: String
    private lateinit var user2Token: String

    @BeforeAll
    @Order(2)
    fun `setup users`() {

        // HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // Check if test users exist and get login token, if no register and get token
        user1Token = client.post().uri(Uris.User.LOGIN)
            .bodyValue(
                mapOf(
                    "username" to "PlayerTest1",
                    "password" to "12345"
                )
            )
            .exchange()
            .returnResult(String::class.java)
            .responseCookies["TGFWCookie"]?.first()?.value ?: run {
                client.post().uri(Uris.User.REGISTER)
                    .bodyValue(
                        mapOf(
                            "username" to "PlayerTest1",
                            "password" to "12345"
                        )
                    )
                    .exchange()
                    .expectStatus().isCreated

                return@run client.post().uri(Uris.User.LOGIN)
                    .bodyValue(
                        mapOf(
                            "username" to "PlayerTest1",
                            "password" to "12345"
                        )
                    )
                    .exchange()
                    .expectStatus().isOk
                    .returnResult(String::class.java)
                    .responseCookies["TGFWCookie"]?.first()?.value ?: fail("Error login! No token set in response.")
        }

        user2Token = client.post().uri(Uris.User.LOGIN)
            .bodyValue(
                mapOf(
                    "username" to "PlayerTest2",
                    "password" to "12345"
                )
            )
            .exchange()
            .returnResult(String::class.java)
            .responseCookies["TGFWCookie"]?.first()?.value ?: run {
                client.post().uri(Uris.User.REGISTER)
                    .bodyValue(
                        mapOf(
                            "username" to "PlayerTest2",
                            "password" to "12345"
                        )
                    )
                    .exchange()
                    .expectStatus().isCreated

                return@run client.post().uri(Uris.User.LOGIN)
                    .bodyValue(
                        mapOf(
                            "username" to "PlayerTest2",
                            "password" to "12345"
                        )
                    )
                    .exchange()
                    .expectStatus().isOk
                    .returnResult(String::class.java)
                    .responseCookies["TGFWCookie"]?.first()?.value ?: fail("Error login! No token set in response.")
        }

    }

    private fun makeTurn(client: WebTestClient, matchId: String, playerToken: String, position: Position) {
        val message = client.post().uri(Uris.Game.doTurnByGameName(gameName).toString())
            .cookie("TGFWCookie", playerToken)
            .bodyValue(
                mapOf(
                    "matchId" to "${UUID.fromString(matchId)}",
                    "info" to position
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .responseBody ?: fail("No message receive")

        assertEquals("Next Player.", message)
    }

    private val objMapper = ObjectMapper()

    @Test
    fun `simple game`() {

        // HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // Search Match
        client.post().uri(Uris.Game.findByGameName(gameName).toString())
            .cookie("TGFWCookie", user1Token)
            .exchange()
            .expectStatus().isSeeOther

        client.post().uri(Uris.Game.findByGameName(gameName).toString())
            .cookie("TGFWCookie", user2Token)
            .exchange()
            .expectStatus().isSeeOther

        // Get user 1 Match
        val siren = client.get().uri(Uris.Game.foundByGameName(gameName).toString())
            .cookie("TGFWCookie", user1Token)
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: fail("No match found!")

        val match = objMapper.convertValue(siren.properties, JsonNode::class.java) ?: fail("Error converting to JsonNode")

        // Check if match is correct
        assertEquals(match["gameName"].asText(), gameName)

        // Check if user 2 can get match
        client.get().uri(Uris.Game.foundByGameName(gameName).toString())
            .cookie("TGFWCookie", user2Token)
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: fail("No match found!")

        val matchId = match["id"].asText()

        // Check if is turn of user1
        val myTurn = client.get().uri(Uris.Game.isMyTurn(gameName, matchId).toString())
            .cookie("TGFWCookie", user1Token)
            .exchange()
            .expectStatus().isOk
            .expectBody(Boolean::class.java)
            .returnResult()
            .responseBody ?: fail("No receive response from isMyTurn!")

        val player1Token = if (myTurn) user1Token else user2Token
        val player2Token = if (myTurn) user2Token else user1Token

        // Make a move with player 1
        makeTurn(client, matchId, player1Token, Position(0,0))

        // Check if is turn of player 2
        assertEquals(true, client.get().uri(Uris.Game.isMyTurn(gameName, matchId).toString())
            .cookie("TGFWCookie", user2Token)
            .exchange()
            .expectStatus().isOk
            .expectBody(Boolean::class.java)
            .returnResult()
            .responseBody ?: fail("No receive response from isMyTurn!")
        )

        // Make a wrong move to check the error
        assertEquals("Position not available!", client.post().uri(Uris.Game.doTurnByGameName(gameName).toString())
            .cookie("TGFWCookie", player2Token)
            .bodyValue(
                mapOf(
                    "matchId" to "${UUID.fromString(matchId)}",
                    "info" to Position(0,0)
                )
            )
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody(String::class.java)
            .returnResult()
            .responseBody ?: fail("No message receive")
        )

        // Make other moves
        makeTurn(client, matchId, player2Token, Position(0,1))
        makeTurn(client, matchId, player1Token, Position(1,1))
        makeTurn(client, matchId, player2Token, Position(0,2))

        // Make wining move from player 1
        assertEquals("Player X won! Game Ended.", client.post().uri(Uris.Game.doTurnByGameName(gameName).toString())
            .cookie("TGFWCookie", player1Token)
            .bodyValue(
                mapOf(
                    "matchId" to "${UUID.fromString(matchId)}",
                    "info" to Position(2,2)
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .responseBody ?: fail("No message receive")
        )

    }

}
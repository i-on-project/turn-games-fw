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
import pt.isel.castlerun.CastleRunGameLogic
import pt.isel.castlerun.domain.*
import pt.isel.turngamesfw.TurnGamesFwApplication
import pt.isel.turngamesfw.gameProvider
import pt.isel.turngamesfw.http.Uris
import pt.isel.turngamesfw.http.model.MyTurnOutputModel
import pt.isel.turngamesfw.http.model.SirenModel
import pt.isel.turngamesfw.services.GameServices
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [TurnGamesFwApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CastleRunTests {

    @LocalServerPort
    var port: Int = 0

    @Autowired
    private lateinit var gameServices: GameServices

    private val gameName = "CastleRun"

    @BeforeAll
    @Order(1)
    fun `setup gameProvider`() {
        gameProvider.addGame(gameName, CastleRunGameLogic())
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

    private fun makeTurn(client: WebTestClient, matchId: String, playerToken: String, turn: Turn) {
        val message = client.post().uri(Uris.Game.doTurnByGameName(gameName).toString())
            .cookie("TGFWCookie", playerToken)
            .bodyValue(
                mapOf(
                    "matchId" to "${UUID.fromString(matchId)}",
                    "info" to turn
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .responseBody ?: fail("No message receive")

        assertEquals("Turn completed!", message)
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
            .expectStatus().isBadRequest

        client.post().uri(Uris.Game.findByGameName(gameName).toString())
            .cookie("TGFWCookie", user2Token)
            .exchange()
            .expectStatus().isBadRequest

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
        assertEquals(match["game"]["gameName"].asText(), gameName)

        // Check if user 2 can get match
        client.get().uri(Uris.Game.foundByGameName(gameName).toString())
            .cookie("TGFWCookie", user2Token)
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: fail("No match found!")

        val matchId = match["game"]["id"].asText()

        // Check if is turn of user1
        val myTurn = client.get().uri(Uris.Game.isMyTurn(gameName, matchId).toString())
            .cookie("TGFWCookie", user1Token)
            .exchange()
            .expectStatus().isOk
            .expectBody(MyTurnOutputModel::class.java)
            .returnResult()
            .responseBody ?: fail("No receive response from isMyTurn!")

        val player1Token = if (myTurn.myTurn == true) user1Token else user2Token
        val player2Token = if (myTurn.myTurn == true) user2Token else user1Token

        // Make a move with player 1
        makeTurn(client, matchId, player1Token, Turn("move", Move(null, Coords(4, 0)), null))

        println(client.get().uri(Uris.Game.isMyTurn(gameName, matchId).toString()))
        // Check if is turn of player 2
            assertEquals(true, client.get().uri(Uris.Game.isMyTurn(gameName, matchId).toString())
            .cookie("TGFWCookie", user2Token)
            .exchange()
            .expectStatus().isOk
            .expectBody(MyTurnOutputModel::class.java)
            .returnResult()
            .responseBody?.myTurn ?: fail("No receive response from isMyTurn!")
        )

        // Make other moves
        makeTurn(client, matchId, player2Token, Turn("move", Move(null, Coords(4, 0)), null))
        makeTurn(client, matchId, player1Token, Turn("move", Move(null, Coords(2, 2)), null))
        makeTurn(client, matchId, player2Token, Turn("move", Move(null, Coords(2, 4)), null))

        // Make a duel
        makeTurn(client, matchId, player1Token, Turn("duel", null, Duel(Piece(1, Coords(2, 2)), Piece(2, Coords(2, 4)), Dices(5, 2), 1)))

        assertEquals(true, client.get().uri(Uris.Game.isMyTurn(gameName, matchId).toString())
            .cookie("TGFWCookie", user2Token)
            .exchange()
            .expectStatus().isOk
            .expectBody(MyTurnOutputModel::class.java)
            .returnResult()
            .responseBody?.myTurn ?: fail("No receive response from isMyTurn!")
        )
    }
}
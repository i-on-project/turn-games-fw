package pt.isel.application

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.battleship.BattleshipGameLogic
import pt.isel.battleship.Ship
import pt.isel.battleship.Position
import pt.isel.turngamesfw.TurnGamesFwApplication
import pt.isel.turngamesfw.gameProvider
import pt.isel.turngamesfw.http.Uris
import pt.isel.turngamesfw.http.model.MyTurnOutputModel
import pt.isel.turngamesfw.http.model.SirenModel
import pt.isel.turngamesfw.services.GameServices
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [TurnGamesFwApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BattleshipTests {

    @LocalServerPort
    var port: Int = 0

    @Autowired
    private lateinit var gameServices: GameServices

    private val gameName = "Battleship"

    @BeforeAll
    @Order(1)
    fun `setup gameProvider`() {
        gameProvider.addGame(gameName, BattleshipGameLogic())
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

    private fun makeTurn(client: WebTestClient, matchId: String, playerToken: String, position: Position): String {
        return client.post().uri(Uris.Game.doTurnByGameName(gameName).toString())
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
    }

    private fun makeSetup(client: WebTestClient, matchId: String, playerToken: String, listShip: List<Ship>) {
        val message = client.post().uri(Uris.Game.setupByGameName(gameName).toString())
            .cookie("TGFWCookie", playerToken)
            .bodyValue(
                mapOf(
                    "matchId" to "${UUID.fromString(matchId)}",
                    "info" to listShip
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .responseBody ?: fail("No message receive")

        assertEquals("Setup Done.", message)
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
        Assertions.assertEquals(match["game"]["gameName"].asText(), gameName)

        // Check if user 2 can get match
        client.get().uri(Uris.Game.foundByGameName(gameName).toString())
            .cookie("TGFWCookie", user2Token)
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: fail("No match found!")

        val matchId = match["game"]["id"].asText()

        // Make setup player 1
        makeSetup(client, matchId, user1Token, listOf(
            Ship(Ship.Type.CARRIER, Position(0,0), Ship.Orientation.HORIZONTAL),
            Ship(Ship.Type.BATTLESHIP, Position(0,2), Ship.Orientation.HORIZONTAL),
            Ship(Ship.Type.CRUISER, Position(0,4), Ship.Orientation.HORIZONTAL),
            Ship(Ship.Type.SUBMARINE, Position(0,6), Ship.Orientation.HORIZONTAL),
            Ship(Ship.Type.DESTROYER, Position(0,8), Ship.Orientation.HORIZONTAL)
        ))

        // Check if is still setup
        assertEquals(true, client.get().uri(Uris.Game.isMyTurn(gameName, matchId).toString())
            .cookie("TGFWCookie", user1Token)
            .exchange()
            .expectStatus().isOk
            .expectBody(MyTurnOutputModel::class.java)
            .returnResult()
            .responseBody?.setup ?: fail("No receive response from isMyTurn!")
        )

        // Make setup player 2
        makeSetup(client, matchId, user2Token, listOf(
            Ship(Ship.Type.CARRIER, Position(0,0), Ship.Orientation.VERTICAL),
            Ship(Ship.Type.BATTLESHIP, Position(2,0), Ship.Orientation.VERTICAL),
            Ship(Ship.Type.CRUISER, Position(4,0), Ship.Orientation.VERTICAL),
            Ship(Ship.Type.SUBMARINE, Position(6,0), Ship.Orientation.VERTICAL),
            Ship(Ship.Type.DESTROYER, Position(8,0), Ship.Orientation.VERTICAL)
        ))

        // Check if is not setup anymore
        assertEquals(false, client.get().uri(Uris.Game.isMyTurn(gameName, matchId).toString())
            .cookie("TGFWCookie", user2Token)
            .exchange()
            .expectStatus().isOk
            .expectBody(MyTurnOutputModel::class.java)
            .returnResult()
            .responseBody?.setup ?: fail("No receive response from isMyTurn!")
        )

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
        makeTurn(client, matchId, player1Token, Position(0, 0))

        // Check if is turn of player 2
        assertEquals(true, client.get().uri(Uris.Game.isMyTurn(gameName, matchId).toString())
            .cookie("TGFWCookie", player2Token)
            .exchange()
            .expectStatus().isOk
            .expectBody(MyTurnOutputModel::class.java)
            .returnResult()
            .responseBody?.myTurn ?: fail("No receive response from isMyTurn!")
        )

        makeTurn(client, matchId, player2Token, Position(0, 0))

        // Check if is turn of player 1
        assertEquals(true, client.get().uri(Uris.Game.isMyTurn(gameName, matchId).toString())
            .cookie("TGFWCookie", player1Token)
            .exchange()
            .expectStatus().isOk
            .expectBody(MyTurnOutputModel::class.java)
            .returnResult()
            .responseBody?.myTurn ?: fail("No receive response from isMyTurn!")
        )

    }

}
package pt.isel.turngamesfw.http.model

import org.springframework.http.ResponseEntity
import java.net.URI

const val MEDIA_TYPE = "application/problem+json"
const val GITHUB_URI = "https://github.com/i-on-project/turn-games-fw/docs/problems"

fun problemResponse(problem: Problem) = ResponseEntity
    .status(problem.status)
    .header("Content-Type", MEDIA_TYPE)
    .body(problem)

enum class Problem(
    val type: URI,
    val title: String,
    val status: Int,
) {
    INVALID_ARGUMENTS(
        status = 400,
        type = URI("$GITHUB_URI/invalid-arguments"),
        title = "Invalid arguments"
    ),

    USER_ALREADY_EXISTS(
        status = 400,
        type = URI("$GITHUB_URI/user-already-exists"),
        title = "User already exists"
    ),

    SERVER_ERROR(
        status = 500,
        type = URI("$GITHUB_URI/server-error"),
        title = "Server error",
    ),


    INSECURE_PASSWORD(
        status = 400,
        type = URI("$GITHUB_URI/insecure-password"),
        title = "Insecure password",
    ),

    INVALID_LOGIN(
        status = 400,
        type = URI("$GITHUB_URI/invalid-login"),
        title = "User or Password are invalid",
    ),

    USER_NOT_FOUND(
        status = 404,
        type = URI("$GITHUB_URI/user-not-found"),
        title = "User not found",
    ),

    GAME_NOT_EXIST(
        status = 400,
        type = URI("$GITHUB_URI/game-not-exist"),
        title = "Game not exist",
    ),

    USER_ALREADY_SEARCHING_IN_GAME(
        status = 400,
        type = URI("$GITHUB_URI/user-already-searching-in-game"),
        title = "User already searching or in game",
    ),

    INVALID_MATCH_ID(
        status = 400,
        type = URI("$GITHUB_URI/invalid-match-id"),
        title = "id doesnt represent a UUID",
    ),

    MATCH_NOT_EXIST(
        status = 400,
        type = URI("$GITHUB_URI/match-not-exist"),
        title = "Match with that id not exist",
    ),

    USER_NOT_IN_MATCH(
        status = 400,
        type = URI("$GITHUB_URI/match-not-in-match"),
        title = "User is not in match",
    ),

    NOT_YOUR_TURN(
        status = 400,
        type = URI("$GITHUB_URI/not-your-turn"),
        title = "Not Your Turn",
    ),

    MATCH_STATE(
        status = 400,
        type = URI("$GITHUB_URI/match-state"),
        title = "Not the correct state to do this action",
    ),
}
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
    USER_ALREADY_EXIST(
        status = 400,
        type = URI("$GITHUB_URI/user-already-exists"),
        title = "User already exists",
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
}
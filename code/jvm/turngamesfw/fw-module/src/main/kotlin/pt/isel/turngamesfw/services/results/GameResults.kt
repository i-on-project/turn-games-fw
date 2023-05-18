package pt.isel.turngamesfw.services.results

import pt.isel.fwinterfaces.Match
import pt.isel.turngamesfw.utils.Either

sealed class FindMatchSuccess {
    object SearchingMatch: FindMatchSuccess()
}
sealed class FindMatchError {
    object GameNotExist: FindMatchError()
    object AlreadySearchingOrInGame: FindMatchError()
}
typealias FindMatchResult = Either<FindMatchError, FindMatchSuccess>

sealed class FoundMatchError {
    object UserNotInGame: FoundMatchError()
    object ServerError: FoundMatchError()
}
typealias FoundMatchResult = Either<FoundMatchError, Match>

sealed class MatchByIdError {
    object MatchNotExist: MatchByIdError()
    object ServerError: MatchByIdError()
}
typealias MatchByIdResult = Either<MatchByIdError, Match>

sealed class SetupMatchSuccess {
    data class SetupDone(val resp: Any): SetupMatchSuccess()
    data class ErrorInGameLogic(val resp: Any): SetupMatchSuccess()
}
sealed class SetupMatchError {
    object MatchNotExist: SetupMatchError()
    object UserNotInMatch: SetupMatchError()
    object ServerError: SetupMatchError()
    object MatchStateError: SetupMatchError()

}
typealias SetupMatchResult = Either<SetupMatchError, SetupMatchSuccess>

sealed class DoTurnMatchSuccess {
    data class DoTurnDone(val resp: Any): DoTurnMatchSuccess()
    data class ErrorInGameLogic(val resp: Any): DoTurnMatchSuccess()
}
sealed class DoTurnMatchError {
    object MatchNotExist: DoTurnMatchError()
    object UserNotInMatch: DoTurnMatchError()
    object ServerError: DoTurnMatchError()
    object MatchStateError: DoTurnMatchError()
    object NotYourTurn: DoTurnMatchError()
}
typealias DoTurnMatchResult = Either<DoTurnMatchError, DoTurnMatchSuccess>
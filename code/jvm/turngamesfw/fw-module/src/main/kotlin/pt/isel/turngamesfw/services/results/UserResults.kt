package pt.isel.turngamesfw.services.results

import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.utils.Either

sealed class UserCreationError {
    object UserAlreadyExists : UserCreationError()
    object InvalidArguments: UserCreationError()
    object ServerError: UserCreationError()
}

typealias UserCreationResult = Either<UserCreationError, User>

sealed class TokenCreationError {
    object InvalidArguments : TokenCreationError()
    object UserOrPasswordAreInvalid : TokenCreationError()
}

typealias TokenCreationResult = Either<TokenCreationError, String>
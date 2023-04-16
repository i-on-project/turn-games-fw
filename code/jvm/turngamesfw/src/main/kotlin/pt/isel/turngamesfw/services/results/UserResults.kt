package pt.isel.turngamesfw.services.results

import pt.isel.turngamesfw.utils.Either

sealed class UserCreationError {
    object UserAlreadyExists : UserCreationError()
    object InvalidArguments: UserCreationError()
}

typealias UserCreationResult = Either<UserCreationError, Int>

sealed class TokenCreationError {
    object InvalidArguments : TokenCreationError()
    object UserOrPasswordAreInvalid : TokenCreationError()
}

typealias TokenCreationResult = Either<TokenCreationError, String>
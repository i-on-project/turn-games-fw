interface TokenInputModel {
    token: string
}

enum UserStatus {
    ONLINE = "ONLINE",
    OFFLINE = "OFFLINE",
}

interface UserDetailInputModel {
    id: number
    username: string
    status: UserStatus
}
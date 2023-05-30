enum UserStatus {
    ONLINE = "ONLINE",
    OFFLINE = "OFFLINE",
}

interface UserDetailsOutputModel {
    id: number
    username: string
    status: UserStatus
}

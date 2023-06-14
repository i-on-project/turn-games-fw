class LeaderBoardOutputModel {
    gameName: string
    limit: number
    page: number

    public constructor(gameName: string, limit: number, page: number) {
        this.gameName = gameName
        this.limit = limit
        this.page = page
    }
}

export class SetupInputModel {
    matchId: string
    info: any

    public constructor(matchId: string, info: any) {
        this.matchId = matchId
        this.info = info
    }
}

export class TurnInputModel {
    matchId: string
    info: any

    public constructor(matchId: string, info: any) {
        this.matchId = matchId
        this.info = info
    }
}

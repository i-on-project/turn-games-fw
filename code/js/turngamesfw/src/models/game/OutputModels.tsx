class SetupOutputModel {
    matchId: string //UUID
    info: string //JsonNode

    public constructor(matchId: string, info: string) {
        this.matchId = matchId;
        this.info = info;
    }
}

class TurnOutputModel {
    matchId: string //UUID
    info: string //JsonNode

    public constructor(matchId: string, info: string) {
        this.matchId = matchId;
        this.info = info;
    }
}

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

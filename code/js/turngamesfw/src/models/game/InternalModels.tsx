class Game {
    name: string;
    numPlayers: number;
    description: string;
    rules: string;

    constructor(name: string, numPlayers: number, description: string, rules: string) {
        this.name = name;
        this.numPlayers = numPlayers;
        this.description = description;
        this.rules = rules;
    }
}

class Match {
    id: string; //UUID
    gameName: string;
    state: MatchState;
    players: number[]; //Ids of players
    currPlayer: number; //Id of current player
    currTurn: number;
    deadlineTurn: Date;
    created: Date;
    info: string; //JsonNode

    constructor(id: string, gameName: string, state: MatchState, players: number[], currPlayer: number, currTurn: number, deadlineTurn: Date, created: Date, info: string) {
        this.id = id;
        this.gameName = gameName;
        this.state = state;
        this.players = players;
        this.currPlayer = currPlayer;
        this.currTurn = currTurn;
        this.deadlineTurn = deadlineTurn;
        this.created = created;
        this.info = info;
    }
}
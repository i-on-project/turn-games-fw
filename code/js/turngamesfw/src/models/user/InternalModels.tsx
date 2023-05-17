class User {
    id: number;
    name: string;
    
    constructor(id: number, name: string) {
        this.id = id;
        this.name = name;
    }
}

class LeaderboardUser {
    id: number;
    username: string;
    rating: number;
    position: number;

    constructor(id: number, username: string, rating: number, position: number) {
        this.id = id;
        this.username = username;
        this.rating = rating;
        this.position = position;
    }
}
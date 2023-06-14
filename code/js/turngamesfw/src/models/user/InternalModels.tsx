class User {
    id: number;
    username: string;
    
    constructor(id: number, name: string) {
        this.id = id;
        this.username = name;
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
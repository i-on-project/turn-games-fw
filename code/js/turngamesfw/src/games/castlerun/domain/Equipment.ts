export class Equipment {
    type: Equipment.Type;
    upgraded: boolean;

    constructor(type: Equipment.Type, upgraded: boolean) {
        this.type = type;
        this.upgraded = upgraded;
    }
}

export namespace Equipment {
    export enum Type {
        Shield,
        Sword,
        Axe,
        Banner,
        Boots,
        Horse,
    }
}

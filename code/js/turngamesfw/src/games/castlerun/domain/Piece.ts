import { Coords } from './Coords';
import { Equipment } from './Equipment';

export class Piece {
    owner: number;
    position: Coords;
    equipment: Equipment | null;
    frozen: number;
    isKing: boolean;

    constructor(
        owner: number,
        position: Coords,
        equipment: Equipment | null = null,
        frozen: number = 0,
        isKing: boolean = false
    ) {
        this.owner = owner;
        this.position = position;
        this.equipment = equipment;
        this.frozen = frozen;
        this.isKing = isKing;
    }

    equip(): Piece {
        const type = Equipment.Type[Object.keys(Equipment.Type)[Math.floor(Math.random() * Object.keys(Equipment.Type).length)]];

        return new Piece(
            this.owner,
            this.position,
            this.equipment === null
                ? new Equipment(type, false)
                : this.equipment.upgraded
                    ? this.equipment
                    : new Equipment(type, this.equipment.type === type),
            this.frozen,
            this.isKing
        );
    }
}

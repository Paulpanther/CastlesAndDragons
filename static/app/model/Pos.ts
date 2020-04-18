
export default class Pos {

    public static parse(str: string): Pos {
        const numbers = str.split(",");
        return new Pos(parseInt(numbers[0]), parseInt(numbers[1]))
    }

    constructor(public x: number, public y: number) {}

    public orientationTo(pos: Pos): number {
        if (pos.y < this.y) return 0;
        if (pos.x < this.x) return 1;
        if (pos.y > this.y) return 2;
        else return 3;
    }

    public equals(pos: Pos) {
        return pos.x === this.x && pos.y === this.y;
    }
}
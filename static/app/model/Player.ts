import Grid from "./Grid";

export default class Player {

    public static parse(str: string): Player {
        const parts = str.split(",");
        const id = parseInt(parts[0]);
        const name = parts[1];
        const level = parseInt(parts[2]);
        return new Player(id, name, level);
    }

    public grid: Grid;

    constructor(public id: number, public name: string, public level: number = 0) {}
}
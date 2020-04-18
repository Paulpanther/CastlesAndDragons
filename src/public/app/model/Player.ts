import Grid from "./Grid";

export default class Player {

    public static parse(str: string): Player {
        const parts = str.split(",");
        const id = parseInt(parts[0]);
        const name = parts[1];
        return new Player(id, name);
    }

    public grid: Grid;

    constructor(public id: number, public name: string) {}
}

export default class Pos {

    public static parse(str: string): Pos {
        const numbers = str.split(",");
        return new Pos(parseInt(numbers[0]), parseInt(numbers[1]))
    }

    constructor(public x: number, public y: number) {}
}

export default class Item {

    public static empty(): Item {
        return new Item(0);
    }

    constructor(public readonly type: number, public up: number = 0) {}
}

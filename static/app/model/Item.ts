
export default class Item {

    public static empty(): Item {
        return new Item(0);
    }

    public static pickableTypes(): number[] {
        return [0, 9, 10, 11, 12, 13, 14];
    }

    public static startItems(): Item[] {
        return [
            new Item(1),
            new Item(2),
            new Item(3),
            new Item(4),
            new Item(5),
            new Item(6),
            new Item(7),
            new Item(8),
            new Item(15)
        ];
    }


    constructor(public readonly type: number, public up: number = 0) {}
}

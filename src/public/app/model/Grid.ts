import Item from "./Item";

export default class Grid {

    public items: Item[][] = [];

    constructor(public width: number, public height: number) {
        this.items = [...Array(height)].map(row => {
            return [...Array(width)].map(item => {
                return Item.empty();
            })
        });
    }

    public setFromParsed(str: string) {
        let pos = str.indexOf("h") + 1;
        const itemsArr: Item[] = [];
        while (pos < str.length) {
            const item = parseInt(str[pos], 16);
            if (item === 0) {
                itemsArr.push(new Item(item));
                pos++;
                console.log("0");
            } else {
                const up = parseInt(str[pos + 1]);
                itemsArr.push(new Item(item, up));
                pos += 2;
                console.log(item);
            }
        }

        console.log(itemsArr);
        for (let i = 0; i < itemsArr.length; i++) {
            const x = i % this.width;
            const y = Math.floor(i / this.width);
            this.items[y][x] = itemsArr[i];
        }
    }

    public clear() {
        this.items = [];
        for (let y = 0; y < this.height; y++) {
            this.items.push([]);
            for (let x = 0; x < this.width; x++) {
                this.items[y].push(Item.empty());
            }
        }
    }
}
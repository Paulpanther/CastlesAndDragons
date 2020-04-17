import Item from "./Item";
import Pos from "./Pos";

export default class Grid {

    public items: Item[][] = [];
    public heroPos: Pos = null;

    constructor(public width: number, public height: number) {
        this.items = [...Array(height)].map(row => {
            return [...Array(width)].map(item => {
                return Item.empty();
            })
        });
    }

    public setFromParsed(str: string) {
        this.setHeroFromParsed(str);

        let pos = str.indexOf("p") + 1;
        const itemsArr: Item[] = [];
        while (pos < str.length) {
            const item = parseInt(str[pos], 16);
            if (item === 0) {
                itemsArr.push(new Item(item));
                pos++;
            } else {
                const up = parseInt(str[pos + 1]);
                itemsArr.push(new Item(item, up));
                pos += 2;
            }
        }

        for (let i = 0; i < itemsArr.length; i++) {
            const x = i % this.width;
            const y = Math.floor(i / this.width);
            this.items[y][x] = itemsArr[i];
        }
    }

    private setHeroFromParsed(str: string) {
        const heroI = str.indexOf("h") + 1;
        const heroEndI = str.indexOf("p");
        const side = parseInt(str[heroI]);
        const value = parseInt(str.substr(heroI + 1, heroEndI));
        switch (side) {
            case 0: this.heroPos = new Pos(value, -1); break;
            case 1: this.heroPos = new Pos(-1, value); break;
            case 2: this.heroPos = new Pos(value, this.height); break;
            case 3: this.heroPos = new Pos(this.width, value); break;
        }
    }

    public heroStartPos(): Pos {
        if (this.heroPos.x == -1) return new Pos(this.heroPos.x + 1, this.heroPos.y);
        else if (this.heroPos.x == this.width) return new Pos(this.heroPos.x - 1, this.heroPos.y);
        else if (this.heroPos.y == -1) return new Pos(this.heroPos.x, this.heroPos.y + 1);
        else return new Pos(this.heroPos.x, this.heroPos.y - 1);
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
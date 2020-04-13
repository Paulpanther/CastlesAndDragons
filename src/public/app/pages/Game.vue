<template lang="pug">
    #game
        .board
            .row(v-for="y in parseInt(gameHeight)" :key="y")
                .slot(v-for="x in parseInt(gameWidth)" :key="x" v-on:click="onSlotClick(y-1, x-1, $event)")
                    .item(:class="classForPos(y-1, x-1)")
        button#finish(v-on:click="finish") Finish
        .drawer(v-on:click="onDrawerClick()")
            .item(v-for="item in drawerItems" :key="item.type" :class="classForItem(item)" v-on:click="onItemClick(item, $event)")
        .free-items
            .item(v-if="freeItem !== null" :class="classForItem(freeItem)" v-bind:style="{ top: freeItemPos.y + 'px', left: freeItemPos.x + 'px' }")
        h4(v-if="isGameStarting") Game starts in: {{ gameStartsCountdown }}
</template>

<script lang="ts">
    import {ConnectionListener, Message} from "../Connection";
    import Component from "vue-class-component";
    import * as _ from "lodash";
    import Player from "../model/Player";
    import Grid from "../model/Grid";
    import {EventBus} from "../App.vue";
    import Item from "../model/Item";
    import Pos from "../model/Pos";

    @Component
    export default class Game extends ConnectionListener {

        public gameWidth: number = 0;
        public gameHeight: number = 0;

        private grid: Grid;
        public drawerItems: Item[] = Item.startItems();
        public freeItem: Item = null;
        public freeItemPos: Pos = new Pos(0, 0);
        private justHadItemClick = false;

        public gameStartsDelay: number;
        public gameStartTime: number;
        public isGameStarting = false;
        public gameStartsCountdown = 0;

        public finished = false;

        public self: Player;
        public others: Player[];

        public mounted() {
            EventBus.$on("gamestart", (event) => {
                this.gameWidth = parseInt(event.gameWidth);
                this.gameHeight = parseInt(event.gameHeight);
                this.gameStartsDelay = parseInt(event.gameDelay);
                this.self = event.self;
                this.others = event.others;

                this.grid = new Grid(this.gameWidth, this.gameHeight);
                this.gameStartTime = Date.now() + this.gameStartsDelay;
                this.isGameStarting = true;
                this.gameStartTick();
            });
            document.addEventListener("mousemove", (event: MouseEvent) => {
                if (this.freeItem !== null) {
                    this.freeItemPos = new Pos(event.x, event.y);
                }
            });
            document.addEventListener("keydown", (event: KeyboardEvent) => {
                if (this.freeItem !== null && event.key === "r") {
                    this.freeItem.up = (this.freeItem.up + 1) % 4;
                    this.$forceUpdate();
                }
            });
            // this.testSet()
        }

        public testSet() {
            this.gameWidth = 5;
            this.gameHeight = 3;
            this.gameStartsDelay = 0;
            this.grid = new Grid(5, 3);
            this.grid.items[2][2] = new Item(3, 1);
        }

        public onMessage(message: Message) {
            this.callMethodForMessage(message);
        }

        private callMethodForMessage(message: Message) {
            switch (message.get("type")) {
                case "setGrid": this.setGrid(message);
            }
        }

        public setGrid(message: Message) {
            const forPlayer = Player.parse(message.get("client"));
            if (forPlayer.id === this.self.id) {
                this.grid.setFromParsed(message.get("grid"));
                this.$forceUpdate();
            }
        }

        public onItemClick(item: Item, event) {
            if (this.freeItem === null) {
                const inDrawer = this.drawerItems.findIndex(i => i.type === item.type);
                if (inDrawer !== -1) {
                    this.drawerItems.splice(inDrawer, 1);
                    this.freeItem = item;
                    this.freeItemPos = new Pos(event.x, event.y);
                }
                this.justHadItemClick = true;
            }
        }

        public onDrawerClick() {
            if (this.justHadItemClick) {
                this.justHadItemClick = false;
                return;
            }

            if (this.freeItem !== null) {
                this.drawerItems.push(this.freeItem);
                this.drawerItems = _.sortBy(this.drawerItems, i => i.type);
                this.freeItem = null;
            }
        }

        public onSlotClick(y: number, x: number, event) {
            if (this.justHadItemClick) {
                this.justHadItemClick = false;
                return;
            }

            const itemAtPos = this.grid.items[y][x];
            if (this.freeItem !== null && !this.finished) {
                if (itemAtPos.type === 0) {
                    this.grid.items[y][x] = this.freeItem;
                    this.send(Message.addItem(new Pos(x, y), this.freeItem));
                    this.freeItem = null;
                    this.$forceUpdate();
                }
            } else if (!Item.pickableTypes().includes(itemAtPos.type) && !this.finished) {
                this.grid.items[y][x] = Item.empty();
                this.freeItem = itemAtPos;
                this.freeItemPos = new Pos(event.x, event.y);
                this.send(Message.removeItem(new Pos(x,y), itemAtPos));
            }
        }

        public finish() {
            this.send(Message.finished());
            this.finished = true;
        }

        public classForPos(y: number, x: number): string {
            const item = this.grid.items[y][x];
            return this.classForItem(item);
        }

        public classForItem(item: Item): string {
            const rotClass = `rot-${item.up}`;
            const typeClass = `item-${item.type}`;
            return `${rotClass} ${typeClass}`;
        }

        private gameStartTick() {
            if (this.isGameStarting && this.gameStartTime > Date.now()) {
                setTimeout(this.gameStartTick, 1000);
                this.gameStartsCountdown = Math.floor((this.gameStartTime - Date.now()) / 1000);
            } else {
                this.isGameStarting = false;
            }
        }
    }
</script>

<style lang="css">
#game {
    width: 100%;
    height: 100vh;
}

#game .item {
    width: 100px;
    height: 100px;
    background-size: cover;
}

#game .board {
    position: relative;
    z-index: 0;
    width: 100%;
    background: #00ff00;
}


#game .board .row {
    display: flex;
    justify-content: space-around;
    line-height: 30px;
}

#game .board .row .slot {
    margin: 5px;
    flex: 1 0 auto;
    height: auto;
}

#game .board .row .slot:before {
    content: "";
    float: left;
    padding-top: 100%;
}

#game .board .row .slot .item {
    width: 100%;
    height: 100%;
    border: 1px solid black;
}

#game .drawer {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    width: 100%;
}

#game .drawer .item {
    margin: 10px;
}

#game .free-items {
    position: absolute;
    z-index: 1;
    top: 0;
    left: 0;
}

#game .free-items .item {
    position: absolute;
    pointer-events: none;
    touch-action: none;
}

.item-0 { background-image: url("../assets/empty.png") }
.item-1 { background-image: url("../assets/line_mud.png") }
.item-2 { background-image: url("../assets/line_stone.png") }
.item-3 { background-image: url("../assets/line_both.png") }
.item-4 { background-image: url("../assets/three_1.png") }
.item-5 { background-image: url("../assets/three_2.png") }
.item-6 { background-image: url("../assets/three_3.png") }
.item-7 { background-image: url("../assets/corner_1.png") }
.item-8 { background-image: url("../assets/corner_2.png") }
.item-9 { background-image: url("../assets/castle_stone_1.png") }
.item-10 { background-image: url("../assets/castle_stone_2.png") }
.item-11 { background-image: url("../assets/castle_mud_1.png") }
.item-12 { background-image: url("../assets/castle_mud_2.png") }
.item-13 { background-image: url("../assets/dragon_stone.png") }
.item-14 { background-image: url("../assets/dragon_mud.png") }
.item-15 { background-image: url("../assets/four.png") }
.rot-0 { transform: rotateZ(0deg) }
.rot-1 { transform: rotateZ(90deg) }
.rot-2 { transform: rotateZ(180deg) }
.rot-3 { transform: rotateZ(270deg) }
</style>
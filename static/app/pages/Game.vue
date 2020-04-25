<template lang="pug">
    #game
        h4(v-if="isGameStarting") Game starts in: {{ gameStartsCountdown }}

        Board(
            ref="board"
            :clickable="gameStarted && !finished"
            v-on:slot-click="onSlotClick($event)")

        button#finish(v-on:click="finish") Finish

        .drawer(v-on:click="onDrawerClick()")
            ItemComponent(
                v-for="item in drawerItems"
                :key="item.type"
                :item="item"
                @click="onItemClick(item, $event)")

        .free-items
            ItemComponent(
                v-if="freeItem !== null"
                :item="freeItem"
                rotatable="true"
                v-bind:style="{ top: freeItemPos.y + 'px', left: freeItemPos.x + 'px' }")
</template>

<script lang="ts">
    import Component from "vue-class-component";
    import * as _ from "lodash";
    import {ConnectionListener, Message} from "../Connection";
    import Player from "../model/Player";
    import {EventBus} from "../App.vue";
    import Item from "../model/Item";
    import Pos from "../model/Pos";
    import Board, {SlotClickEvent} from "../components/Board.vue";
    import ItemComponent from "../components/ItemComponent.vue";

    @Component({ components: {Board, ItemComponent} })
    export default class Game extends ConnectionListener {

        public drawerItems: Item[] = Item.startItems();
        public freeItem: Item = null;
        public freeItemPos: Pos = new Pos(0, 0);
        private justHadItemClick = false;

        public gameStartsDelay: number;
        public gameStartTime: number;
        public isGameStarting = false;
        public gameStartsCountdown = 0;
        public gameStarted = false;

        public finished = false;

        public self: Player;
        public others: Player[];

        $refs!: {
            board: Board
        };

        public mounted() {
            EventBus.$on("gamestart", (event) => {
                this.$refs.board.gameWidth = parseInt(event.gameWidth);
                this.$refs.board.gameHeight = parseInt(event.gameHeight);
                this.gameStartsDelay = parseInt(event.gameDelay);
                this.self = event.self;
                this.$refs.board.player = event.self;
                this.others = event.others;

                this.gameStartTime = Date.now() + this.gameStartsDelay;
                this.isGameStarting = true;
                this.gameStartTick();
                this.$refs.board.start();
            });
            document.addEventListener("mousemove", (event: MouseEvent) => {
                if (this.freeItem !== null) {
                    this.freeItemPos = new Pos(event.x, event.y);
                }
            });
        }

        public onMessage(message: Message) {
            this.callMethodForMessage(message);
        }

        private callMethodForMessage(message: Message) {
            switch (message.get("type")) {
                case "setGrid": return this.setGrid(message);
                case "finished": return this.trueFinished(message);
                case "notFinished": return this.falseFinished(message);
                case "gameStart": return this.nextGame(message);
            }
        }

        private falseFinished(message: Message) {
            const player = Player.parse(message.get("client"));
            if (player.id === this.self.id) {
                alert("You lost")
            }
        }

        private trueFinished(message: Message) {
            const player = Player.parse(message.get("client"));
            if (player.id === this.self.id) {
                alert("You won!");
            } else {
                alert(`Player "${player.name} won!`)
            }
        }

        public nextGame(message: Message) {
            EventBus.$emit("gamestart", {
                gameWidth: this.$refs.board.grid.width,
                gameHeight: this.$refs.board.grid.height,
                gameDelay: message.get("delay"),
                self: this.self,
                others: this.others
            });
        }

        public setGrid(message: Message) {
            this.gameStarted = true;
            const forPlayer = Player.parse(message.get("client"));
            if (forPlayer.id === this.self.id) {
                this.$refs.board.updateGrid(message.get("grid"));
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

        public onSlotClick(click: SlotClickEvent) {
            const x = click.x;
            const y = click.y;
            const event = click.event;

            if (this.justHadItemClick) {
                this.justHadItemClick = false;
                return;
            }

            const itemAtPos = this.$refs.board.getItem(x, y);
            if (this.freeItem !== null && !this.finished) {
                if (itemAtPos.type === 0) {
                    this.$refs.board.setItem(x, y, this.freeItem);
                    this.send(Message.addItem(new Pos(x, y), this.freeItem));
                    this.freeItem = null;
                }
            } else if (!Item.pickableTypes().includes(itemAtPos.type) && !this.finished) {
                this.$refs.board.setItem(x, y, Item.empty());
                this.freeItem = itemAtPos;
                this.freeItemPos = new Pos(event.x, event.y);
                this.send(Message.removeItem(new Pos(x,y), itemAtPos));
            }
        }

        public finish() {
            this.send(Message.finished());
            this.finished = true;
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

<style lang="sass" scoped>

#game
    width: 100%
    height: 100vh

    .item
        width: 100px
        height: 100px
        background-size: cover

    .drawer
        display: flex
        flex-direction: row
        flex-wrap: wrap
        width: 100%

        .item
            margin: 10px

    .free-items
        position: absolute
        z-index: 1
        top: 0
        left: 0

        .item
            position: absolute
            pointer-events: none
            touch-action: none
</style>
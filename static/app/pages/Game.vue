<template lang="pug">
    #game
        h4(v-if="isGameStarting") Game starts in: {{ gameStartsCountdown }}

        .game-grid
            Board#player-board(
                ref="board"
                :clickable="gameStarted && !finished"
                v-on:slot-click="onSlotClick($event)")

            .player-list-and-finish
                PlayerList(ref="playerList" :players="others")
                button#finish(v-on:click="finish") Finish

            .drawer(v-on:click="onDrawerClick()")
                .slot(
                    v-for="index in drawerStartItemsCount"
                    :key="index")
                    ItemComponent(
                        v-show="drawerItems[index - 1]"
                        :item="drawerItems[index - 1]"
                        @click="onItemClick(drawerItems[index - 1], $event)")

        .free-items
            ItemComponent(
                v-show="freeItem"
                :item="freeItem"
                rotatable="true"
                v-bind:style="{\
                    top: freeItemPos.y + 'px',\
                    left: freeItemPos.x + 'px',\
                    width: freeItemSize.width + 'px',\
                    height: freeItemSize.height + 'px' }")
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
    import PlayerList from "../components/PlayerList.vue";

    @Component({ components: {PlayerList, Board, ItemComponent} })
    export default class Game extends ConnectionListener {

        public drawerItems: Item[] = Item.startItems();
        public readonly drawerStartItemsCount = Item.startItems().length;

        public freeItem: Item = null;
        public freeItemPos: Pos = new Pos(0, 0);
        public freeItemSize = {width: 0, height: 0};
        private justHadItemClick = false;

        public gameStartsDelay: number;
        public gameStartTime: number;
        public isGameStarting = false;
        public gameStartsCountdown = 0;
        public gameStarted = false;

        public finished = false;

        public self: Player;
        public others: Player[] = [];

        $refs!: {
            board: Board,
            playerList: PlayerList
        };

        public mounted() {
            EventBus.$on("gamestart", (event) => {
                this.startListening();

                const gameWidth = parseInt(event.gameWidth);
                const gameHeight = parseInt(event.gameHeight);

                this.drawerItems = Item.startItems();
                this.finished = false;
                this.gameStartsDelay = parseInt(event.gameDelay);
                this.self = event.self;
                this.others = event.others;

                this.$refs.board.gameWidth = gameWidth;
                this.$refs.board.gameHeight = gameHeight;
                this.$refs.board.player = event.self;
                this.$refs.playerList.startWidth = gameWidth;
                this.$refs.playerList.startHeight = gameHeight;

                this.gameStartTime = Date.now() + this.gameStartsDelay;
                this.isGameStarting = true;
                this.gameStartTick();
                this.$refs.board.start();
            });
            document.addEventListener("mousemove", (event: MouseEvent) => {
                if (this.freeItem !== null) {
                    this.freeItemPos = new Pos(event.x - this.freeItemSize.width / 2,
                        event.y - this.freeItemSize.height / 2);
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
                case "won": return this.won(message);
                case "gameStart": return this.nextGame(message);
                case "left": return this.onLeave(message);
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
                alert(`Player "${player.name}" won!`)
            }
        }

        public won(message: Message) {
            const player = Player.parse(message.get("client"));
            if (player.id === this.self.id) {
                alert("You won the Game!");
            } else {
                alert(`Player "${player.name}" won the Game!`);
            }
            this.startWaitingRoom();
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

        public startWaitingRoom() {
            this.stopListening();
            EventBus.$emit("gameend", {
                self: this.self,
                others: this.others,
            });
        }

        public setGrid(message: Message) {
            this.gameStarted = true;
            const forPlayer = Player.parse(message.get("client"));
            if (forPlayer.id === this.self.id) {
                this.$refs.board.updateGrid(message.get("grid"));
            }
        }

        public onLeave(message: Message) {
            const forPlayer = Player.parse(message.get("client"));
            if (forPlayer.id !== this.self.id) {
                const otherPlayerIndex = this.others.findIndex(p => p.id === forPlayer.id);
                if (otherPlayerIndex !== -1) {
                    this.others.splice(otherPlayerIndex, 1);
                    this.startWaitingRoom();
                }
            }
        }

        public onItemClick(item: Item, event) {
            if (this.freeItem === null) {
                const inDrawer = this.drawerItems.findIndex(i => i && i.type === item.type);
                if (inDrawer !== -1) {
                    this.drawerItems[inDrawer] = null;
                    this.freeItem = item;
                    this.updateFreeItemSize();
                    this.freeItemPos = new Pos(event.x - this.freeItemSize.width / 2,
                        event.y - this.freeItemSize.height / 2);
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
                const drawerPos = Item.startItems().findIndex(i => i.type === this.freeItem.type);
                if (drawerPos !== -1 && this.drawerItems[drawerPos] === null) {
                    this.drawerItems[drawerPos] = this.freeItem;
                    this.freeItem = null;
                }
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
                this.freeItemPos = new Pos(event.x - this.freeItemSize.width / 2,
                    event.y - this.freeItemSize.height / 2);
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

        public updateFreeItemSize() {
            const slot = document.querySelector("#player-board .slot") as HTMLDivElement;
            if (slot) {
                this.freeItemSize = {
                    width: slot.clientWidth,
                    height: slot.clientHeight,
                }
            } else {
                this.freeItemSize = {
                    width: 0,
                    height: 0
                }
            }
        }
    }
</script>

<style lang="sass" scoped>

#game
    display: flex
    flex-direction: column
    justify-content: center

    width: 800px
    height: 100vh

    margin: 0 auto

    .item
        background-size: cover

    .game-grid
        display: grid
        grid-template-columns: 4fr 1fr
        grid-template-rows: auto auto
        grid-template-areas: "board player-list-and-finish" "drawer drawer"
        gap: 20px

        .board
            grid-area: board
            flex-grow: 1
            height: 100%

        .player-list-and-finish
            grid-area: player-list-and-finish
            display: flex
            flex-direction: column

            .player-list
                width: 100%

            #finish
                flex-grow: 1

    .drawer
        grid-area: drawer
        display: flex
        flex-direction: row
        justify-content: stretch
        width: 100%

        background: #ddd

        .slot
            width: inherit
            margin: 10px

            &::before
                content: ""
                float: left
                padding-top: 100%

            .item
                width: 100%
                height: 100%


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
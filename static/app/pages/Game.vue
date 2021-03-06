<template lang="pug">
    #game
        .game-grid
            Board#player-board(
                ref="board"
                :clickable="gameStarted && !finished"
                v-on:slot-click="onSlotClick($event)")

            .player-list-and-finish
                PlayerList(ref="playerList" :players="others")
                button#finish(v-on:click="finish") Finish

            .bottom-row
                MessageDisplay(ref="messageDisplay")
                Drawer(
                    ref="drawer"
                    @click="onDrawerClick($event)"
                    @item-click="onItemClick($event.item, $event)")

        .free-items
            ItemComponent(
                v-show="freeItem"
                :item="freeItem"
                rotatable="true"
                :style="{\
                    top: freeItemPos.y + 'px',\
                    left: freeItemPos.x + 'px',\
                    width: freeItemSize.width + 'px',\
                    height: freeItemSize.height + 'px' }")
</template>

<script lang="ts">
    import Component from "vue-class-component";
    import {ConnectionListener, Message} from "../Connection";
    import Player from "../model/Player";
    import {EventBus} from "../App.vue";
    import Item from "../model/Item";
    import Pos from "../model/Pos";
    import Board, {SlotClickEvent} from "../components/Board.vue";
    import ItemComponent from "../components/ItemComponent.vue";
    import PlayerList from "../components/PlayerList.vue";
    import Drawer from "../components/Drawer.vue";
    import MessageDisplay from "../components/MessageDisplay.vue";

    @Component({ components: {MessageDisplay, Drawer, PlayerList, Board, ItemComponent} })
    export default class Game extends ConnectionListener {

        public freeItem: Item = null;
        public freeItemPos: Pos = new Pos(0, 0);
        public freeItemSize = {width: 0, height: 0};
        private justHadItemClick = false;

        public isGameStarting = false;
        public gameStartsCountdown = 0;
        public gameStarted = false;

        public finished = false;

        public self: Player;
        public others: Player[] = [];

        $refs!: {
            board: Board,
            playerList: PlayerList,
            drawer: Drawer,
            messageDisplay: MessageDisplay
        };

        public mounted() {
            EventBus.$on("gamestart", (event) => {
                this.startListening();

                const gameWidth = parseInt(event.gameWidth);
                const gameHeight = parseInt(event.gameHeight);

                this.self = event.self;
                this.others = event.others;

                this.$refs.board.gameWidth = gameWidth;
                this.$refs.board.gameHeight = gameHeight;
                this.$refs.board.player = event.self;
                this.$refs.playerList.startWidth = gameWidth;
                this.$refs.playerList.startHeight = gameHeight;

                this.reset();

                this.$refs.board.start();

                setTimeout(() => {
                    this.$refs.messageDisplay.showTimer("Starting Game", parseInt(event.gameDelay));
                }, 10);
            });
            document.addEventListener("mousemove", (event: MouseEvent) => {
                if (this.freeItem !== null) {
                    this.freeItemPos = new Pos(event.x - this.freeItemSize.width / 2,
                        event.y - this.freeItemSize.height / 2);
                }
            });
        }

        private reset() {
            this.finished = false;
            this.isGameStarting = true;
            this.gameStarted = false;
            this.$refs.drawer.reset();
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
                case "left": return this.onLeave(message);
            }
        }

        private falseFinished(message: Message) {
            const player = Player.parse(message.get("client"));
            if (player.id === this.self.id) {
                this.$refs.messageDisplay.show("You lost", -1);
                this.finished = true;
            }
        }

        private trueFinished(message: Message) {
            const player = Player.parse(message.get("client"));
            const delay = parseInt(message.get("delay"));
            if (player.id === this.self.id) {
                this.$refs.messageDisplay.showTimer("You won this round", delay);
            } else {
                this.$refs.messageDisplay.showTimer(`Player "${player.name}" won this round`, delay);
            }
            this.reset();
        }

        public won(message: Message) {
            const player = Player.parse(message.get("client"));
            const delay = parseInt(message.get("delay"));
            if (player.id === this.self.id) {
                this.$refs.messageDisplay.showTimer("You won the Game", delay);
            } else {
                this.$refs.messageDisplay.showTimer(`Player "${player.name}" won the Game`, delay);
            }
            this.reset();
            setTimeout(() => {
                this.startWaitingRoom();
            }, delay);
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
            if (this.freeItem === null && !this.finished) {
                const inDrawer = this.$refs.drawer.removeItemWithType(item.type);
                if (inDrawer) {
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

            if (this.freeItem !== null && !this.finished) {
                if (this.$refs.drawer.setItem(this.freeItem)) {
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

    width: 900px
    height: 100vh

    margin: 0 auto

    .item
        background-size: cover

    .game-grid
        display: grid
        grid-template-columns: 4fr 0.9fr
        grid-template-rows: auto auto
        grid-template-areas: "board player-list-and-finish" "bottom-row bottom-row"
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

    .bottom-row
        grid-area: bottom-row
        position: relative
        overflow-y: hidden

        .message-display
            position: absolute
            width: 100%
            height: 100%

        .drawer
            grid-area: drawer

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
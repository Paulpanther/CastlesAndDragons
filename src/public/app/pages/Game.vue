<template lang="pug">
    #game
        .board
            .row(v-for="y in parseInt(gameHeight)" :key="y")
                .slot(v-for="x in parseInt(gameWidth)" :key="x")
                    .item(:class="classForItem(y - 1, x - 1)")
        h4(v-if="isGameStarting") Game starts in: {{ gameStartsCountdown }}
</template>

<script lang="ts">
    import {ConnectionListener, Message} from "../Connection";
    import Component from "vue-class-component";
    import Player from "../model/Player";
    import Grid from "../model/Grid";
    import {EventBus} from "../App.vue";

    @Component
    export default class Game extends ConnectionListener {

        public gameWidth: number = 0;
        public gameHeight: number = 0;

        private grid: Grid;

        public gameStartsDelay: number;
        public gameStartTime: number;
        public isGameStarting = false;
        public gameStartsCountdown = 0;

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

        public classForItem(y: number, x: number): string {
            const item = this.grid.items[y][x];
            const rotClass = `rot-${item.up}`;
            const typeClass = `item-${item.type}`;
            return `${rotClass} ${typeClass}`;
        }

        private gameStartTick() {
            if (this.isGameStarting && this.gameStartTime > Date.now()) {
                setTimeout(this.gameStartTick, 1000);
                this.gameStartsCountdown = Math.floor((this.gameStartTime - Date.now()) / 1000);
            }
        }
    }
</script>

<style lang="css">
#game {
    width: 100%;
    height: 100vh;
}

#game .board {
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
    background-size: cover;
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
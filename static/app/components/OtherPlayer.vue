<template lang="pug">
    .other-player
        .wrapper
            Board(
                ref="board"
                :clickable="false")
            .message-box(v-show="message")
                .message {{ message }}
        .player-info
            .name {{ player.name }}
            LevelCounter(:level="player.level")

</template>

<script lang="ts">

    import Component from "vue-class-component";
    import {Prop} from "vue-property-decorator";
    import Player from "../model/Player";
    import LevelCounter from "./LevelCounter.vue";
    import {ConnectionListener, Message} from "../Connection";
    import Board from "./Board.vue";

    @Component({ components: {Board, LevelCounter} })
    export default class OtherPlayer extends ConnectionListener {

        @Prop({ default: null })
        public player: Player;

        @Prop({ default: 0 })
        public startWidth: number;
        @Prop({ default: 0 })
        public startHeight: number;

        public message: string = null;

        $refs!: {
            board: Board
        };

        public mounted() {
            this.startListening();
            this.$refs.board.start(this.startWidth, this.startHeight);
        }

        public onMessage(message: Message) {
            if (message.get("type") === "setGrid") {
                const player = Player.parse(message.get("client"));
                if (player.id === this.player.id) {
                    this.$refs.board.updateGrid(message.get("grid"));
                    this.message = null;
                }
            }
            if (message.get("type") === "notFinished") {
                const player = Player.parse(message.get("client"));
                if (player.id === this.player.id) {
                    this.message = "Lost";
                }
            }
            if (message.get("type") === "finished" || message.get("type") === "won") {
                const player = Player.parse(message.get("client"));
                if (player.id === this.player.id) {
                    this.player.level = player.level;
                    this.message = "Won";
                }
            }
        }

        public destroyed() {
            this.stopListening();
        }
    }
</script>

<style lang="sass" scoped>
    .other-player
        margin-bottom: 10px

        .wrapper
            position: relative

            .board
                width: 100%

            .message-box
                position: absolute
                z-index: 50

                display: flex
                justify-content: center
                align-items: center

                width: 100%
                height: 100%
                top: 0

                background: rgba(255, 255, 255, 0.4)

        .player-info
            display: flex
            flex-direction: row
            justify-content: space-between
</style>
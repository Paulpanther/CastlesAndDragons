<template lang="pug">
    .other-player
        Board(
            ref="board"
            :clickable="false")
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

        .board
            width: 100%

        .player-info
            display: flex
            flex-direction: row
            justify-content: space-between
</style>
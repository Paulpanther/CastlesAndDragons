<template lang="pug">
    .board
        .row(v-for="y in parseInt(gameHeight)" :key="y")
            .slot(v-for="x in parseInt(gameWidth)" :key="x" v-on:click="onSlotClick(y-1, x-1, $event)")
                .item(:class="classForPos(y-1, x-1)")
</template>

<script lang="ts">
    import Vue from "vue";
    import Component from "vue-class-component";
    import Grid from "../model/Grid";
    import Player from "../model/Player";
    import Item from "../model/Item";
    import {classForItem} from "./util";

    export interface SlotClickEvent {
        x: number;
        y: number;
        event: MouseEvent;
    }

    @Component
    export default class Board extends Vue {

        public gameWidth: number;
        public gameHeight: number;
        public player: Player;

        public grid: Grid;

        public start() {
            this.grid = new Grid(this.gameWidth, this.gameHeight);
        }

        public updateGrid(message: string) {
            this.grid.setFromParsed(message);
            this.$forceUpdate();
        }

        public getItem(x: number, y: number): Item {
            return this.grid[y][x];
        }

        public setItem(x: number, y: number, item: Item) {
            this.grid[y][x] = item;
            this.$forceUpdate();
        }

        public classForPos(y: number, x: number): string {
            const item = this.grid.items[y][x];
            return classForItem(item);
        }

        public onSlotClick(y: number, x: number, event: MouseEvent) {
            this.$emit("slot-click", {
                x, y, event
            });
        }
    }
</script>

<style lang="sass">

@use "./_items.sass"

#game
    width: 100%
    height: 100vh

    .item
        width: 100px
        height: 100px
        background-size: cover

    .board
        position: relative
        z-index: 0
        width: 100%
        background: #00ff00

        .row
            display: flex
            justify-content: space-around
            line-height: 30px

            .slot
                margin: 5px
                flex: 1 0 auto
                height: auto

                &:before
                    content: ""
                    float: left
                    padding-top: 100%

                .item
                    width: 100%
                    height: 100%
                    border: 1px solid black
</style>
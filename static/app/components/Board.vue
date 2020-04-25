<template lang="pug">
    .board(v-show="isStarted")
        .row(
            v-for="y in gameHeight"
            :key="y")
            .slot(
                v-for="x in gameWidth"
                :class="classForSlot(getRealPos(x, y))"
                :key="x"
                v-on:click="onSlotClick(getRealPos(x, y), $event)")
                ItemComponent(:item="itemAt(getRealPos(x, y))")
</template>

<script lang="ts">
    import Vue from "vue";
    import Component from "vue-class-component";
    import Grid from "../model/Grid";
    import Player from "../model/Player";
    import Item from "../model/Item";
    import Pos from "../model/Pos";
    import ItemComponent from "./ItemComponent.vue";

    export interface SlotClickEvent {
        x: number;
        y: number;
        event: MouseEvent;
    }

    @Component({ components: {ItemComponent} })
    export default class Board extends Vue {

        public gameWidth: number = 0;
        public gameHeight: number = 0;
        public player: Player;
        public isStarted = false;

        public grid: Grid;

        public start() {
            this.isStarted = true;
            this.grid = new Grid(this.gameWidth, this.gameHeight);
        }

        public updateGrid(message: string) {
            this.grid.setFromParsed(message);
            this.$forceUpdate();
        }

        public getItem(x: number, y: number): Item {
            return this.grid.items[y][x];
        }

        public setItem(x: number, y: number, item: Item) {
            this.grid.items[y][x] = item;
            this.$forceUpdate();
        }

        public classForSlot(pos: Pos): string {
            const onHero = this.grid.heroPos != null && this.grid.heroStartPos().equals(pos);
            if (onHero) {
                const dir = pos.orientationTo(this.grid.heroPos);
                return "hero-" + dir;
            }
            return "";
        }

        public itemAt(pos: Pos): Item {
            return this.grid.items[pos.y][pos.x];
        }

        public onSlotClick(pos: Pos, event: MouseEvent) {
            console.log(pos);
            this.$emit("slot-click", {
                x: pos.x, y: pos.y, event
            });
        }

        public getRealPos(x: number, y: number) {
            // vue arrays start at 1
            return new Pos(x - 1, y - 1);
        }
    }
</script>

<style lang="sass" scoped>

.board
    position: relative
    z-index: 0
    width: 100%
    background: #8BC34A

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
                z-index: 1
                width: 100%
                height: 100%
                border: 1px solid black

            &[class*=" hero"]:before
                z-index: 2
                display: block
                position: relative

                width: 100%
                height: 100%
                bottom: 50%

                pointer-events: none
                touch-action: none

                background: url("../assets/knight.png") no-repeat center

            &.hero-0:before
                top: -100%
                transform: rotate(90deg)
            &.hero-1:before
                left: -50%
                transform: rotate(0deg)
            &.hero-2:before
                bottom: 0
                transform: rotate(270deg)
            &.hero-3:before
                right: -50%
                transform: rotate(180deg)
</style>
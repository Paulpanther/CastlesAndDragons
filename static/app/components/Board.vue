<template lang="pug">
    .board(v-show="isStarted")
        .field
            .row(
                v-for="y in gameHeight"
                :key="y")
                .slot(
                    v-for="x in gameWidth"
                    :class="classForSlot(getRealPos(x, y))"
                    :key="x"
                    v-on:click="onSlotClick(getRealPos(x, y), $event)")
                    .wrapper
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
    import {Prop} from "vue-property-decorator";

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

        @Prop({default: false})
        public clickable: boolean;

        public start(width?: number, height?: number) {
            this.isStarted = true;
            if (width) this.gameWidth = width;
            if (height) this.gameHeight = height;
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
            if (this.clickable) {
                this.$emit("slot-click", {
                    x: pos.x, y: pos.y, event
                });
            }
        }

        public getRealPos(x: number, y: number) {
            // vue arrays start at 1
            return new Pos(x - 1, y - 1);
        }
    }
</script>

<style lang="sass" scoped>
    .board
        display: flex
        flex-direction: column
        justify-content: center

        .field
            max-height: 100%
            max-width: 100%

        .row
            display: flex
            justify-content: space-around
            line-height: 30px

            .slot
                margin: calc(.5% - 1px)
                flex: 1 0 auto
                height: auto
                background: #8BC34A

                &:first-of-type
                    margin-left: 0

                &:last-of-type
                    margin-right: 0

                &:before
                    content: ""
                    float: left
                    padding-top: 100%

                .item
                    z-index: 1
                    width: 100%
                    height: 100%

                .wrapper
                    width: 100%
                    height: 100%

                    &:before
                        display: block
                        content: ""
                        float: left

                &[class*=" hero"] .wrapper:before
                    z-index: 2
                    position: relative

                    width: 100%
                    height: 100%

                    pointer-events: none
                    touch-action: none

                    background: url("../assets/knight.png") no-repeat center
                    background-size: 30%

                $hero-offset: -47%

                &.hero-0 .wrapper:before
                    top: $hero-offset
                    transform: rotate(90deg)
                &.hero-1 .wrapper:before
                    left: $hero-offset
                    transform: rotate(0deg)
                &.hero-2 .wrapper:before
                    bottom: $hero-offset
                    transform: rotate(270deg)
                &.hero-3 .wrapper:before
                    right: $hero-offset
                    transform: rotate(180deg)

            &:first-of-type
                .slot
                    margin-top: 0

            &:last-of-type
                .slot
                    margin-bottom: 0
</style>
<template lang="pug">
    .item(
        v-show="item"
        :data-type="type"
        :data-rotation="rotation"
        :data-rotatable="rotatable"
        :class="{no_transition: !useTransition}"
        @click="$emit('click', $event)")
</template>

<script lang="ts">
    import Vue from "vue";
    import Component from "vue-class-component";
    import {Prop, Watch} from "vue-property-decorator";
    import Item from "../model/Item";
    import {mod} from "./util";

    const rotationTime = 200;

    @Component
    export default class ItemComponent extends Vue {

        @Prop({default: null})
        public item: Item;

        @Prop({default: false})
        public rotatable: boolean

        public type = 0;
        public rotation = 0;
        // public useTransition = true;
        public useTransition = false;

        private isRotating = false;

        public mounted() {
            document.addEventListener("keydown", this.keyListener);
            this.type = this.item?.type || this.type;
            this.updateRotation();
        }

        public destroyed() {
            document.removeEventListener("keydown", this.keyListener);
        }

        private keyListener(event: KeyboardEvent) {
            if (this.rotatable && event.key === "r") {
                this.rotate();
            }
        }

        private rotate() {
            if (!this.isRotating) {
                // this.useTransition = true;
                //
                // this.item.up = mod(this.item.up - 1, 4);
                // this.rotation = this.item.up;
                //
                // this.isRotating = true;
                //
                // setTimeout(() => {
                //     if (this.rotation === 0) {
                //         this.rotation = 4;
                //     }
                //     this.isRotating = false;
                // }, rotationTime);
                this.item.up = mod(this.item.up + 1, 4);
                this.rotation = this.item.up;
                console.log("ROTATEEEEEE");
            }
        }

        private updateRotation(item = this.item) {
            if (item) {
                // this.rotation = item.up === 0 ? 4 : item.up;
                this.rotation = item.up;
            }
        }

        @Watch("item")
        public onItemChange(newItem: Item, oldItem: Item) {
            this.useTransition = false;
            this.updateRotation(newItem);
            this.type = newItem.type;
        }
    }
</script>

<style lang="sass" scoped>
.item
    background-size: cover

    &[data-rotatable]
        transition: transform ease 0.2s

    &.no_transition
        transition: none

    &[data-type="0"]
        background-image: url("../assets/empty.png")
    &[data-type="1"]
        background-image: url("../assets/line_mud.png")
    &[data-type="2"]
        background-image: url("../assets/line_stone.png")
    &[data-type="3"]
        background-image: url("../assets/line_both.png")
    &[data-type="4"]
        background-image: url("../assets/three_1.png")
    &[data-type="5"]
        background-image: url("../assets/three_2.png")
    &[data-type="6"]
        background-image: url("../assets/three_3.png")
    &[data-type="7"]
        background-image: url("../assets/corner_1.png")
    &[data-type="8"]
        background-image: url("../assets/corner_2.png")
    &[data-type="9"]
        background-image: url("../assets/castle_stone_1.png")
    &[data-type="10"]
        background-image: url("../assets/castle_stone_2.png")
    &[data-type="11"]
        background-image: url("../assets/castle_mud_1.png")
    &[data-type="12"]
        background-image: url("../assets/castle_mud_2.png")
    &[data-type="13"]
        background-image: url("../assets/dragon_stone.png")
    &[data-type="14"]
        background-image: url("../assets/dragon_mud.png")
    &[data-type="15"]
        background-image: url("../assets/four.png")

    &[data-rotation="0"]
        transform: rotateZ(0deg)
    &[data-rotation="1"]
        transform: rotateZ(90deg)
    &[data-rotation="2"]
        transform: rotateZ(180deg)
    &[data-rotation="3"]
        transform: rotateZ(270deg)
    &[data-rotation="4"]
        transform: rotateZ(360deg)
        transition: transform ease 0s
</style>
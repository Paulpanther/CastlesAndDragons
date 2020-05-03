<template lang="pug">
    .drawer(@click="onDrawerClick()")
        .slot(
            v-for="index in drawerStartItemsCount"
            :key="index")
            ItemComponent(
                v-show="drawerItems[index - 1]"
                :item="drawerItems[index - 1]"
                @click="onItemClick(drawerItems[index - 1], $event)")
</template>

<script lang="ts">
    import Vue from "vue";
    import Component from "vue-class-component";
    import Item from "../model/Item";
    import ItemComponent from "./ItemComponent.vue";

    @Component({ components: {ItemComponent} })
    export default class Drawer extends Vue {

        public drawerItems: Item[] = Item.startItems();
        public readonly drawerStartItemsCount = Item.startItems().length;

        public removeItemWithType(type: number): Item {
            const index = this.drawerItems.findIndex(i => i && i.type === type);
            if (index !== -1) {
                const item = this.drawerItems[index];
                this.$set(this.drawerItems, index, null);
                return item;
            }
            return null;
        }

        public setItem(item: Item): boolean {
            const drawerPos = Item.startItems().findIndex(i => i.type === item.type);
            if (drawerPos !== -1 && this.drawerItems[drawerPos] === null) {
                this.$set(this.drawerItems, drawerPos, item);
                return true;
            }
            return false;
        }

        public reset() {
            this.drawerItems = Item.startItems();
        }

        public onDrawerClick(event) {
            this.$emit("click", event);
        }

        public onItemClick(item: Item, event) {
            event.item = item;
            this.$emit("item-click", event);
        }
    }
</script>

<style lang="sass" scoped>
    .drawer
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
</style>
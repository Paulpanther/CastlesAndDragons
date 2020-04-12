<template lang="pug">
    #game
        .board
            .row(v-for="y in parseInt(gameHeight)" :key="y")
                .slot(v-for="x in parseInt(gameWidth)" :key="x") {{ x }}
</template>

<script lang="ts">
    import {ConnectionListener, Message} from "../Connection";
    import Component from "vue-class-component";
    import {Prop} from "vue-property-decorator";
    import Item from "../model/Item";

    @Component
    export default class Game extends ConnectionListener {

        @Prop({default: 0})
        public gameWidth: number;
        @Prop({default: 0})
        public gameHeight: number;
        public boardItems: Item[] = Array(this.gameWidth * this.gameHeight).map(v => Item.empty());

        onMessage(message: Message) {

        }

    }
</script>

<style lang="css">
#game {
    width: 100%;
    height: 100vh;
}

#game .board {
    width: 50%
}


#game .board .row {
    display: flex;
    justify-content: space-around;
    line-height: 30px;
}

#game .board .row .slot {
    background: tomato;
    margin: 5px;
    flex: 1 0 auto;
    height: auto;
}

#game .board .row .slot:before {
    content: "";
    float: left;
    padding-top: 100%;
}
</style>
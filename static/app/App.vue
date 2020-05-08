<template lang="pug">
    div
        WaitingRoom(v-show="!inGame")
        Game(v-show="inGame")
</template>

<script lang="ts">
    import Vue from "vue";
    import WaitingRoom from "./pages/WaitingRoom.vue";
    import Game from "./pages/Game.vue";
    import Connection from "./Connection";

    export const EventBus = new Vue();

    Connection.init();

    export default {
        components: { WaitingRoom, Game },
        data() {
            return {
                inGame: false
            }
        },
        created() {
            EventBus.$on("gamestart", event => {
                console.log("gamestart: " + JSON.stringify(event));
                this.inGame = true;
            });
            EventBus.$on("gameend", event => {
                console.log("gameend: " + JSON.stringify(event));
                this.inGame = false;
            })
        }
    }
</script>

<style lang="sass">
    @import url('https://fonts.googleapis.com/css2?family=Roboto&display=swap')

    body
        margin: 0
        padding: 0
        font-family: "Roboto", sans-serif
</style>

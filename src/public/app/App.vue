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
            EventBus.$on("gamestart", () => {
                this.inGame = true;
            });
        }
    }
</script>

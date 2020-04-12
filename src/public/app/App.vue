<template lang="pug">
    div
        WaitingRoom(v-show="!inGame" v-on:gamestart="startGame")
        Game(v-show="inGame")
</template>

<script lang="ts">
    import WaitingRoom from "./pages/WaitingRoom.vue";
    import Game from "./pages/Game.vue";
    import Connection from "./Connection";

    Connection.init();

    export default {
        components: { WaitingRoom, Game },
        data() {
            return {
                inGame: false
            }
        },
        methods: {
            startGame: function() {
                console.log("Starting Game");
                this.inGame = true;
            }
        }
    }
</script>

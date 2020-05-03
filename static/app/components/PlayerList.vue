<template lang="pug">
    .player-list
        OtherPlayer(
            v-for="player in players"
            :player="player"
            :key="player.id"
            :startWidth="startWidth"
            :startHeight="startHeight")
</template>

<script lang="ts">

    import Vue from "vue";
    import Component from "vue-class-component";
    import {Prop} from "vue-property-decorator";
    import Player from "../model/Player";
    import OtherPlayer from "./OtherPlayer.vue";

    @Component({ components: {OtherPlayer} })
    export default class PlayerList extends Vue {

        @Prop({ default: [] })
        public players: Player[]

        public startWidth = 0;
        public startHeight = 0;

    }
</script>

<style lang="sass" scoped>
    .player-list
        display: flex
        flex-direction: column
        justify-content: stretch
</style>

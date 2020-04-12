<template lang="pug">
    div
        h1 Game
</template>

<script lang="ts">
    import {ConnectionListener, Message} from "../Connection";
    import Component from "vue-class-component";

    @Component
    export default class Game extends ConnectionListener {

        onMessage(message: Message) {

        }

    }
</script>

<style lang="sass">

</style>
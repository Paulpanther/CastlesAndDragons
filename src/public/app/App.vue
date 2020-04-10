<template lang="pug">
    WaitingRoom
</template>

<script lang="ts">
    import Vue from "vue"
    import WaitingRoom from "./pages/WaitingRoom.vue";
    import Connection from "./Connection";

    Connection.init();

    export default {
        components: { WaitingRoom }
    }
</script>

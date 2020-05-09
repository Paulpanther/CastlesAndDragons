<template lang="pug">
    .level-counter
        .marker(
            v-for="marker in parseInt(level)"
            :key="marker")
</template>

<script lang="ts">

    import Vue from "vue";
    import Component from "vue-class-component";
    import {Prop} from "vue-property-decorator";

    @Component
    export default class LevelCounter extends Vue {

        @Prop({ default: 0 })
        public level: number;

    }
</script>

<style lang="sass" scoped>
    .level-counter
        display: flex
        flex-direction: row
        justify-content: flex-end
        align-items: center

        .marker
            width: 1em
            height: 1em
            margin-left: 0.1em

            background: red
            border-radius: 50%
</style>

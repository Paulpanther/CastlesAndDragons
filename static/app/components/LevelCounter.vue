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

        .marker
            height: 100%
            flex: 1 0 auto
            background: red
            border-radius: 50%
</style>

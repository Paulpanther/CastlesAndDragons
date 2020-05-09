<template lang="pug">
    .message-display(:class="{active: active, useAnimation: useAnimation}" :style="{'--duration': animationDuration + 'ms'}")
        .wrapper
            span.message {{ text }}
</template>

<script lang="ts">
    import Vue from "vue";
    import Component from "vue-class-component";
    import Timeout = NodeJS.Timeout;

    @Component
    export default class MessageDisplay extends Vue {

        public text = ""
        public active = false;
        public timer: Timeout;

        public animationDuration = 0;
        public useAnimation = false;

        public update(text: string) {
            this.text = text;
        }

        public show(text: string, duration: number) {
            this.cancel();
            setTimeout(() => {
                this._show(text, duration);
            }, 10);
        }

        public cancel() {
            if (this.timer) {
                clearTimeout(this.timer);
            }
            this.animationDuration = 0
            this.active = false;
        }

        public showTimer(text: string, duration: number) {
            this.show(text, duration);
            this.animationDuration = duration;
            this.useAnimation = true;
        }

        private _show(text: string, duration: number) {
            this.text = text;
            this.active = true;

            this.timer = setTimeout(() => {
                this.cancel();
            }, duration);
        }
    }
</script>

<style lang="sass" scoped>
    .message-display
        position: relative

        .wrapper
            position: relative
            display: flex
            justify-content: center
            align-items: center

            width: 100%
            height: 100%

            z-index: 10
            top: 100%
            transition: top ease 1s

            background: rgba(200, 200, 200, 0.8)

            .message
                position: relative
                z-index: 20
                font-size: 2em

        &.active .wrapper
            top: 0

        &:before
            position: absolute
            z-index: 15
            left: 0
            bottom: 0
            display: block
            content: ""
            height: 5%
            width: 0

            background: #3d9970

            transition: width linear var(--duration)


        &.active:before
            width: 100%
</style>
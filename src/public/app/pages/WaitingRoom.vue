<template lang="pug">
    div
        h1 Waiting Room
        div(v-if="self !== null")
            h3 You:
            input(v-model="name")
            h3 Other Players:
            ul
                li(v-for="player in connectedPlayers") {{ player.name }}
            h4(v-if="isGameStarting") Game starts in: {{ countDown }}
            h4(v-else) ... waiting for more players ...
        h4(v-else) ... waiting for connection ...
</template>

<script lang="ts">
    import Component from "vue-class-component";
    import {Watch} from "vue-property-decorator";
    import * as _ from "lodash";
    import {ConnectionListener, Message} from "../Connection";
    import Player from "../model/Player";

    @Component
    export default class WaitingRoom extends ConnectionListener {

        public connectedPlayers: Player[] = [];
        public self: Player = null;
        public name = "";
        public waitingForNameChange = false;
        public gameStartTime: number;
        public isGameStarting = false;
        public countDown = 0;
        private completedHandshake = false;

        onMessage(message: Message) {
            this.callMethodForMessage(message);
        }

        private callMethodForMessage(message: Message) {
            switch (message.get("type")) {
                case "nameAndId": return this.onNameAndIdChange(message);
                case "setPlayers": return this.onSetPlayers(message);
                case "gameStartsIn": return this.onGameStartsIn(message);
                case "gameStartStopped": return this.onGameStartStopped(message);
                case "gameStart": return this.onGameStarts(message);
                case "error": return this.callMethodForError(message);
            }
        }

        private callMethodForError(message: Message) {
            switch (message.get("error")) {
                case "invalidName": return this.onInvalidName(message);
            }
        }

        @Watch("name")
        private onSelfNameChange(newName: string, oldName: string) {
            if (this.completedHandshake) {
                if (newName !== self.name) {
                    this.waitingForNameChange = true;
                    this.sendNameChange(newName);
                }
            }
            this.completedHandshake = true;
        }

        private sendNameChange = _.debounce((newName: string) => {
            this.send(Message.newName(newName));
        }, 500);

        private onNameAndIdChange(message: Message) {
            this.self = Player.parse(message.get("client"));
            this.name = this.self.name;
            this.waitingForNameChange = false;
        }

        private onSetPlayers(message: Message) {
            const names = message.getMultiple("names");
            const ids = message.getMultiple("ids");
            const all = names.map((v, i) => new Player(parseInt(ids[i]), v));
            this.connectedPlayers = all.filter(player => player.id !== this.self.id);
        }

        private onInvalidName(message: Message) {
            this.name = this.self.name;
            this.waitingForNameChange = false;
            console.log("Error: Invalid name");
        }

        private onGameStartsIn(message: Message) {
            const delay = parseInt(message.get("delay"));
            this.gameStartTime = Date.now() + delay;
            this.isGameStarting = true;
            this.tick();
        }

        private onGameStartStopped(message: Message) {
            this.isGameStarting = false;
        }

        private onGameStarts(message: Message) {
            this.$emit("gamestart");
        }

        private tick() {
            if (this.isGameStarting && this.gameStartTime > Date.now()) {
                setTimeout(this.tick, 1000);
                this.countDown = Math.floor((this.gameStartTime - Date.now()) / 1000);
            }
        }
    }
</script>

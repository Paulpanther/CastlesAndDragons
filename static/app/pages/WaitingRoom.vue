<template lang="pug">
    div
        h1(v-show="!roomIdFromServer") Waiting Room
        h1(v-show="roomIdFromServer") Waiting Room \#{{ roomId }}
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
    import Vue from "vue";
    import Component from "vue-class-component";
    import {Watch} from "vue-property-decorator";
    import * as _ from "lodash";
    import Connection, {ConnectionListener, Message} from "../Connection";
    import Player from "../model/Player";
    import {EventBus} from "../App.vue";
    import {TickingTimer} from "../components/util";

    Vue.prototype.$location = window.location.hash;
    console.log(Vue.prototype.$location);

    @Component
    export default class WaitingRoom extends ConnectionListener {

        public connectedPlayers: Player[] = [];
        public self: Player = null;
        public name = "";
        public waitingForNameChange = false;

        public isGameStarting = false;
        public countDown = 0;

        public roomId: string = "";
        public roomIdFromServer = false;

        private completedHandshake = false;
        private gameStartTimer = new TickingTimer();

        public mounted() {
            this.startListening();
            EventBus.$on("gameend", (event) => {
                this.self = event.self;
                this.connectedPlayers = event.others;
            });

            if (Connection.open) {
                this.onOpen();
            }
        }

        public onOpen() {
            this.roomId = this.$location.slice(1) || undefined;
            this.send(Message.enterRoom(this.roomId));
        }

        public onMessage(message: Message) {
            this.callMethodForMessage(message);
        }

        private callMethodForMessage(message: Message) {
            switch (message.get("type")) {
                case "joinedWaiting": return this.setRoomId(message);
                case "nameAndId": return this.onNameAndIdChange(message);
                case "setPlayers": return this.onSetPlayers(message);
                case "gameStartsIn": return this.onGameStartsIn(message);
                case "gameStartStopped": return this.onGameStartStopped(message);
                case "gameStart": return this.onGameStarts(message);
                case "error": return this.callMethodForError(message);
            }
        }

        private setRoomId(message: Message) {
            this.roomIdFromServer = true;
            this.roomId = message.get("id");
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
            this.gameStartTimer.start(delay, 1000, timer => {
                this.countDown = timer.count;
            });
            this.isGameStarting = true;
        }

        private onGameStartStopped(message: Message) {
            this.gameStartTimer.cancel();
            this.isGameStarting = false;
        }

        private onGameStarts(message: Message) {
            this.gameStartTimer.cancel();
            this.isGameStarting = false;
            const size = message.getMultiple("size");
            EventBus.$emit("gamestart", {
                gameWidth: size[0],
                gameHeight: size[1],
                gameDelay: message.get("delay"),
                self: this.self,
                others: this.connectedPlayers
            });
            this.stopListening();
        }
    }
</script>

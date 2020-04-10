<template lang="pug">
    div
        h1 Waiting Room
        h3 You:
        div(v-if="self !== null")
            input(v-model="name")
        h3 Other Players:
        ul
            li(v-for="player in connectedPlayers") {{ player.name }}
</template>

<script lang="ts">
    import Vue from "vue";
    import Component from "vue-class-component";
    import {Watch} from "vue-property-decorator";
    import * as _ from "lodash";
    import {ConnectionListener, Message} from "../Connection";
    import Player from "../model/Player";

    @Component
    export default class WaitingRoom extends ConnectionListener {

        public connectedPlayers: Player[] = [];
        public self: Player = null;
        public name: string = "";
        public waitingForNameChange = false;

        onMessage(message: Message) {
            if (message.get("type") === "joined") {
                this.onJoined(Player.parse(message.get("client")));
            }
            if (message.get("type") === "nameAndId") {
                this.onSelfUpdate(Player.parse(message.get("client")));
            }
            if (message.get("type") === "error") {
                if (message.get("error") === "invalidName") {
                    this.name = this.self.name;
                    this.waitingForNameChange = false;
                    console.log("Error: Invalid name");
                }
            }
        }

        @Watch("name")
        private onSelfNameChange(newName: string, oldName: string) {
            if (newName !== self.name) {
                this.waitingForNameChange = true;
                this.sendNameChange(newName);
            }
        }

        private sendNameChange = _.debounce((newName: string) => {
            this.send(Message.newName(newName));
        }, 500);

        private onJoined(player: Player) {
            this.connectedPlayers.push(player);
        }

        private onSelfUpdate(self: Player) {
            this.self = self;
            this.name = self.name;
            this.waitingForNameChange = false;
            console.log("Name set to: " + this.name);
        }
    }
</script>

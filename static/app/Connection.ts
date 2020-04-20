import Vue from "vue";
import Component from "vue-class-component";
import Pos from "./model/Pos";
import Item from "./model/Item";


export default class Connection {

    private static listeners: ConnectionListener[] = [];

    private static connection: WebSocket;

    public static init() {
        const server = process.env.SERVER || "localhost:6789";
        this.connection = new WebSocket("ws://" + server);

        this.connection.onmessage = (event) => {
            console.log(event.data);
            this.listeners.forEach(l => l.onMessage(Message.parse(event.data)));
        }
    }

    public static send(message: string) {
        this.connection.send(message);
    }

    public static addListener(listener: ConnectionListener) {
        this.listeners.push(listener);
    }

    public static removeListener(listener: ConnectionListener) {
        this.listeners.splice(this.listeners.indexOf(listener), 1);
    }
}

@Component
export abstract class ConnectionListener extends Vue {

    created() {
        Connection.addListener(this);
    }

    abstract onMessage(message: Message);

    protected send(message: string) {
        Connection.send(message);
        console.log("Send: " + message);
    }

    protected stopListening() {
        Connection.removeListener(this);
    }
}

export class Message {

    public static parse(str: String): Message {
        const args = str.split(";").map(s => s.split("="));
        return new Message(args);
    }

    constructor(private readonly args: string[][]) {}

    public get(key: string): string {
        return this.args.find(arg => arg[0] === key)[1]
    }

    public getMultiple(key: string): string[] {
        return this.get(key).split(",");
    }

    // #### SEND ####

    public static newName(newName: string) { return Message.join(this.type("name"), this.v("name", newName)) }

    public static removeItem(pos: Pos, item: Item) {
        return Message.join(
            this.type("move"),
            this.v("from", pos.x, pos.y),
            this.v("item", item.type.toString(16)))
    }

    public static addItem(pos: Pos, item: Item) {
        return Message.join(
            this.type("move"),
            this.v("to", pos.x, pos.y),
            this.v("up", item.up),
            this.v("item", item.type.toString(16)));
    }

    public static finished() { return this.type("finished") }

    private static type(type: string) { return Message.v("type", type) }
    private static v(key: string, ... value: Array<string | number>) { return `${key}=${value.join(",")}` }
    private static join(...parts: string[]) { return parts.join(";") }
}
import Vue from "vue";
import Component from "vue-class-component";


export default class Connection {

    private static listeners: ConnectionListener[] = [];

    private static connection: WebSocket;

    public static init() {
        this.connection = new WebSocket("ws://localhost:6789");

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

    public static newName(newName: string) { return Message.join(this.type("name"), this.v("name", newName)) }

    private static type(type: string) { return Message.v("type", type) }
    private static v(key: string, value: string) { return `${key}=${value}` }
    private static join(...parts: string[]) { return parts.join(";") }
}
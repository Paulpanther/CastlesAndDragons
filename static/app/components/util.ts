import Item from "../model/Item";
import Connection from "../Connection";

export function classForItem(item: Item): string {
    const rotClass = `rot-${item.up}`;
    const typeClass = `item-${item.type}`;
    return `${rotClass} ${typeClass}`;
}

export class TickingTimer {

    private _running = false;
    private _count = 0;

    private endTime: number;
    private tickRate: number;
    private tickFunction: (TickingTimer) => void;

    public start(duration: number, tickRate = 1000, tickFunction: (timer: TickingTimer) => void = () => {}) {
        this.endTime = Date.now() + duration;
        this._running = true;
        this.tickRate = tickRate;
        this.tickFunction = tickFunction;
        this.tick();
    }

    public isRunning() {
        return this._running;
    }

    public cancel() {
        this._running = false;
        this._count = 0;
    }

    get running() {
        return this._running;
    }

    get count() {
        return this._count;
    }

    private tick() {
        if (this._running && this.endTime > Date.now()) {
            setTimeout(() => this.tick(), this.tickRate);
            this._count = Math.floor((this.endTime - Date.now()) / 1000);
            this.tickFunction(this);
        }
    }
}
import Item from "../model/Item";

export function classForItem(item: Item): string {
    const rotClass = `rot-${item.up}`;
    const typeClass = `item-${item.type}`;
    return `${rotClass} ${typeClass}`;
}

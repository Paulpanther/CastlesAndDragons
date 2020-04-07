
const connection = new WebSocket("ws://localhost:6789");

document.getElementById("h1").innerHTML = "TS";

connection.onopen = () => {
    console.log("Open");
    connection.send("Hello");
    document.getElementById("h1").innerHTML = "Open";
};

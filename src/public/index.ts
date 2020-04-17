import Vue from "vue";
import App from "./app/App.vue";
import Connection from "./app/Connection";

new Vue({ render: createElement => createElement(App) }).$mount("#app");

const btn = document.getElementById("restart");
btn.addEventListener("click", () => {
    console.log("Restart");
    Connection.send("type=restart");
});

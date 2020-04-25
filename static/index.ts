import Vue from "vue";
import App from "./app/App.vue";
import Connection from "./app/Connection";

new Vue({
    el: "#app",
    template: "<App/>",
    components: {App},
})

const btn = document.getElementById("restart");
btn.addEventListener("click", () => {
    console.log("Restart");
    Connection.send("type=restart");
});

import Vue from "vue";
import App from "./app/App.vue";

new Vue({ render: createElement => createElement(App) }).$mount("#app");
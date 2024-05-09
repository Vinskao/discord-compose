// src/main.js

import { createApp } from "vue";
import ElementPlus from "element-plus";
import "./style.css";
import App from "./App.vue";
import axios from "axios";
import router from "./router/index.js";
import "bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootswatch/dist/darkly/bootstrap.min.css";
import "element-plus/theme-chalk/dark/css-vars.css";
import { store } from "./store";

if (typeof window !== "undefined") {
  window.global = window;
}
const app = createApp(App);

window.$vueApp = app;

app.use(ElementPlus, { locale: "zh-TW" }).use(router).use(store).mount("#app");

axios.interceptors.request.use((request) => {
  // 從 sessionStorage 獲取 token
  const token = sessionStorage.getItem("authToken");
  if (token) {
    request.headers["Authorization"] = `Bearer ${token}`;
  }
  console.log("Request Headers:", request.headers);
  return request;
});

axios.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.config && error.config.url.includes("/export-chat-history")) {
      return Promise.reject(error);
    }

    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403)
    ) {
      if (window.location.pathname !== "/login") {
        window.location.href =
          "/login?redirect=" +
          encodeURIComponent(window.location.pathname + window.location.search);
      }
    }

    return Promise.reject(error);
  }
);

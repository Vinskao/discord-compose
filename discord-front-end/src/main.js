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

axios.interceptors.response.use(
  (response) => {
    // 正常響應直接返回
    return response;
  },
  (error) => {
    // 如果錯誤來自 /export-chat-history，保持原有處理
    if (error.config && error.config.url.includes("/export-chat-history")) {
      return Promise.reject(error);
    }

    // 針對 401 和 403 錯誤，如果當前不在登錄頁面，則重定向到登錄頁面
    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403)
    ) {
      //  檢查當前頁面是否已經是登錄頁，避免重定向循環
      if (window.location.pathname !== "/login") {
        // 附帶當前頁面路徑，便於登錄後跳轉回來
        window.location.href =
          "/login?redirect=" +
          encodeURIComponent(window.location.pathname + window.location.search);
      }
    }
    return Promise.reject(error);
  }
);

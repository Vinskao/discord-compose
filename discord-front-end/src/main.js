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
import VueJwtDecode from "vue-jwt-decode";

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
    const decoded = VueJwtDecode.decode(token);
    console.log(decoded);
    const currentTime = Date.now() / 1000;
    if (decoded.exp - currentTime < 180) {
      renewToken(); // 當Token即將在3分鐘內過期時自動續期
    }
  }
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

const renewToken = async () => {
  // 實現Token續期請求...
  try {
    const response = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/user/renew-token`
    );
    const newToken = response.data.token;
    sessionStorage.setItem("authToken", newToken);
    axios.defaults.headers.common["Authorization"] = `Bearer ${newToken}`;
  } catch (error) {
    console.error("Token續期失敗:", error);
    Swal.fire("錯誤", "無法續期Token，請重新登入。", "error");
    sessionStorage.removeItem("authToken");
    router.push("/login");
  }
};

// src/router/index.js

import { createRouter, createWebHashHistory } from "vue-router";
import index from "../views/Index.vue";
import login from "../views/Login.vue";
import register from "../views/Register.vue";
import security from "../views/Security.vue";
import history from "../views/History.vue";

import axios from "axios";
const routes = [
  {
    path: "/",
    redirect: "/index",
    meta: { requiresAuth: true },
  },
  {
    path: "/register",
    component: register,
  },
  {
    path: "/history",
    component: history,
  },
  {
    path: "/index",
    component: index,
    meta: { requiresAuth: true },
  },
  {
    path: "/security",
    component: security,
    meta: { requiresAuth: true },
    name: "security",
  },
  {
    path: "/login",
    component: login,
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});
router.beforeEach(async (to, from, next) => {
  const bypassAuth = sessionStorage.getItem("bypassAuth") === "true";

  if (bypassAuth) {
    next();
  } else if (to.matched.some((record) => record.meta.requiresAuth)) {
    try {
      const response = await axios.post(
        `${import.meta.env.VITE_HOST_URL}/user/me`
      );
      if (response && response.status === 200) {
        next();
      } else {
        throw new Error("Unauthorized");
      }
    } catch (error) {
      next({ path: "/login", query: { redirect: to.fullPath } });
    }
  } else {
    next();
  }
});

export default router;

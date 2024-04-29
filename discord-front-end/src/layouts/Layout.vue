<!-- src/layouts/Layouts.vue -->

<script setup>
import { useRouter } from "vue-router";
import axios from "axios";
import Swal from "sweetalert2";
import { useStore } from "vuex";
import { computed, ref, onMounted } from "vue";

axios.defaults.withCredentials = true;

const isInRoom = computed(() => store.state.isInRoom);
const router = useRouter();
const store = useStore();
const userLoggedIn = ref(false);

onMounted(() => {
  checkUserAuth();
});

const checkUserAuth = async () => {
  try {
    await axios.post(`${import.meta.env.VITE_HOST_URL}/user/me`);
    userLoggedIn.value = true;
  } catch (error) {
    if (error.response && error.response.status === 401) {
      userLoggedIn.value = false;
    } else {
      console.error("Error fetching user details:", error);
    }
  }
};
const logout = async () => {
  store.dispatch("disconnectStomp");
  const username = JSON.parse(localStorage.getItem("userInfo")).username;
  try {
    await axios.post(
      `${import.meta.env.VITE_HOST_URL}/user-to-room/delete-all-by-username`,
      { username }
    );
    await axios.post(
      `${import.meta.env.VITE_HOST_URL}/user-to-group/delete-all-by-username`,
      { username }
    );
  } catch (error) {
    console.error("中介表中已經沒有此使用者:", error);
  }

  // 执行登出逻辑
  try {
    await axios.post(`${import.meta.env.VITE_HOST_URL}/user/logout`);
    await Swal.fire({
      icon: "success",
      title: "您已登出",
      showConfirmButton: false,
      timer: 1500,
    });

    router.push("/login");

    localStorage.removeItem("userInfo");
    sessionStorage.removeItem("bypassAuth");
  } catch (error) {
    console.error("登出時出現錯誤:", error);
    Swal.fire({
      icon: "error",
      title: "Oops...",
      text: "請重試",
    });
  }
};
</script>
<style scoped>
.transparent-navbar {
  background-color: rgba(0, 0, 0, 0);
}

.horizontal-navbar {
  display: flex;
  flex-direction: row;
  align-items: center;
}

.nav-item.nav-link {
  margin-right: 20px;
}

.logout-button {
  background-color: transparent;
  border: none;
  color: grey;
  transition: color 0.2s ease-in-out, font-weight 0.2s ease-in-out;
}

.logout-button:hover {
  color: red;
  font-weight: bold;
}
</style>

<template>
  <nav class="navbar navbar-dark fixed-top transparent-navbar">
    <div class="container-fluid">
      <div class="navbar-nav horizontal-navbar">
        <router-link class="nav-item nav-link" to="/index">聊天</router-link>
        <router-link
          v-if="!userLoggedIn"
          class="nav-item nav-link"
          to="/register"
          >註冊</router-link
        >
        <router-link v-if="!userLoggedIn" class="nav-item nav-link" to="/login"
          >登入</router-link
        >
        <router-link
          v-if="!isInRoom && userLoggedIn"
          class="nav-item nav-link"
          to="/security"
          >安全</router-link
        >
        <button
          v-if="!isInRoom && userLoggedIn"
          class="nav-item nav-link logout-button"
          @click="logout"
        >
          登出
        </button>
      </div>
    </div>
  </nav>
</template>

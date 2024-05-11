<!-- src/views/login.vue -->
<template>
  <Layout />

  <div class="login-container blue-text">
    <h2>登入</h2>
    <form @submit.prevent="login">
      <div class="mb-3">
        <label for="username" class="form-label">Username</label>
        <input
          type="text"
          class="form-control"
          id="id"
          v-model="username"
          required
        />
      </div>
      <div class="mb-3">
        <label for="password" class="form-label">Password</label>
        <input
          type="password"
          class="form-control"
          id="password"
          v-model="password"
          required
        />
      </div>
      <button type="submit" class="btn blue-button">登入</button>
    </form>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  </div>

  <button @click="toggleForgotPassword" class="blue-button">忘記密碼？</button>

  <div v-if="showForgotPassword" class="blue-text">
    <input
      v-if="!showSecurityQuestion"
      type="text"
      v-model="forgotUsername"
      placeholder="請輸入您的用戶名"
    />
    <button
      v-if="!showSecurityQuestion"
      @click="fetchSecurityQuestion"
      class="blue-button"
    >
      提交
    </button>

    <div v-if="showSecurityQuestion">
      <p>{{ securityQuestion }}</p>
      <input type="text" v-model="securityAnswer" placeholder="請輸入答案" />
      <button @click="verifySecurityAnswer" class="blue-button">
        驗證答案
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import Layout from "../layouts/Layout.vue";
import axios from "axios";
import { useRouter } from "vue-router";
import Swal from "sweetalert2";
import VueJwtDecode from "vue-jwt-decode";

axios.defaults.withCredentials = true;
const userLoggedIn = ref(false);
const username = ref("");
const password = ref("");
const errorMessage = ref("");
const router = useRouter();

// 忘記密碼
const forgotUsername = ref("");
const securityQuestion = ref("");
const securityAnswer = ref("");
const showForgotPassword = ref(false);
const showSecurityQuestion = ref(false);

const toggleForgotPassword = () => {
  showForgotPassword.value = !showForgotPassword.value;
};

const fetchSecurityQuestion = async () => {
  try {
    const response = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/get-question`,
      { username: forgotUsername.value }
    );
    securityQuestion.value = response.data;
    showSecurityQuestion.value = true;
  } catch (error) {
    console.error("Error fetching security question:", error);
    // 現在顯示後端返回的具體錯誤訊息
    const errorMessage =
      error.response && error.response.data
        ? error.response.data
        : "請先登入後設定安全問題";
    await Swal.fire("錯誤", errorMessage, "error");
  }
};

const verifySecurityAnswer = async () => {
  try {
    const response = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/verify-answer`,
      {
        username: forgotUsername.value,
        question: securityQuestion.value,
        answer: securityAnswer.value,
      }
    );
    if (response.data) {
      sessionStorage.setItem("bypassAuth", "true");
      router.push({
        name: "security",
        state: { resetPassword: true, username: forgotUsername.value },
        query: { bypassAuth: "true" },
      });
    } else {
      await Swal.fire("錯誤", "答案不正確。", "error");
    }
  } catch (error) {
    console.error("Error verifying security answer:", error);
    await Swal.fire("錯誤", "驗證答案時出現錯誤，請重試。", "error");
  }
};

const login = async () => {
  try {
    const userData = {
      username: username.value,
      password: password.value,
    };
    const loginResponse = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/user/login`,
      JSON.stringify(userData),
      { headers: { "Content-Type": "application/json" } }
    );

    if (loginResponse.status === 200) {
      const { token } = loginResponse.data;
      sessionStorage.setItem("authToken", token);
      const decoded = VueJwtDecode.decode(token);
      console.log(decoded);

      const currentTime = Date.now() / 1000; // 獲取當前時間的秒數
      const timeLeft = decoded.exp - currentTime; // 計算剩餘時間
      setTimeout(() => {
        Swal.fire({
          title: "即將過期",
          text: "您的登錄即將過期，是否要更新？",
          icon: "warning",
          showCancelButton: true,
          confirmButtonText: "更新",
          cancelButtonText: "取消",
        }).then((result) => {
          if (result.isConfirmed) {
            renewToken(); // 實現此函數以續期token
          }
        });
      }, (timeLeft - 180) * 1000); // 在過期前3分鐘提醒

      axios.defaults.headers.common[
        "Authorization"
      ] = `Bearer ${loginResponse.data.token}`;
      if (!axios.defaults.headers.common["Authorization"]) {
        console.error("Authorization token is missing");
      } else {
        console.log(
          "Authorization header set:",
          axios.defaults.headers.common["Authorization"]
        );
      }
      const userInfoResponse = await axios.post(
        `${import.meta.env.VITE_HOST_URL}/user/me`
      );
      localStorage.setItem("userInfo", JSON.stringify(userInfoResponse.data));
      router.push("/index");
      console.log("現在的login username是" + username.value);

      try {
        const response = await axios.post(
          `${
            import.meta.env.VITE_HOST_URL
          }/user-to-room/delete-all-by-username`,
          JSON.stringify({ username: username.value }),
          { headers: { "Content-Type": "application/json" } }
        );
        if (response.status === 200 && response.data) {
          // 利用回應中的訊息向用戶提供適當的反饋
          console.log(response.data); // 打印後端的回應訊息，以便於調試和確認
        }
      } catch (error) {
        console.error("刪除該使用者的房間項目失敗:", error);
        errorMessage.value =
          error.response && error.response.data
            ? error.response.data
            : "刪除房間項目時出現問題";
      }

      try {
        const response = await axios.post(
          `${
            import.meta.env.VITE_HOST_URL
          }/user-to-group/delete-all-by-username`,
          JSON.stringify({ username: username.value }),
          { headers: { "Content-Type": "application/json" } }
        );
        if (response.status === 200 && response.data) {
          // 檢查後端是否返回了特定的訊息
          console.log(response.data); // 打印後端回應的訊息，便於確認是成功刪除還是無需操作
        }
      } catch (error) {
        console.error("Failed to delete user groups:", error);
      }
    } else {
      errorMessage.value = "Invalid username or password";
    }
  } catch (error) {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("userInfo");
      router.push("/login");
      errorMessage.value = "Invalid username or password";
    } else {
      errorMessage.value = error.response
        ? error.response.data
        : "An error occurred while processing your request";
    }
  }
};
</script>

<style scoped>
@import url("../style.css");

.login-container {
  max-width: 400px;
  margin: auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.error-message {
  color: red;
  margin-top: 10px;
}

.blue-text {
  color: #82a6cb;
}

.blue-button {
  background-color: #007bff;
  color: white;
  border: none;
}

.blue-button:hover {
  background-color: #0056b3;
}
</style>

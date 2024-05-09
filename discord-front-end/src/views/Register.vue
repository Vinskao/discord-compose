<!-- src/views/Register.vue -->

<template>
  <Layout />

  <div class="register-container">
    <h1>註冊</h1>
    <form @submit.prevent="register">
      <div class="form-group">
        <label for="username">Username</label>
        <input v-model="username" type="text" id="username" required />
      </div>

      <div class="form-group">
        <label for="password">Password</label>
        <input v-model="password" type="password" id="password" required />
      </div>
      <!-- 生日输入框 -->
      <div class="form-group">
        <label for="birthday">Birthday</label>
        <input
          v-model="birthday"
          type="date"
          id="birthday"
          :max="maxDate"
          required
        />
      </div>

      <!-- 兴趣输入框 -->
      <div class="form-group">
        <label for="interests">Interests</label>
        <input v-model="interests" type="text" id="interests" required />
      </div>
      <!-- 在註冊表單中新增選擇安全問題的部分 -->
      <div class="form-group">
        <label for="security-question">安全問題</label>
        <select v-model="selectedQuestion" id="security-question" required>
          <option disabled value="">請選擇一個問題</option>
          <option>你的媽媽叫什麼名子?</option>
          <option>你的狗狗叫什麼名子?</option>
          <option>你的國小叫什麼名子?</option>
        </select>
      </div>
      <div class="form-group">
        <label for="security-answer">答案</label>
        <input
          type="text"
          id="security-answer"
          v-model="securityAnswer"
          required
        />
      </div>
      <button type="submit">註冊</button>
    </form>
  </div>
</template>

<script setup>
import axios from "axios";
import Swal from "sweetalert2";
import Layout from "../layouts/Layout.vue";
import { ref, computed } from "vue";
import { useRouter } from "vue-router";

axios.defaults.withCredentials = true;

const username = ref("");
const password = ref("");
const birthday = ref("");
const interests = ref("");
const selectedQuestion = ref("");
const securityAnswer = ref("");
const router = useRouter();

const maxDate = computed(() => {
  const today = new Date();
  const yyyy = today.getFullYear();
  let mm = today.getMonth() + 1; // Months start at 0
  let dd = today.getDate();

  mm = mm < 10 ? "0" + mm : mm;
  dd = dd < 10 ? "0" + dd : dd;

  return `${yyyy}-${mm}-${dd}`;
});
const register = async () => {
  const currentDateTime = new Date();
  const selectedBirthday = new Date(birthday.value + "T00:00:00");
  if (selectedBirthday > currentDateTime) {
    await Swal.fire("失敗", "生日不能超過今天", "error");
    return;
  }

  try {
    const registrationResponse = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/user/register`,
      {
        username: username.value,
        password: password.value,
        birthday: birthday.value + "T00:00:00",
        interests: interests.value,
      }
    );

    if (registrationResponse.status === 200) {
      // 注册成功，继续添加安全问题
      const securityQuestionResponse = await addSecurityQuestion();

      if (securityQuestionResponse.status === 200) {
        await Swal.fire("成功", "用戶成功註冊且安全問題已添加", "success");
        router.push("/login");
      } else {
        throw new Error("安全問題添加失敗");
      }
    } else {
      throw new Error("註冊失敗");
    }
  } catch (error) {
    let message = error.message;
    if (error.response && error.response.data) {
      message =
        error.response.data.message || JSON.stringify(error.response.data);
    }
    await Swal.fire("操作失敗", message, "error");
  }
};

const addSecurityQuestion = async () => {
  return axios.post(`${import.meta.env.VITE_HOST_URL}/add-security-question`, {
    username: username.value,
    question: selectedQuestion.value,
    answer: securityAnswer.value,
  });
};
</script>

<style>
.register-container {
  max-width: 300px;
  margin: auto;
  padding: 20px;
}

.form-group {
  margin-bottom: 10px;
}

.form-group label {
  display: block;
}

.form-group input {
  width: 100%;
  padding: 8px;
  margin-top: 4px;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>

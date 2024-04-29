<!-- src/views/Security.vue -->

<template>
  <Layout />

  <div class="normal-info">
    <h1 @click="toggleSection('normalInfo')">一般資料</h1>
    <div v-if="sections.normalInfo" class="content">
      <div class="info-item">用户: {{ userInfo.username }}</div>
      <div class="info-item">權限: {{ userInfo.authority }}</div>
      <div class="info-item" v-if="!editMode">
        生日: {{ formatDate(userInfo.birthday) }}
      </div>
      <div class="info-item" v-if="!editMode">
        興趣: {{ userInfo.interests }}
      </div>

      <div v-if="editMode" class="edit-section">
        <label for="birthday" class="edit-label">生日:</label>
        <input
          id="birthday"
          type="date"
          v-model="editableBirthday"
          class="edit-input"
        />
        <br />
        <label for="interests" class="edit-label">興趣:</label>
        <input
          id="interests"
          type="text"
          v-model="editableInterests"
          class="edit-input"
        />
      </div>
      <div class="buttons">
        <button v-if="!editMode" @click="toggleEditMode">修改</button>
        <button v-else @click="toggleEditMode" class="cancel-btn">取消</button>
        <button v-if="editMode" @click="saveUserInfo">儲存</button>
      </div>
    </div>
  </div>

  <div class="security-question">
    <h1 @click="toggleSection('securityQuestion')">安全問題</h1>
    <div v-if="sections.securityQuestion" class="content">
      <div class="form-section">
        <h3>新增安全問題</h3>
        <form @submit.prevent="addQuestion">
          <select v-model="selectedQuestionAdd">
            <option disabled value="">請選擇一個問題</option>
            <option>你的媽媽叫什麼名子?</option>
            <option>你的狗狗叫什麼名子?</option>
            <option>你的國小叫什麼名子?</option>
          </select>
          <input type="text" v-model="answerAdd" placeholder="您的答案" />
          <button type="submit">新增問題</button>
        </form>
      </div>
      <br />
      <div class="form-section">
        <h3>修改安全問題</h3>
        <form @submit.prevent="modifyQuestion">
          <select v-model="selectedQuestionModify">
            <option disabled value="">請選擇一個新問題</option>
            <option>你的媽媽叫什麼名子?</option>
            <option>你的狗狗叫什麼名子?</option>
            <option>你的國小叫什麼名子?</option>
          </select>
          <input type="text" v-model="answerModify" placeholder="您的新答案" />
          <button type="submit">提交修改</button>
        </form>
      </div>
    </div>
  </div>

  <div class="modify-info">
    <h1 @click="toggleSection('modifyInfo')">機密資料</h1>
    <div v-if="sections.modifyInfo" class="content">
      <div class="form-section">
        <h3>修改密碼</h3>
        <form @submit.prevent="changePassword">
          <input
            type="password"
            v-model="newPassword"
            placeholder="新密碼"
            pattern="^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!@#$%^&*()_+]{8,}$"
            title="密碼必須包含英文字母和數字，並至少8個字符長度。"
            required
          />
          <button type="submit">更改密碼</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from "vue";
import axios from "axios";
import Swal from "sweetalert2";
import Layout from "../layouts/Layout.vue";
import { useRoute } from "vue-router";
import router from "../router/index.js";

axios.defaults.withCredentials = true;

const selectedQuestionAdd = ref("");
const answerAdd = ref("");
const selectedQuestionModify = ref("");
const answerModify = ref("");
const newPassword = ref("");
const route = useRoute();
const resetPassword = ref(false);

// 可编辑的数据
const editableBirthday = ref("");
const editableInterests = ref("");
// 编辑模式标志
const editMode = ref(false);

const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const day = date.getDate().toString().padStart(2, "0");
  return `${year}-${month}-${day}`; // 返回格式化的日期
};

const toggleEditMode = () => {
  editMode.value = !editMode.value;
  if (editMode.value) {
    editableBirthday.value = formatDate(userInfo.value.birthday); // 使用formatDate确保正确的格式
    editableInterests.value = userInfo.value.interests;
  }
};

const saveUserInfo = async () => {
  const today = new Date().toISOString().split("T")[0];
  if (editableBirthday.value > today) {
    await Swal.fire("錯誤", "生日不能超過今天的日期", "error");
    return; // 提前终止函数执行
  }

  try {
    await axios.post(
      `${import.meta.env.VITE_HOST_URL}/user/update-user-details`,
      {
        username: userInfo.value.username,
        birthday: new Date(editableBirthday.value).toISOString(),
        interests: editableInterests.value,
      }
    );
    await Swal.fire("成功", "資訊已更新", "success");
    // 更新成功后，重新获取用户信息以刷新显示
    await getUserInfo();
    editMode.value = false; // 关闭编辑模式
  } catch (error) {
    console.error("更新資訊時出錯:", error);
    await Swal.fire("錯誤", "更新資訊失敗，請稍後再試。", "error");
  }
};

const userInfo = ref({});
const getUserInfo = async () => {
  const localUserInfo = JSON.parse(localStorage.getItem("userInfo"));
  if (!localUserInfo || !localUserInfo.username) {
    await Swal.fire("錯誤", "未找到用戶信息。請登錄。", "error");
    router.push("/login");
    return;
  }

  try {
    const response = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/user/find-by-username`,
      { username: localUserInfo.username }
    );
    userInfo.value = response.data; // 保存从 API 获取的用户信息
  } catch (error) {
    console.error("獲取用户信息失敗:", error);
    await Swal.fire("錯誤", "獲取用户信息失敗。", "error");
  }
};
const sections = reactive({
  normalInfo: false,
  securityQuestion: false,
  modifyInfo: false,
});

onMounted(async () => {
  if (!sessionStorage.getItem("bypassAuth")) {
    await getUserInfo();
  }
  let userInfoResponse;
  try {
    if (!sessionStorage.getItem("bypassAuth")) {
      userInfoResponse = await axios.post(
        `${import.meta.env.VITE_HOST_URL}/user/me`
      );
      console.log("User info:", userInfoResponse.data);

      if (
        !userInfoResponse.data ||
        Object.keys(userInfoResponse.data).length === 0
      ) {
        throw new Error("No user info returned");
      }
    }
  } catch (error) {
    console.error("用戶未登錄或session已過期:", error);
    router.push("/login");
  }

  if (route.fullPath && history.state && history.state.resetPassword) {
    toggleSection("modifyInfo");
  } else {
    sections.modifyInfo = false;
  }
});

const toggleSection = (section) => {
  // Only toggle the 'modifyInfo' section if the resetPassword state is true
  if (
    section === "modifyInfo" &&
    !(route.fullPath && history.state && history.state.resetPassword)
  ) {
    // Unauthorized attempt to open the modifyInfo section
    Swal.fire({
      icon: "error",
      title: "無法訪問",
      text: "請前往登入頁面使用忘記密碼以進入進行密碼修改",
    });
    return; // Prevent the section from toggling
  }
  sections[section] = !sections[section];
};

const addQuestion = async () => {
  if (!userInfo.value || !userInfo.value.username) {
    await Swal.fire(
      "錯誤",
      "未找到用戶信息或用戶未登錄。請登錄後再試。",
      "error"
    );
    router.push("/login");
    return;
  }

  const postData = {
    username: userInfo.value.username,
    question: selectedQuestionAdd.value,
    answer: answerAdd.value,
  };

  try {
    await axios.post(
      `${import.meta.env.VITE_HOST_URL}/add-security-question`,
      postData
    );
    await Swal.fire("成功", "安全問題已成功添加", "success");
  } catch (error) {
    console.error("新增安全問題時出錯:", error);
    await Swal.fire("錯誤", "你已新增過安全問題。", "error");
  }
};

const modifyQuestion = async () => {
  const localUserInfo = JSON.parse(localStorage.getItem("userInfo"));

  if (!localUserInfo || !localUserInfo.username) {
    await Swal.fire("錯誤", "未找到用戶信息。請登錄。", "error");
    router.push("/login");
    return;
  }

  const postData = {
    username: localUserInfo.username,
    question: selectedQuestionModify.value,
    answer: answerModify.value,
  };

  try {
    await axios.post(
      `${import.meta.env.VITE_HOST_URL}/modify-security-question`,
      postData
    );
    await Swal.fire("成功", "安全問題已成功修改", "success");
  } catch (error) {
    console.error("修改安全問題時出錯:", error);
    await Swal.fire("錯誤", "修改安全問題失敗，請稍後再試。", "error");
  }
};

const changePassword = async () => {
  const usernameForPasswordChange =
    history.state?.username ?? userInfo.value.username;
  console.log(usernameForPasswordChange);
  console.log(newPassword.value);

  const postData = {
    username: usernameForPasswordChange,
    password: newPassword.value,
  };

  try {
    const response = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/user/update-password`,
      postData
    );

    if (response.status === 200) {
      sessionStorage.removeItem("bypassAuth");
      await Swal.fire("成功", "密碼已成功更改", "success");
      // 可以在这里调用 getUserInfo() 或其他方法来刷新用户信息
    } else {
      throw new Error("Password update failed");
    }
  } catch (error) {
    console.error("更改密碼時出錯:", error);
    await Swal.fire("錯誤", "更改密碼失敗，請稍後再試。", "error");
  }
};
onUnmounted(() => {
  sessionStorage.removeItem("bypassAuth");
});
</script>

<style scoped>
.security-question h1,
.modify-info h1 {
  cursor: pointer;
  transition: color 0.3s ease;
}

.security-question h1:hover,
.modify-info h1:hover {
  color: #007bff;
}

.content {
  padding: 15px;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-top: 10px;
}

.security-question,
.modify-info {
  max-width: 500px;
  margin: 20px auto;
  cursor: pointer;
}

.form-sections {
  margin-bottom: 40px;
  display: none;
}

.form-sections h3 {
  margin-bottom: 15px;
}

form {
  display: flex;
  flex-direction: column;
}

select,
input {
  padding: 10px;
  margin-bottom: 20px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  padding: 10px;
  background-color: #4caf50;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 4px;
}

button:hover {
  background-color: #45a049;
}

.info-item {
  margin-bottom: 10px;
  text-align: left;
}

.buttons button {
  margin-right: 10px;
}

.cancel-btn {
  background-color: #d9534f;
}

.cancel-btn:hover {
  background-color: #c9302c;
}

.edit-section {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.edit-label,
.edit-input {
  text-align: left;
  margin-bottom: 10px;
}

.edit-input {
  padding: 10px;
  margin: 5px 0;
  border: 1px solid #ccc;
  border-radius: 4px;
}
</style>

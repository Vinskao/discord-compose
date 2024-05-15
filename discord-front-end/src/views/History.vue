<template>
  <Layout />
  <h1>RSA 實體列表</h1>
  <table>
    <thead>
      <tr>
        <th>名稱</th>
        <th>文件名</th>
        <th>公鑰</th>
        <th>簽名</th>
        <th>操作</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="entity in rsaEntities" :key="entity.id">
        <td>{{ entity.username }}</td>
        <td>{{ entity.name }}</td>
        <td class="fixed-width">{{ entity.pub }}</td>
        <td class="fixed-width">{{ entity.signature }}</td>
        <td>
          <button @click="downloadFile(entity.username, entity.name)">
            下載
          </button>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
import Swal from "sweetalert2";
import axios from "axios";
import { ref, onMounted } from "vue";
import Layout from "../layouts/Layout.vue";
axios.defaults.withCredentials = true;
const rsaEntities = ref([]);
import { useRouter } from "vue-router";
const router = useRouter();

onMounted(() => {
  fetchRSAEntities();
});

const fetchRSAEntities = async () => {
  try {
    const userInfoResponse = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/user/me`
    );
    const username = userInfoResponse.data.username.replace("@mli.com", "");
    // 檢查是否具有ADMIN權限
    if (!userInfoResponse.data.authorities.includes("ADMIN")) {
      Swal.fire({
        icon: "error",
        title: "Access Denied",
        text: "請聯絡管理員",
      });
      router.push("/index");
    }
    const response = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/get-rsa-entities`,
      { username }
    );
    rsaEntities.value = response.data;
  } catch (error) {
    console.error("Failed to fetch RSA entities:", error);
    Swal.fire({
      icon: "error",
      title: "Oops...",
      text: "Failed to fetch RSA entities.",
    });
  }
};

const downloadFile = async (username, fileName) => {
  try {
    const response = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/export-chat-history`,
      { username, fileName },
      { responseType: "blob" }
    );
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", fileName);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    console.error("Download failed:", error);
    if (error.response.data instanceof Blob) {
      // 讀取 Blob 內的文字
      const errorText = await error.response.data.text();
      Swal.fire({
        icon: "error",
        title: "Download Failed",
        text:
          errorText ||
          "An unexpected error occurred while downloading the file.",
      });
    } else {
      Swal.fire({
        icon: "error",
        title: "Download Failed",
        text: "An unexpected error occurred while downloading the file.",
      });
    }
  }
};
</script>
<style>
table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #400909;
}

tr:nth-child(even) {
  background-color: #342e2e;
}

.fixed-width {
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>

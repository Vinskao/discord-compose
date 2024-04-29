<!-- /src/views/Index.vue -->
<template>
  <div>
    <Layout />

    <div v-if="selectedGroupId === null">
      <!-- 群组列表 -->
      <div
        v-for="group in groups"
        :key="group.id"
        @click="selectGroup(group.id)"
        class="group-entry"
      >
        {{ group.name }}
      </div>
    </div>
    <div v-else>
      <Group
        :groupId="selectedGroupId"
        :onLeave="deselectGroup"
        ref="groupComponent"
        @roomSelected="() => toggleBackButton(false)"
        @roomDeselected="() => toggleBackButton(true)"
      />
      <button v-if="showBackButton" @click="deselectGroup">返回</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import Layout from "../layouts/Layout.vue";
import Group from "../components/Group.vue";
import axios from "axios";
import { onBeforeRouteLeave } from "vue-router";

axios.defaults.withCredentials = true;
const groupComponent = ref(null);
const router = useRouter();
const groups = ref([]);
const selectedGroupId = ref(null);
const showBackButton = ref(true);

const userInfoStr = localStorage.getItem("userInfo");
const userInfo = userInfoStr ? JSON.parse(userInfoStr) : null;
const username = userInfo ? userInfo.username : null;

const props = defineProps({
  groupId: Number,
  onLeave: Function, // 接收一个名为 onLeave 的函数作为 prop
});

onBeforeRouteLeave((to, from) => {
  deselectGroup();
});

window.addEventListener("beforeunload", (event) => {
  deselectGroup();

  event.returnValue = "Are you sure you want to leave?";
});

onMounted(async () => {
  checkUserRooms();
  await checkSession();
  try {
    const response = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/groups/find-all-groups`
    );
    groups.value = response.data;
  } catch (error) {
    console.error("Failed to fetch groups:", error);
  }
});

const toggleBackButton = (isVisible) => {
  showBackButton.value = isVisible;
};

const checkUserRooms = async () => {
  if (!username) {
    console.error("Username is not available.");
    return;
  }

  try {
    const response = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/user-to-room/get-by-username`,
      {
        username: username,
      }
    );
    showBackButton.value = response.data.length === 0;
  } catch (error) {
    console.error("Failed to fetch user rooms:", error);

    showBackButton.value = true;
  }
};

const checkSession = async () => {
  try {
    const userInfoResponse = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/user/me`
    );
    console.log("User info:", userInfoResponse.data);

    if (
      !userInfoResponse.data ||
      Object.keys(userInfoResponse.data).length === 0
    ) {
      throw new Error("No user info returned");
    }
  } catch (error) {
    console.error("用戶未登錄或session已過期:", error);
    router.push("/login");
  }
};

const selectGroup = async (groupId) => {
  if (selectedGroupId.value) {
    await deselectGroup();
  }

  console.log("Selected Group ID: ", groupId);
  selectedGroupId.value = groupId;

  const userInfoStr = localStorage.getItem("userInfo");
  const userInfo = userInfoStr ? JSON.parse(userInfoStr) : null;
  const username = userInfo ? userInfo.username : null;

  if (!username) {
    console.error("Username is not available.");
    return;
  }

  try {
    await axios.post(`${import.meta.env.VITE_HOST_URL}/user-to-group/add`, {
      username: username,
      groupId: groupId,
    });
  } catch (error) {
    console.error("Failed to add user to group:", error);
  }
};
const deselectGroup = async () => {
  if (selectedGroupId.value !== null) {
    const userInfoStr = localStorage.getItem("userInfo");
    const userInfo = userInfoStr ? JSON.parse(userInfoStr) : null;
    const username = userInfo ? userInfo.username : null;

    if (!username) {
      console.error("Username is not available.");
      return;
    }

    if (
      groupComponent.value &&
      typeof groupComponent.value.leaveCurrentRoom === "function"
    ) {
      await groupComponent.value.leaveCurrentRoom();
    }

    try {
      const response = await axios.post(
        `${import.meta.env.VITE_HOST_URL}/user-to-room/get-by-username`,
        { username: username }
      );
      const rooms = response.data;

      for (const room of rooms) {
        await axios.post(
          `${import.meta.env.VITE_HOST_URL}/user-to-room/remove`,
          {
            username: username,
            roomId: room.roomId,
          }
        );
      }
    } catch (error) {
      console.error("Failed to remove user from rooms:", error);
    }

    // 离开群组的逻辑
    try {
      await axios.post(
        `${import.meta.env.VITE_HOST_URL}/user-to-group/remove`,
        {
          username: username,
          groupId: selectedGroupId.value,
        }
      );
      selectedGroupId.value = null;
    } catch (error) {
      console.error("Failed to remove user from group:", error);
    }
    selectedGroupId.value = null;
    router.push("/login");
  }
};
</script>

<style>
.group-entry {
  cursor: pointer;
  padding: 10px 15px;
  margin: 5px 0;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

.group-entry:hover {
  background-color: #641d1d;
}

.group-entry:hover {
  font-weight: bold;
}
</style>

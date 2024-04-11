<!-- /src/components/Group.vue -->
<template>
  <div class="container">
    <div class="sidebar">
      <div
        v-for="room in rooms"
        :key="room.id"
        :class="{ 'selected-room': room.id === selectedRoomId }"
        @click="selectRoom(room.id)"
        class="room-entry"
      >
        {{ room.name }}
      </div>
    </div>
    <div class="room">
      <Room
        :roomId="selectedRoomId"
        ref="roomComponent"
        @roomLeft="handleRoomLeft"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import Room from "../components/Room.vue";
import axios from "axios";
axios.defaults.withCredentials = true;

const props = defineProps({
  groupId: Number, // 接收一個名為 groupId 的 prop，類型為 Number
});
const rooms = ref([]);
const selectedRoomId = ref(null);
const roomComponent = ref(null);

const emit = defineEmits(["roomSelected", "roomDeselected"]);

onMounted(async () => {
  // 檢查會話狀態
  try {
    const userInfoResponse = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/user/me`
    );
    // 如果請求成功，說明使用者已登錄，可以繼續加載群組房間信息
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

  // load一群組下的所有房間
  try {
    const response = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/room/find-all-rooms`
    );
    rooms.value = response.data;
  } catch (error) {
    console.error("無法獲取聊天室:", error);
  }
});

// 當 groupId 改變時，獲取相應的房間
watch(
  () => props.groupId,
  async (newGroupId) => {
    if (newGroupId !== null) {
      try {
        const response = await axios.post(
          `${import.meta.env.VITE_HOST_URL}/room/find-all-rooms`,
          {
            groupId: newGroupId,
          }
        );
        rooms.value = response.data;
      } catch (error) {
        console.error("無法獲取聊天室:", error);
      }
    }
  },
  { immediate: true }
);

const handleRoomLeft = () => {
  selectedRoomId.value = null;
  emit("roomDeselected");
};
const selectRoom = async (id) => {
  console.log("Selected Room ID: ", id);

  // 如果已經選擇了房間且不是當前房間，則先離開當前房間
  if (
    selectedRoomId.value &&
    selectedRoomId.value !== id &&
    roomComponent.value &&
    typeof roomComponent.value.leaveRoom === "function"
  ) {
    await roomComponent.value.leaveRoom(selectedRoomId.value);
  }

  // 更新 selectedRoomId 為新選中的房間ID
  selectedRoomId.value = id;

  // 確保 Room 組件已加載並且 joinRoom 方法可用
  if (
    roomComponent.value &&
    typeof roomComponent.value.joinRoom === "function"
  ) {
    roomComponent.value.joinRoom(id);
  } else {
    console.error("joinRoom 方法不可用");
  }

  emit("roomSelected");
};
</script>

<style>
.container {
  display: flex;
}

.sidebar {
  flex: 1;
  background-color: #2c3e50;
  color: #fff;
  overflow-y: auto;
}

.room-entry {
  padding: 10px 15px;
  margin: 5px 0;
  border-radius: 5px;
  transition: background-color 0.3s;
  cursor: pointer;
}

.room-entry:hover {
  background-color: #34495e;
}

.room {
  flex: 5;
  background-color: #1b1e1f;
  overflow-y: auto;
}

.selected-room {
  background-color: #2980b9;
  color: #fff;
}
</style>

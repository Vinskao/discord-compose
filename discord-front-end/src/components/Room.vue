<!-- /src/components/Room.vue -->

<template>
  <div class="room-container">
    <div v-if="!props.roomId" class="no-room-selected">
      請選擇一個聊天室以加入聊天。
    </div>
    <div v-else class="room-content">
      <div class="room">
        <div class="messages" ref="messagesContainer">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="{
              'my-message': isMyMessage(msg.username),
              'other-message': !isMyMessage(msg.username),
              'system-message': msg.type === 'JOIN' || msg.type === 'LEAVE',
            }"
            class="message"
          >
            <div v-if="msg.type === 'JOIN' || msg.type === 'LEAVE'">
              <em>{{ msg.message }}</em>
              <span class="message-time">{{ formatTime(msg.time) }}</span>
            </div>
            <div v-else>
              <strong
                >{{ msg.username ? msg.username.split("@")[0] : "" }}：</strong
              >
              {{ msg.message }}
              <span class="message-time">{{ formatTime(msg.time) }}</span>
            </div>
          </div>
        </div>

        <div class="input-area">
          <input
            type="text"
            v-model="inputMessage"
            placeholder="請輸入訊息..."
          />
          <button @click="sendMessage" :disabled="!isMessageValid">傳送</button>
        </div>
      </div>

      <div class="user-sidebar">
        <div v-for="user in roomUsers" :key="user.username" class="user-entry">
          {{ user.username.split("@")[0] }}
        </div>
      </div>

      <button @click="leaveRoom(props.roomId)" class="leave-button">
        離開
      </button>
    </div>
  </div>
  <button v-if="canExportChatHistory" @click="exportChatHistory">
    聊天紀錄下載
  </button>
</template>
<script setup>
import {
  ref,
  watch,
  watchEffect,
  onMounted,
  onBeforeUnmount,
  onUnmounted,
  nextTick,
  computed,
} from "vue";
import SockJS from "sockjs-client/dist/sockjs.min.js";
import { Stomp } from "@stomp/stompjs";
import axios from "axios";
import Swal from "sweetalert2";
import { onBeforeRouteLeave } from "vue-router";
import { useRouter } from "vue-router";
import { useStore } from "vuex";

axios.defaults.withCredentials = true;
const store = useStore();
const router = useRouter();
const connectedUsers = ref([]);
const currentRoomId = ref(null);
const canExportChatHistory = ref(false);
const messagesContainer = ref(null);
const userInfo = ref(null);
const currentUserUsername = ref("");
const emit = defineEmits(["roomLeft"]);
const messages = ref([]);
const inputMessage = ref("");
let stompClient = null;
const roomUsers = ref([]);
const userInfoStr = localStorage.getItem("userInfo");

if (userInfoStr) {
  const userInfo = JSON.parse(userInfoStr);
  currentUserUsername.value = userInfo.username.split("@")[0];
}

const props = defineProps({
  roomId: Number,
});

const isMessageValid = computed(() => {
  return inputMessage.value.trim().length > 0;
});

// 將時間格式化為可讀的格式
const formatTime = (timeString) => {
  if (!timeString) return "";
  const time = new Date(timeString);
  return time.toLocaleTimeString("en-US", {
    hour: "2-digit",
    minute: "2-digit",
  });
};

// 三種方法確保在任何方式離開聊天室都會觸發leaveRoom
window.addEventListener("beforeunload", () => {
  if (props.roomId && currentUserUsername.value) {
    leaveRoom(props.roomId);
  }
});
onBeforeRouteLeave((to, from) => {
  if (props.roomId && currentUserUsername.value) {
    leaveRoom(props.roomId);
  }
});
window.addEventListener("beforeunload", () => {
  if (props.roomId && currentUserUsername.value) {
    const leaveMessage = {
      username: currentUserUsername.value,
      roomId: props.roomId,
      type: "LEAVE",
    };
    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/app/message", false);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(leaveMessage));
  }
});

// 监视 messages 数组的变化
watchEffect(() => {
  if (messages.value.length > 0 && messagesContainer.value) {
    // 等待 Vue 更新 DOM
    nextTick(() => {
      // 滚动到 messagesContainer 元素的底部
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    });
  }
});

const changeRoom = async (newRoomId) => {
  if (newRoomId) {
    currentRoomId.value = newRoomId; // 更新 currentRoomId
    await joinRoom(newRoomId);
  }
  console.log("完成join api", newRoomId);
};

watch(
  currentRoomId,
  async (newRoomId, oldRoomId) => {
    if (newRoomId !== oldRoomId) {
      await changeRoom(newRoomId);
    }
  },
  { immediate: true }
);

let stompSubscription = null;
const reconnectStompAndSubscribe = (newRoomId) => {
  if (stompClient && isConnected) {
    if (stompSubscription) {
      stompSubscription.unsubscribe();
    }
    stompSubscription = stompClient.subscribe(
      `/topic/message/${newRoomId}`,
      (msg) => {
        const messageData = JSON.parse(msg.body);
        if (messageData.type === "JOIN") {
          connectedUsers.value.push(messageData.username);
        } else if (messageData.type === "LEAVE") {
          const index = connectedUsers.value.indexOf(messageData.username);
          if (index > -1) {
            connectedUsers.value.splice(index, 1);
          }
        }

        onStompMessageReceived(msg);
      }
    );

    stompClient.subscribe("/topic/message", (message) => {
      try {
        const updatedUserList = JSON.parse(message.body);
        if (Array.isArray(updatedUserList)) {
          connectedUsers.value = updatedUserList;
        } else {
          console.error("Received user list is not an array:", message.body);
        }
      } catch (error) {
        console.error("Error parsing user list:", error);
      }
    });
  }
};

onUnmounted(() => {
  store.dispatch("leaveRoom");
});

onMounted(async () => {
  console.log("Room.vue mounted");
  store.dispatch("enterRoom");
  console.log("Room ID in Room Component: ", props.roomId);
  currentRoomId.value = props.roomId;
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

  const userInfoStr = localStorage.getItem("userInfo");
  if (userInfoStr) {
    userInfo.value = JSON.parse(userInfoStr);
    canExportChatHistory.value = userInfo.value.authorities.includes("ADMIN");
    console.log("User info loaded from local storage:", userInfo.value);

    connectStomp();
    await loadMessages();
  } else {
    console.error("No user info found in local storage.");
    router.push("/login");
  }
});
onBeforeUnmount(() => {
  if (stompClient && isConnected) {
    if (stompSubscription) {
      stompSubscription.unsubscribe();
    }
    stompClient.disconnect(() => {
      console.log("STOMP client disconnected.");
    });
  }
});
// WebSocket 服務器的 URL
const SOCKET_URL = `${import.meta.env.VITE_HOST_URL}/ws-message`;
// 是否已連接到 WebSocket 服務器
let isConnected = false;
// 連接 STOMP
const connectStomp = () => {
  const socket = new SockJS(SOCKET_URL);
  stompClient = Stomp.over(socket);
  console.log("Attempting to connect to STOMP with roomId:", props.roomId);

  stompClient.connect(
    {},
    (frame) => {
      isConnected = true;
      console.log("Connected to STOMP!");

      stompClient.subscribe(`/topic/message/${props.roomId}`, (msg) => {
        const messageData = JSON.parse(msg.body);
        console.log("Received message: ", messageData);

        switch (messageData.type) {
          case "TEXT":
          case "JOIN":
          case "LEAVE":
            messages.value.push(messageData);
            break;
          default:
            console.warn("Received unknown message type:", messageData.type);
        }

        if (messageData.type === "JOIN" || messageData.type === "LEAVE") {
          fetchRoomUsers(props.roomId);
        }
      });

      stompClient.subscribe("/topic/message", (message) => {
        const messageData = JSON.parse(message.body);
        console.log("Received message from /topic/message:", messageData);
        if (messageData.type === "USER_LIST") {
          store.commit("updateOnlineUsers", messageData.message.split(","));
        }
      });

      socket.onclose = () => {
        console.log("WebSocket connection closed");
        if (props.roomId && userInfo.value) {
          leaveRoom(props.roomId);
        }
        router.push("/index");
      };
    },
    (error) => {
      isConnected = false;
      console.error("Error connecting to STOMP:", error);
    }
  );
};

// 接收到 STOMP 訊息時的處理函式
const onStompMessageReceived = (msg) => {
  console.log("New message received:", msg);
  const messageData = JSON.parse(msg.body);

  // 检查消息类型并相应地处理
  switch (messageData.type) {
    case "TEXT":
      messages.value.push(messageData);
      break;
    case "JOIN":
      messages.value.push(messageData);
      fetchRoomUsers(props.roomId);
      break;
    case "LEAVE":
      messages.value.push(messageData);
      if (Array.isArray(roomUsers.value)) {
        const userIndex = roomUsers.value.findIndex(
          (user) => user.username === messageData.username
        );
        if (userIndex !== -1) {
          roomUsers.value.splice(userIndex, 1);
        }
      } else {
        console.error("roomUsers.value is not an array:", roomUsers.value);
      }
      break;
    default:
      console.warn("Received unknown message type:", messageData.type);
  }
};
// 發送訊息
const sendMessage = () => {
  if (inputMessage.value.trim() !== "" && stompClient) {
    if (stompClient.connected) {
      const messageToSend = {
        username: currentUserUsername.value,
        message: inputMessage.value,
        roomId: props.roomId,
        type: "TEXT",
      };

      stompClient.send("/app/message", {}, JSON.stringify(messageToSend));

      inputMessage.value = "";
    } else {
      console.error("STOMP client is not connected.");
    }
  }
};

// 判斷是否為當前使用者的訊息
const isMyMessage = (msgUsername) => {
  return msgUsername.split("@")[0] === currentUserUsername.value;
};

// 加入房間
const joinRoom = async (roomId) => {
  if (roomId == null || !userInfo.value) {
    console.error("未選擇聊天室，或用戶信息不完整，無法加入。");
    return;
  }

  console.log(
    `加入房間。使用者名稱：${userInfo.value.username}，房間ID：${roomId}`
  );

  try {
    // Step 1: Make a call to check if the user is already in the room
    const response = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/user-to-room/get-by-room`,
      { roomId }
    );

    // Handle the case where the room is empty (204 No Content)
    let usersInRoom = [];
    if (response.status === 204) {
      console.log("The room is currently empty.");
    } else {
      usersInRoom = response.data;
    }
    // Step 2: Check if the current user is in the list
    const isUserInRoom = usersInRoom.some(
      (user) => user.username === userInfo.value.username
    );

    // Step 3: If user is found, show SweetAlert and return
    if (isUserInRoom) {
      Swal.fire({
        icon: "error",
        title: "Oops...",
        text: "你已經在該聊天室中，不能重複加入！",
      });
      return;
    }
  } catch (error) {
    console.error("檢查用戶是否已在房間中時發生錯誤", error);
    return;
  }
  messages.value = [];

  // 重新加载新房间的消息
  await loadMessages(roomId);

  // 获取新房间的用户列表
  await fetchRoomUsers(roomId);

  // 重新连接STOMP并订阅新房间的主题
  reconnectStompAndSubscribe(roomId);

  const joinMessage = {
    username: userInfo.value.username,
    message: `${currentUserUsername.value} 已加入`,
    roomId: roomId,
    type: "JOIN",
  };

  // 發送加入房間的消息
  stompClient.send("/app/message", {}, JSON.stringify(joinMessage));

  try {
    await axios.post(`${import.meta.env.VITE_HOST_URL}/user-to-room/add`, {
      username: userInfo.value.username,
      roomId: roomId,
    });
  } catch (error) {
    console.error("加入新房間時發生錯誤", error);
  }

  await nextTick(); // 等待 Vue 更新 DOM 或状态

  await fetchRoomUsers(roomId); // 獲取房間內的用戶列表
};

// 離開房間
const leaveRoom = async (roomId) => {
  if (!props.roomId || !userInfo.value) {
    console.error("未選擇聊天室，或用戶信息不完整，無法離開。");
    return;
  }

  console.log(
    `離開房間。使用者名稱：${userInfo.value.username}，房間ID：${props.roomId}`
  );

  const leaveMessage = {
    username: userInfo.value.username,
    message: `${currentUserUsername.value} 已離開`,
    roomId: props.roomId,
    type: "LEAVE",
  };

  // 發送離開房間的消息
  if (stompClient && stompClient.connected) {
    stompClient.send("/app/message", {}, JSON.stringify(leaveMessage));
  } else {
    console.error("STOMP client is not connected. Cannot send leave message.");
    // reconnecting
    attemptReconnect();
  }
  try {
    await axios.post(`${import.meta.env.VITE_HOST_URL}/user-to-room/remove`, {
      username: userInfo.value.username,
      roomId: roomId,
    });
  } catch (error) {
    console.error("離開房間時發生錯誤", error);
  }

  await nextTick(); // 等待 Vue 更新 DOM 或状态

  await fetchRoomUsers(roomId); // 更新房間內的用戶列表

  // 通知父組件房間已離開
  emit("roomLeft");
};

const attemptReconnect = () => {
  if (!stompClient) {
    stompClient = Stomp.over(new SockJS(SOCKET_URL));
  }

  // Attempt to reconnect
  stompClient.connect(
    {},
    (frame) => {
      isConnected = true;
      console.log("Reconnected to STOMP server.");
      // Retry sending the leave message or any other pending actions here
    },
    (error) => {
      isConnected = false;
      console.error("Failed to reconnect to STOMP server:", error);
      // Handle reconnection failure (e.g., retry after a delay)
    }
  );
};

defineExpose({
  joinRoom,
  leaveRoom,
});

// 載入歷史訊息
const loadMessages = async (roomId) => {
  try {
    const response = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/get-messages`,
      { roomId }
    );
    const historyMessages = response.data;

    // 將歷史訊息與當前訊息合併，並按時間排序
    messages.value = [...historyMessages, ...messages.value].sort(
      (a, b) => new Date(a.time) - new Date(b.time)
    );
  } catch (error) {
    console.error("載入訊息時發生錯誤:", error);
  }
};

const fetchRoomUsers = async (roomId) => {
  try {
    const response = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/user-to-room/get-by-room`,
      { roomId: roomId }
    );
    roomUsers.value = response.data;
  } catch (error) {
    console.error("獲取房間用戶時發生錯誤:", error);
  }
};

const exportChatHistory = async () => {
  console.log(`Exporting chat history for room ID: ${props.roomId}`);

  try {
    const response = await axios.post(
      `${import.meta.env.VITE_HOST_URL}/export-chat-history`,
      { roomId: props.roomId },
      { responseType: "blob" }
    );

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", `chat_history_room_${props.roomId}.xlsx`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    console.log("Chat history download initiated.");
  } catch (error) {
    console.error("Error exporting chat history:", error);
    Swal.fire({
      icon: "error",
      title: "Oops...",
      text: "你沒有權限喔!",
    });
  }
};
</script>

<style>
.room-container {
  display: flex;
  /* 使用 Flexbox 佈局 */
  align-items: flex-start;
  /* 頂部對齊 */
}

.room-content {
  display: flex;
  /* 也使用 Flexbox 佈局來並排顯示 .room 和 .user-sidebar */
  flex: 1;
  /* 確保它填充父容器的剩餘空間 */
  overflow-y: auto;
  /* 內容過多時可滾動 */
}

.room {
  flex: 1;
  /* 確保聊天室內容填充可用空間 */
  overflow-y: auto;
  /* 內容過多時可滾動 */
}

.user-sidebar {
  width: 100px;
  /* 用戶列表側邊欄寬度 */
  background-color: #2c3e50;
  /* 背景色 */
  margin-left: 20px;
  /* 與聊天室內容間距 */
  padding: 10px;
  /* 內邊距 */
  overflow-y: auto;
  /* 內容過多時可滾動 */
}

.user-entry {
  margin-bottom: 10px;
  /* 用戶條目間距 */
  padding: 5px;
  /* 用戶條目內邊距 */
  background-color: #10bd3b;
  /* 用戶條目背景色 */
  border-radius: 5px;
  /* 圓角 */
}

.system-message {
  text-align: center;
  color: #888;
}

.messages {
  display: flex;
  flex-direction: column;
  padding: 10px;
  overflow-y: auto;
  /* 如果訊息太多，允許滾動 */
  height: 500px;
  /* 根據需要調整高度 */
}

.message {
  max-width: 80%;
  margin-bottom: 10px;
  padding: 10px;
  border-radius: 15px;
  /* 圓角邊框 */
  word-wrap: break-word;
  /* 避免長訊息溢出 */
}

.my-message {
  align-self: flex-end;
  /* 將個人訊息靠右顯示 */
  background-color: #3498db;
  /* 個人訊息的背景色 */
  color: white;
  /* 文本顏色 */
}

.other-message {
  align-self: flex-start;
  /* 將他人的訊息靠左顯示 */
  background-color: #ecf0f1;
  /* 他人訊息的背景色 */
  color: #2c3e50;
  /* 文本顏色 */
}

.message-time {
  display: block;
  font-size: 0.75em;
  margin-top: 5px;
  color: #bdc3c7;
  /* 時間戳顏色 */
}

/* 美化輸入區域 */
.input-area {
  display: flex;
  margin-top: 10px;
}

input[type="text"] {
  flex-grow: 1;
  /* 輸入框填充剩餘空間 */
  padding: 10px;
  border-radius: 20px;
  /* 圓角邊框 */
  border: 1px solid #bdc3c7;
  margin-right: 10px;
  /* 與傳送按鈕的間距 */
}

button {
  padding: 10px 20px;
  border-radius: 20px;
  /* 圓角按鈕 */
  background-color: #3498db;
  /* 按鈕背景色 */
  color: white;
  /* 文本顏色 */
  border: none;
  /* 移除邊框 */
  cursor: pointer;
  /* 鼠標懸停時顯示手型指針 */
}

button:hover {
  background-color: #2980b9;
  /* 鼠標懸停時的背景色 */
}

.leave-button {
  position: absolute;
  bottom: 0;
  right: 0;
  background-color: red;
}

.leave-button:hover {
  position: absolute;
  bottom: 0;
  right: 0;
  background-color: rgb(54, 17, 17);
}

.no-room-selected {
  text-align: center;
  margin-top: 50px;
  /* 或根據需要調整 */
  font-size: 18px;
  /* 或根據需要調整 */
  color: #bdc3c7;
  /* 或根據需要調整 */
}
</style>

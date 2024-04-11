// src/store/index.js

import { createStore } from "vuex";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
const SOCKET_URL = `${import.meta.env.VITE_HOST_URL}/ws-message`;

export const store = createStore({
  state: {
    stompClient: null,
    isInRoom: false,
  },
  mutations: {
    // 設置 Stomp 客戶端
    setStompClient(state, client) {
      state.stompClient = client;
    },
    // 設置是否在聊天室中的狀態
    setIsInRoom(state, isInRoom) {
      state.isInRoom = isInRoom;
    },
    // 更新線上使用者
    updateOnlineUsers(state, onlineUsers) {
      console.log("Updating online users:", onlineUsers);
      state.onlineUsers = onlineUsers;
    },
  },
  actions: {
    // 連接 Stomp 客戶端
    connectStomp({ commit }) {
      const socket = new SockJS(SOCKET_URL);
      const client = Stomp.over(socket);
      client.connect({}, (frame) => {
        commit("setStompClient", client);
      });
    },
    // 斷開 Stomp 客戶端連接
    disconnectStomp({ state }) {
      if (state.stompClient && state.stompClient.connected) {
        state.stompClient.disconnect();
      }
    },
    // 進入聊天室
    enterRoom({ commit }) {
      commit("setIsInRoom", true);
    },
    // 離開聊天室
    leaveRoom({ commit }) {
      commit("setIsInRoom", false);
    },
  },
});

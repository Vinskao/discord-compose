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
    setStompClient(state, client) {
      state.stompClient = client;
    },
    setIsInRoom(state, isInRoom) {
      state.isInRoom = isInRoom;
    },
    updateOnlineUsers(state, onlineUsers) {
      console.log("Updating online users:", onlineUsers);
      state.onlineUsers = onlineUsers;
    },
  },
  actions: {
    connectStomp({ commit }) {
      const socket = new SockJS(SOCKET_URL);
      const client = Stomp.over(socket);
      client.connect({}, (frame) => {
        commit("setStompClient", client);
      });
    },
    disconnectStomp({ state }) {
      if (state.stompClient && state.stompClient.connected) {
        state.stompClient.disconnect();
      }
    },
    enterRoom({ commit }) {
      commit("setIsInRoom", true);
    },
    leaveRoom({ commit }) {
      commit("setIsInRoom", false);
    },
  },
});

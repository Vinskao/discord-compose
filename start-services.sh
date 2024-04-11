#!/bin/bash

# 步驟 1: 啟動資料庫服務
echo "正在啟動資料庫服務..."
docker-compose up -d sql_edge

# 等待資料庫服務啟動
echo "正在等待資料庫服務啟動..."
sleep 5  

# 步驟 2: 執行資料庫初始化腳本
echo "正在初始化資料庫..."
docker-compose up -d sqlcmd

# 等待資料庫初始化完成
echo "正在等待資料庫初始化完成..."
sleep 20  

# 步驟 3: 禁用 Docker BuildKit，建構並啟動後端服務
echo "正在建構並啟動後端服務..."
# docker-compose up -d discord_backend
docker-compose up --build -d discord_backend


# 步驟 4: 啟動前端服務
echo "正在啟動前端服務..."
docker-compose up -d discord_frontend
# docker-compose up --build -d discord_frontend

echo "所有服務已啟動完成。"

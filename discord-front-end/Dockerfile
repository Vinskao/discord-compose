# 以 Node.js 官方的長期支持版本作為基礎映像
FROM node:lts AS build

# 設定容器內的工作目錄為 /app。接下來的指令將會在這個目錄下執行
WORKDIR /app

# 複製依賴文件
COPY package*.json ./

# 安裝依賴
RUN npm install

# 將除了 .dockerignore 文件中指定的內容外，將專案的所有其他文件和目錄都複製到工作目錄中。
COPY . .

# 指示 Docker 在運行容器時監聽 8090 端口
EXPOSE 8090

# 啟動開發伺服器，直接使用 Vite 提供的命令行選項來禁用自動打開瀏覽器的功能。
CMD ["npx", "vite", "--host", "--port", "8090", "--no-open"]

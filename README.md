### 操作步驟

在資料夾跟目錄執行以下指令以啟動資料庫、後端、前端：

```bash
./start-services.sh
```

前端 url：
http://localhost:8090/

後端 swagger url：
http://localhost:8088/swagger-ui/index.html

登入帳密：

```
| 帳號              密碼        權限    |
| chiaki@mli.com   password1   ADMIN  |
| min@mli.com      password2   NORMAL |
| alice@mli.com    password3   NORMAL |
```

### 原理

使用 docker-compose.yml 一次啟動多服務。

總共需要額外撰寫 docker-compose.yml、前端 Dockerfile、後端 Dockerfile、start-services.sh、資料庫 init-db.sql 五個檔案。

init-db.sql 裡面放資料庫一啟動就想要建立的表跟插入的資料，直接在跟目錄建立一個 data 資料夾，然後把 sql 檔案放入。

### docker-compose

此檔案負責所有容器啟動的總設定，他可以連結到個別容器的 Dockerfile 去值行裡面的詳細設定，寫完此檔案，在根目錄執行：

```bash
docker-compose up
```

如果要重新建立鏡像：

```bash
docker compose up --build
```

就可以一次啟動整個服務。但是因為是同時啟動，如果有資料庫要先完成，後端再插入初始資料的需求，就可能在後端插入資料時資料庫還沒建好。所以才有以下這個檔案的出現。

```yml
version: "3.8"

services:
  sql_edge:
    image: mcr.microsoft.com/azure-sql-edge:latest
    environment:
      ACCEPT_EULA: "Y"
      SA_PASSWORD: "Wawi247525="
      MSSQL_COLLATION: "Chinese_Taiwan_Stroke_CI_AI"
      TZ: "Asia/Taipei"
    ports:
      - "1433:1433"
    volumes:
      # 使用先前已經設定好的volume[discord_db_data]來同步來自mssql容器的即時資料
      - discord_db_data:/var/opt/mssql
    networks:
      - discord_network

  sqlcmd:
    image: mcr.microsoft.com/mssql-tools:latest
    command: >
      /bin/bash -c "
        sleep 20;
        /opt/mssql-tools/bin/sqlcmd -S sql_edge -U SA -P 'Wawi247525=' -d master -i /data/init-db.sql;
        "
    # 此命令可以先指示容器使用bash去執行sqlcmd連線，並input初始化資料進去
    volumes:
      - ./data:/data
    networks:
      - discord_network

  discord_backend:
    build:
      context: ./discord-back-end
      dockerfile: Dockerfile
    ports:
      - "8088:8088"
    volumes:
      - java_logs:/var/opt/logs/discord
    networks:
      - discord_network
    environment:
      #   此處的DATASOURCE跟後端的application.yml配合
      - DATASOURCE_URL=jdbc:sqlserver://sql_edge:1433;databaseName=discord;trustServerCertificate=true
      - DATASOURCE_USERNAME=SA
      - DATASOURCE_PASSWORD=${DATASOURCE_PASSWORD}
      #   如果密碼用這種方式寫，就需要在跟目錄建立.env檔來放置密碼

  discord_frontend:
    build:
      context: ./discord-front-end
      dockerfile: Dockerfile
    ports:
      - "8090:8090"
    networks:
      - discord_network

volumes:
  discord_db_data:
  java_logs:

networks:
  discord_network:
    name: discord_network
    external: true
    # 為true時表示該網路已經存在，且不是由目前的Docker Compose配置所建立的
```

#### start-services.sh

此為腳本，目的為決定 docker-coompose 中服務的執行順序。在每個服務啟動中間設定等待時間，以確保後啟動的服務可以運用先啟動的服務之功能。

```sh
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
# 如果不用重build
docker-compose up -d discord_backend
# 如果要重build
docker-compose up --build -d discord_backend


# 步驟 4: 啟動前端服務
echo "正在啟動前端服務..."
# 如果不用重build
docker-compose up -d discord_frontend
# 如果要重build
docker-compose up --build -d discord_frontend

echo "所有服務已啟動完成。"

```

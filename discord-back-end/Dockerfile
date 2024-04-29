# 第一階段，選用了一個包含Maven 3.8.4和OpenJDK 11的官方基礎映像。這個映像被用作構建階段，即編譯和打包Java應用。as build部分給這一階段命名為build
# 使用 Maven 和 OpenJDK 11 的官方基礎映像來構建專案
FROM maven:3.8.4-openjdk-11-slim as build

# 複製專案文件到容器內的/app目錄
COPY . /app

# 設定工作目錄為/app
WORKDIR /app

# 使用 Maven 命令來構建專案，跳過單元測試
RUN mvn clean package -DskipTests

# 第二階段，這次選用的是僅包含OpenJDK 11 JRE（Java運行時環境）的輕量級基礎映像。這個映像用於運行已經構建好的Java應用，而不包含構建應用所需的任何額外工具或庫
# 使用 OpenJDK 11 的輕量級基礎映像來運行構建好的應用
FROM openjdk:11-jre-slim

# 從構建階段複製構建好的 JAR 應用到運行階段的容器內
COPY --from=build /app/target/discord-0.0.1-SNAPSHOT.jar /discord.jar

# 指定運行應用的命令
ENTRYPOINT ["java", "-jar", "/discord.jar"]

# 定義容器內應用將會使用的端口
EXPOSE 8080

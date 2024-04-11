### 使用說明

1.  JDK version = 11;

2.  clone 專案以後，執行 mvn clean install

3.  資料庫使用 discord，已先建立表格，以及預設 3 個帳號

```
| 帳號              密碼        權限    |
| chiaki@mli.com   password1   ADMIN  |
| min@mli.com      password2   NORMAL |
| alice@mli.com    password3   NORMAL |
```

4.  運行 ApiSpringBoot2Application.java

    以啟動 Spring Boot

5.  打開 http://localhost:8088/swagger-ui/index.html

    以查看 api 狀態

### 權限功能

1. 請使用 Admin 帳號以使用匯出聊天紀錄功能
2. 其餘功能都可透過 NORMAL 帳號使用
3. 註冊的帳號一率都是 NORMAL 帳號

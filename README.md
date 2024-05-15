### 連線

前端 url：
http://localhost:8090/
http://www.localhost/

後端 swagger url：
http://localhost:8080/swagger-ui/index.html
http://api.localhost/swagger-ui/index.html

登入帳密：

```
| 帳號              密碼        權限
| chiaki@mli.com   password1   ADMIN
| min@mli.com      password2   NORMAL
| alice@mli.com    password3   NORMAL
```

### 原理

#### HTTPS 是否一定要使用 TLS？

HTTPS (HyperText Transfer Protocol Secure) 原本是基於 SSL (Secure Sockets Layer) 加密，後來發展到現在主要使用 TLS (Transport Layer Security)。事實上，TLS 是 SSL 的後續版本，提供了更強的安全性。因此，現在的 HTTPS 通信基本上是透過 TLS 實現的。

#### CA 是必要的嗎？

CA 的全稱是 Certificate Authority（證書授權中心）。在公開鑰匙基礎設施 (PKI) 中，CA 負責簽發和管理安全/數位證書。CA 是必要的，因為它確保了交換數據雙方的身份可以被信任。沒有 CA，用戶端對伺服器的身份驗證就會缺乏信任基礎，增加中間人攻擊的風險。

#### HTTPS 的握手過程

HTTPS 的握手過程主要是為了安全地交換加密用的鑰匙，並確認對方的身份。握手過程包括以下幾個主要步驟：

- 客戶端 Hello：客戶端發送一個包含支持的 TLS 版本、加密套件列表和一個隨機數的消息。
- 服務器 Hello：服務端回應其選擇的加密套件和另一個隨機數。
- 證書和鑰匙交換：服務端發送其數位證書（含公鑰）給客戶端。服務端可能還會發送一個鑰匙交換消息，客戶端將使用這些信息來生成對稱加密的鑰匙。
- 客戶端鍵交換：客戶端根據服務器提供的公鑰，生成一個預導密鑰（pre-master secret），並將其加密後發送回服務器。
- 結束握手：雙方確認握手過程完成，並開始使用對稱加密來加密通信數據。
  握手過程達到加密的原因主要是通過交換加密的鑰匙（如預導密鑰），這個過程中使用了公鑰加密和私鑰解密的機制。數據本身則是使用對稱加密來保護。

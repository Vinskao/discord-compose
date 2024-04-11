package com.mli.discord.core.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
/**
 * 監聽HTTP請求事件的監聽器。
 * 當請求被初始化時，確保創建一個HttpSession。
 *
 * @Author D3031104
 * @version 1.0
 */
@WebListener
public class RequestListener implements ServletRequestListener {
    /**
     * 請求銷毀時的回調方法。
     * 當前實現為空，可以根據需要進行擴展。
     *
     * @param sre 請求事件
     */
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
    }
    /**
     * 請求初始化時的回調方法。
     * 確保被創建的請求擁有一個HttpSession。
     *
     * @param sre 請求事件
     */
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        // 確保 HttpSession 被創建
        ((HttpServletRequest) sre.getServletRequest()).getSession();
    }
}
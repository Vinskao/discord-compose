package com.mli.discord.core.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
/**
 * 監聽HTTP會話生命週期事件的監聽器。
 * 當會話被創建或銷毀時，將打印會話ID。
 *
 * @Author D3031104
 * @version 1.0
 */
public class SessionListener implements HttpSessionListener {
    /**
     * 會話創建時的回調方法。
     * 打印創建的會話ID。
     *
     * @param se 會話事件
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("Session created: " + se.getSession().getId());
    }
    /**
     * 會話銷毀時的回調方法。
     * 打印銷毀的會話ID。
     *
     * @param se 會話事件
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("Session destroyed: " + se.getSession().getId());
    }
}
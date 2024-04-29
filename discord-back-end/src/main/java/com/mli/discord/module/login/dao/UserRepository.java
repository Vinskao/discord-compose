package com.mli.discord.module.login.dao;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.mli.discord.module.login.model.Authority;
import com.mli.discord.module.login.model.User;

/**
 * 使用者資料庫存取類別，用於管理使用者資料的存取和操作。
 * 
 * 
 * @Author D3031104
 * @version 1.0
 */

@Repository
public class UserRepository {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 對使用者密碼進行加密處理。
     * 
     * @param users 需要加密密碼的使用者物件陣列
     */
    private void encodePasswords(User... users) {
        for (User user : users) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }
    }

    /**
     * 初始化方法，用於在系統啟動時檢查是否已存在使用者資料，若不存在則初始化資料並插入資料庫。
     */
    @PostConstruct
    private void init() {
        // 定义要初始化的用户信息
        LocalDateTime randomBirthday1 = LocalDateTime.of(1990, 5, 24, 12, 0);
        LocalDateTime randomBirthday2 = LocalDateTime.of(1992, 8, 15, 12, 0);
        LocalDateTime randomBirthday3 = LocalDateTime.of(1985, 3, 9, 12, 0);
        String interests1 = "閱讀";
        String interests2 = "游泳";
        String interests3 = "旅行";

        // 检查数据库中是否已存在这些用户名，如果不存在，则创建新用户
        checkAndCreateUser("chiaki@mli.com", "password1", Authority.ADMIN.toString(), randomBirthday1, interests1);
        checkAndCreateUser("min@mli.com", "password2", Authority.NORMAL.toString(), randomBirthday2, interests2);
        checkAndCreateUser("alice@mli.com", "password3", Authority.NORMAL.toString(), randomBirthday3, interests3);
    }

    private void checkAndCreateUser(String username, String password, String authority, LocalDateTime birthday,
            String interests) {
        // 检查数据库中是否已存在该用户名
        Optional<User> existingUser = userDAO.findByUsername(username);

        // 如果用户不存在，则创建新用户并加密密码
        if (existingUser == null) {
            User newUser = new User(password, username, authority, birthday, interests);
            encodePasswords(newUser);
            userDAO.insertUser(newUser);
        }
    }

    /**
     * 將指定使用者插入資料庫。
     * 
     * @param user 需要插入的使用者物件
     */
    public void insert(User user) {
        encodePasswords(user);
        userDAO.insertUser(user);
    }

    /**
     * 根據使用者ID查詢並返回使用者物件。
     * 
     * @param userId 使用者ID
     * @return 查詢到的使用者物件
     */
    public User findById(int userId) {
        return userDAO.findById(userId).get(0);
    }

    public Integer createUser(String username, String password, LocalDateTime birthday, String interests) {
        // 创建一个新的 User 对象
        User newUser = new User(password, username, Authority.NORMAL.toString(), birthday, interests);

        // 对用户密码进行加密
        encodePasswords(newUser);

        // 将新用户信息插入数据库
        userDAO.insertUser(newUser);

        return 1;
    }
}

package com.mli.discord.module.login.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.mli.discord.module.login.model.User;
import org.apache.ibatis.annotations.Param;

/**
 * User DAO MyBatis Mapper
 * 
 * @Author D3031104
 * @version 1.0
 */
public interface UserDAO {
    /**
     * 将新用户插入数据库。
     * 
     * @param user 要插入的用户
     */
    void insertUser(User user);

    /**
     * 根据用户名和密码查找用户。
     * 
     * @param username 用户名
     * @param password 密码
     * @return 如果找到用户，则返回用户的 ID；否则返回 null
     */
    Integer findByUsernameAndPassword(String username, String password);

    /**
     * Find user by their ID.
     * 
     * @param userId The user ID
     * @return A list of user with the specified ID
     */
    List<User> findById(Integer userId);

    /**
     * 查询所有用户
     * 
     * @return 包含所有用户的列表
     */
    List<User> findAll();

    /**
     * 根据用户名查找用户的加密密码。
     * 
     * @param username 用户名
     * @return 用户的加密密码
     */
    User findEncodedPasswordByUsername(String username);

    /**
     * 根据用户名查找用户。
     * 
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);

    Integer updatePasswordByUsername(@Param("username") String username, @Param("password") String newPassword);

    /**
     * 根据用户名更新用户的生日和兴趣。
     * 
     * @param username  用户名
     * @param birthday  新的生日
     * @param interests 新的兴趣
     * @return 更新的记录数
     */
    Integer updateUserDetailsByUsername(@Param("username") String username, @Param("birthday") LocalDateTime birthday,
            @Param("interests") String interests);

}

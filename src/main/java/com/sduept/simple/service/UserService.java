package com.sduept.simple.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.entity.User;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-03-21
 */
public interface UserService extends IService<User> {

    /**
     * 检查用户名和密码是否争取
     *
     * @param userName 用户名
     * @param password 密码
     */
    ServerResponse checkUsernameAndPassword(String userName, String password);

    /**
     * 列出所有的用户列表
     */
    ServerResponse listUsers();

    /**
     * 新增用户
     *
     * @param user 用户
     */
    ServerResponse addUser(User user);

    /**
     * 编辑用户
     *
     * @param user 用户
     */
    ServerResponse editUser(User user);

    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     */
    ServerResponse deleteUser(Integer id);
}

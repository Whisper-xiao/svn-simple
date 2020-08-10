package com.sduept.simple.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.entity.Role;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-03-21
 */
public interface RoleService extends IService<Role> {

    /**
     * 查询出所有的用户
     */
    ServerResponse listRoles();

    /**
     * 新增角色
     *
     * @param role 角色模型
     */
    ServerResponse addRole(Role role);

    /**
     * 编辑角色
     *
     * @param role 角色
     */
    ServerResponse editRole(Role role);

    /**
     * 根据ID删除角色
     *
     * @param id 角色ID
     */
    ServerResponse deleteRole(Integer id);
}

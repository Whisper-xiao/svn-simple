package com.sduept.simple.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.entity.Role;
import com.sduept.simple.entity.User;
import com.sduept.simple.mapper.UserMapper;
import com.sduept.simple.service.RoleService;
import com.sduept.simple.service.UserService;
import com.sduept.simple.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-03-21
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RoleService roleService;

    @Override
    public ServerResponse checkUsernameAndPassword(String userName, String password) {
        LambdaQueryWrapper<User> wrapper = new QueryWrapper<User>().lambda()
                .eq(User::getUserName, userName).eq(User::getPassword, password);
        List<User> users = list(wrapper);
        if (users.isEmpty()) {
            return ServerResponse.createByErrorMessage("用户名或者密码错误");
        }
        return ServerResponse.createBySuccess(assembleUserVo(users.get(0)));
    }

    private UserVo assembleUserVo(User user) {
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        Role role = roleService.getById(user.getRoleId());
        userVo.setRoleName(role.getRoleName());
        userVo.setType(role.getPostType());
        return userVo;
    }

    @Override
    public ServerResponse listUsers() {
        return ServerResponse.createBySuccess(list());
    }

    @Transactional
    @Override
    public ServerResponse addUser(User user) {
        Role xiao = roleService.getOne(new QueryWrapper<Role>().lambda().eq(Role::getRoleName, "xiao"));
        if (StringUtils.isBlank(user.getUserName())) {
            return ServerResponse.createByErrorMessage("用户名不能为空");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            return ServerResponse.createByErrorMessage("密码不能为空");
        }

        boolean isSuccessed = save(user);
        if (isSuccessed) {
            return ServerResponse.createBySuccessMessage("用户新增成功");
        }
        return ServerResponse.createByErrorMessage("新增用户失败");
    }

    @Override
    public ServerResponse editUser(User user) {
        boolean isSuccessed = updateById(user);
        if (isSuccessed) {
            return ServerResponse.createBySuccessMessage("用户编辑成功");
        }
        return ServerResponse.createByErrorMessage("用户编辑失败");
    }

    @Override
    public ServerResponse deleteUser(Integer id) {
        if (Objects.isNull(id)) {
            return ServerResponse.createByErrorMessage("用户ID不能为空");
        }
        boolean isSuccessed = removeById(id);
        if (isSuccessed) {
            ServerResponse.createBySuccessMessage("用户删除成功");
        }
        return ServerResponse.createByErrorMessage("用户删除失败");
    }
}

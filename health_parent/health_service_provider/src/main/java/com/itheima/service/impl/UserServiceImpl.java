package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.PermissionDao;
import com.itheima.dao.RoleDao;
import com.itheima.dao.UserDao;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;

    @Override
    public User findByUsername(String username) {
        //1. 查询用户信息
        User user = userDao.findByUsername(username);
        //1.1 如果查询结果为空，直接返回
        if (user == null) {
            return null;
        }

        //2. 跟据用户ID查询角色信息
        Integer userId = user.getId();
        Set<Role> roleSet = roleDao.findByUserId(userId);
        if (roleSet != null && roleSet.size() > 0) {
            for (Role role : roleSet) {
                //3. 跟据角色ID查询权限
                Integer roleId = role.getId();
                Set<Permission> permissionSet = permissionDao.findByRoleId(roleId);
                if (permissionSet != null && permissionSet.size() > 0) {
                    //3.1 为角色设置权限信息
                    role.setPermissions(permissionSet);
                }
            }
            //2.1 为用户设置角色信息
            user.setRoles(roleSet);
        }

        //3. 返回填充好的用户信息
        return user;
    }
}

package com.itheima.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class SpringSecurityUserService implements UserDetailsService {

    @Reference
    private UserService userService;

    /**
     * 跟据用户名查询数据库，获取用户信息
     * @param username 用户名
     * @return 结果
     * @throws UsernameNotFoundException 用户名不存在异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /*
            数据库默认提供的用户：
                user1    username: admin | password: admin
                user2    username: xiaoming | password: 1234
         */
        //1. 跟据用户名查询用户信息
        User user = userService.findByUsername(username);

        //2. 用户信息为null，直接返回
        if (user == null) {
            return null;
        }

        //3. 为角色进行授权（授予角色、权限）
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        //3.1 授予角色
        Set<Role> roles = user.getRoles();
        for (Role role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getKeyword()));
            //3.2 授予权限
            Set<Permission> permissions = role.getPermissions();
            for (Permission permission : permissions) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission.getKeyword()));
            }
        }

        //4. 封装UserDetails对象
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username, user.getPassword(), grantedAuthorities
        );

        //5. 返回角色对象
        return userDetails;
    }
}

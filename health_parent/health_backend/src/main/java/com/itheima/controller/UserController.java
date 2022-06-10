package com.itheima.controller;

import com.itheima.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.itheima.constant.MessageConstant.GET_USERNAME_FAIL;
import static com.itheima.constant.MessageConstant.GET_USERNAME_SUCCESS;

/**
 * 用户管理
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 获取当前登录用户的用户名
     *
     * @return 结果
     * @throws Exception 异常
     */
    @RequestMapping("/getUsername")
    public Result getUsername() throws Exception {
        try {
            //1. 通过Spring Security容器获取用户信息
            org.springframework.security.core.userdetails.User user =
                    (org.springframework.security.core.userdetails.User)
                            SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            //2. 返回用户信息
            logger.info("当前登录用户为：[{}]", user.getUsername());
            return new Result(true, GET_USERNAME_SUCCESS, user.getUsername());
        } catch (Exception e) {
            //3. 获取失败
            return new Result(false, GET_USERNAME_FAIL);
        }
    }
}

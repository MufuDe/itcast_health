package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itheima.entity.BaseContext;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

import static com.itheima.constant.MessageConstant.LOGIN_SUCCESS;
import static com.itheima.constant.MessageConstant.VALIDATECODE_ERROR;
import static com.itheima.constant.RedisMessageConstant.SENDTYPE_CHECKED;
import static com.itheima.constant.RedisMessageConstant.SENDTYPE_LOGIN;

@RestController
@RequestMapping("/member")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Reference
    private MemberService memberService;

    @Autowired
    private JedisPool jedisPool;

    /**
     * 会员登录的方法（手机号快速登录）
     *
     * @param response 响应对象
     * @param map      登录信息映射
     * @return 结果
     */
    @RequestMapping("/login")
    public Result login(HttpServletResponse response, @RequestBody Map<String, Object> map) {
        //1. 进行验证码校验
        //1.1 获取前端传递过来的手机号、验证码
        String telephone = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");
        //1.2 获取存储在Redis当中的验证码
        String codeKey = SENDTYPE_LOGIN + telephone;
        String redisCode = jedisPool.getResource().get(codeKey);
        if (validateCode == null || !validateCode.equals(redisCode)) {
            return new Result(false, VALIDATECODE_ERROR);
        }

        //2. 判断当前用户是否是会员
        Member member = memberService.findByTelephone(telephone);
        if (member == null) {
            //2.1 当前用户未注册，自动注册
            member = new Member();
            member.setPhoneNumber(telephone);
            member.setRegTime(new Date());
            memberService.add(member);
        }
        logger.debug("用户信息为：[{}]", member);

        //3. 存储用户登录信息
        //3.1 存入Redis中
        String checkedKey = SENDTYPE_CHECKED + telephone;
        String memberJSON = JSON.toJSON(member).toString();
        jedisPool.getResource().setex(checkedKey, 60 * 30, memberJSON);
        //3.2 写入Cookie，跟踪用户
        Cookie cookie = new Cookie("login_member_telephone", telephone);
        cookie.setPath("/");//路径
        cookie.setMaxAge(60 * 60 * 24 * 30);//有效期30天
        response.addCookie(cookie);

        //4. 返回结果信息
        return new Result(true, LOGIN_SUCCESS);
    }
}

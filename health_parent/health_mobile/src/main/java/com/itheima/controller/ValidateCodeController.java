package com.itheima.controller;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.entity.Result;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import static com.itheima.constant.MessageConstant.SEND_VALIDATECODE_FAIL;
import static com.itheima.constant.MessageConstant.SEND_VALIDATECODE_SUCCESS;
import static com.itheima.constant.RedisMessageConstant.SENDTYPE_LOGIN;
import static com.itheima.constant.RedisMessageConstant.SENDTYPE_ORDER;

/**
 * 验证码管理
 */
@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    private static final Logger logger = LoggerFactory.getLogger(ValidateCodeController.class);

    @Autowired
    private JedisPool jedisPool;

    /**
     * 发送体检预约验证码
     *
     * @param telephone 手机号
     * @return 结果
     */
    @RequestMapping("/send4Order")
    public Result send4Order(String telephone) {
        try {
            //1. 获取4位整数验证码
            Integer code = ValidateCodeUtils.generateValidateCode(4);
            //2. 发送短信验证码
            //SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone, code.toString());
            logger.debug("体检预约验证码为：[{}]", code);
            //3. 往Redis中存储验证码，此处设置过期时间为 5分钟
            jedisPool.getResource().setex(SENDTYPE_ORDER + telephone, 5 * 60, code.toString());
            //4. 返回结果
            return new Result(true, SEND_VALIDATECODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, SEND_VALIDATECODE_FAIL);
        }
    }

    /**
     * 登录校验发送验证码
     * @param telephone 用户手机号
     * @return 结果
     */
    @RequestMapping("/send4Login")
    public Result send4Login(String telephone) {
        try {
            //1. 获取6位整数验证码
            Integer code = ValidateCodeUtils.generateValidateCode(6);
            //2. 发送短信验证码
            //SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone, code.toString());
            logger.debug("手机快速登录验证码为：[{}]", code);
            //3. 往Redis中存储验证码，此处设置过期时间为 5分钟
            jedisPool.getResource().setex(SENDTYPE_LOGIN + telephone, 5 * 60, code.toString());
            //4. 返回结果
            return new Result(true, SEND_VALIDATECODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, SEND_VALIDATECODE_FAIL);
        }
    }
}

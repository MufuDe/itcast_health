package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.itheima.entity.Result;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import com.itheima.utils.SMSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

import static com.itheima.constant.MessageConstant.*;
import static com.itheima.constant.RedisMessageConstant.SENDTYPE_ORDER;
import static com.itheima.pojo.Order.ORDERTYPE_WEIXIN;
import static com.itheima.utils.SMSUtils.ORDER_NOTICE;

/**
 * 体检预约管理
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Reference
    private OrderService orderService;

    @Autowired
    private JedisPool jedisPool;

    /**
     * 体检预约
     *
     * @param map 提交信息
     * @return 结果
     */
    @RequestMapping("/submit")
    public Result submitOrder(@RequestBody Map<String, Object> map) {
        //1. 校验验证码
        //1.1 获取传入手机号
        String telephone = (String) map.get("telephone");
        //1.2 通过手机号获取Redis中存储的验证码
        String key = SENDTYPE_ORDER + telephone;
        String redisCode = jedisPool.getResource().get(key);
        //1.3 获取传入的验证码
        String validateCode = (String) map.get("validateCode");
        //1.4 校验验证码
        if (validateCode == null || !validateCode.equals(redisCode)) {
            // 比对不成功的情形，直接返回错误结果
            return new Result(false, VALIDATECODE_ERROR);
        }

        //2. 调用体检预约服务
        Result result = null;
        try {
            //2.1 设置预约类型为 微信预约[√] | 电话预约
            map.put("orderType", ORDERTYPE_WEIXIN);
            //2.2 远程调用服务实现预约服务
            result = orderService.order(map);
        } catch (Exception e) {
            e.printStackTrace();
            // 预约失败
            return result;
        }

        //3. 预约成功，发送短信通知
        if (result.isFlag()) {
            //3.1 获取预约日期数据
            String orderDate = (String) map.get("orderDate");
            try {
                //3.2 向用户发送预约成功验证码
                SMSUtils.sendShortMessage(ORDER_NOTICE, telephone, orderDate);
            } catch (ClientException e) {
                logger.debug("向用户[{}]发送预约成功短信失败！", telephone);
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 跟据预约ID查询相关信息
     *
     * @param id 预约ID
     * @return 结果
     */
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        try {
            Map<String, Object> orderInfo = orderService.findById(id);
            return new Result(true, QUERY_ORDER_SUCCESS, orderInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, QUERY_ORDER_FAIL);
        }
    }
}

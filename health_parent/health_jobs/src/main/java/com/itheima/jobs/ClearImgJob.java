package com.itheima.jobs;

import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Set;

import static com.itheima.constant.RedisConstant.SETMEAL_PIC_DB_RESOURCES;
import static com.itheima.constant.RedisConstant.SETMEAL_PIC_RESOURCES;

/**
 * 自定义Job，实现定时清理垃圾图片
 */
public class ClearImgJob {

    @Autowired
    private JedisPool jedisPool;

    public void clearImg() {
        //1. 根据Redis中保存的两个set集合进行差值计算，获得垃圾图片名称集合
        Set<String> set = jedisPool.getResource().sdiff(SETMEAL_PIC_RESOURCES, SETMEAL_PIC_DB_RESOURCES);
        if (set != null) {
            for (String picName : set) {
                //2. 删除七牛云服务器上的图片
                QiniuUtils.deleteFileFromQiniu(picName);
                //3. 从Redis集合中删除图片名称
                jedisPool.getResource().srem(SETMEAL_PIC_RESOURCES, picName);
            }
        }
    }
}

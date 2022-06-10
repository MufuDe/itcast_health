package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.itheima.constant.MessageConstant.*;
import static com.itheima.constant.RedisConstant.SETMEAL_PIC_RESOURCES;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;

    //使用Jedis服务操作Redis
    @Autowired
    private JedisPool jedisPool;

    /**
     * 上传图片
     *
     * @param imgFile 图片
     * @return 结果
     */
    @RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile) {
        try {
            //1. 获取原始文件名
            String originalFilename = imgFile.getOriginalFilename();
            assert originalFilename != null;
            int index = originalFilename.lastIndexOf(".");
            String suffix = originalFilename.substring(index - 1);
            //2. 拼接新的文件名
            String fileName = UUID.randomUUID().toString() + suffix;
            //3. 上传文件
            QiniuUtils.upload2Qiniu(imgFile.getBytes(), fileName);
            //3.1 将文件名添加到Redis上传图片Set集合中
            jedisPool.getResource().sadd(SETMEAL_PIC_RESOURCES, fileName);
            //4. 上传成功，回传fileName
            return new Result(true, PIC_UPLOAD_SUCCESS, fileName);
        } catch (IOException e) {
            e.printStackTrace();
            //5. 上传失败
            return new Result(false, PIC_UPLOAD_FAIL);
        }
    }

    /**
     * 新增套餐
     *
     * @param setmeal       套餐
     * @param checkgroupIds 检查组IDS
     * @return 结果
     */
    @PreAuthorize("hasAuthority('SETMEAL_ADD')")
    @RequestMapping("/add")
    public Result add(@RequestBody Setmeal setmeal, Integer[] checkgroupIds) {
        try {
            setmealService.add(setmeal, checkgroupIds);
            return new Result(true, ADD_SETMEAL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, ADD_SETMEAL_FAIL);
        }
    }

    /**
     * 分页查询
     *
     * @param queryPageBean 分页信息
     * @return 结果
     */
    @PreAuthorize("hasAuthority('SETMEAL_QUERY')")
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        return setmealService.pageQuery(queryPageBean);
    }

    /**
     * 跟据ID查询检查套餐
     *
     * @param id 检查套餐ID
     * @return 结果
     */
    @PreAuthorize("hasAuthority('SETMEAL_QUERY')")
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        try {
            Setmeal setmeal = setmealService.findById(id);
            return new Result(true, QUERY_SETMEAL_SUCCESS, setmeal);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, QUERY_SETMEAL_FAIL);
        }
    }

    /**
     * 通过套餐ID查询检查组IDS
     *
     * @param id 套餐ID
     * @return 结果
     */
    @PreAuthorize("hasAuthority('SETMEAL_QUERY')")
    @RequestMapping("/findCheckGroupIdsBySetmealId")
    public Result findCheckGroupIdsBySetmealId(Integer id) {
        try {
            List<Integer> checkgroupIds = setmealService.findCheckItemIdsByCheckGroupId(id);
            return new Result(true, QUERY_CHECKGROUP_SUCCESS, checkgroupIds);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, QUERY_CHECKGROUP_FAIL);
        }
    }

    /**
     * 编辑检查套餐
     *
     * @param setmeal       套餐
     * @param checkgroupIds 检查组IDS
     * @return 结果
     */
    @PreAuthorize("hasAuthority('SETMEAL_EDIT')")
    @RequestMapping("/edit")
    public Result edit(@RequestBody Setmeal setmeal, Integer[] checkgroupIds) {
        try {
            setmealService.edit(setmeal, checkgroupIds);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, EDIT_SETMEAL_FAIL);
        }
        return new Result(true, EDIT_SETMEAL_SUCCESS);
    }

    /**
     * 跟据套餐ID删除
     *
     * @param id 套餐ID
     * @return 结果
     */
    @PreAuthorize("hasAuthority('SETMEAL_DELETE')")
    @RequestMapping("/deleteById")
    public Result deleteById(Integer id) {
        try {
            setmealService.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, DELETE_SETMEAL_FAIL);
        }
        return new Result(true, DELETE_SETMEAL_SUCCESS);
    }
}

package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.itheima.constant.MessageConstant.*;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;

    /**
     * 查询套餐列表
     *
     * @return 返回结果
     */
    @RequestMapping("/getAllSetmeal")
    public Result getAllSetmeal() {
        try {
            //1. 查询套餐列表
            List<Setmeal> setmealList = setmealService.findAll();
            return new Result(true, QUERY_SETMEALLIST_SUCCESS, setmealList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, QUERY_SETMEALLIST_FAIL);
        }
    }

    /**
     * 查询套餐信息：包含检查组、检查项信息
     *
     * @param id 套餐ID
     * @return 结果
     */
    @RequestMapping("/findSetmealById")
    public Result findById(Integer id) {
        try {
            Setmeal setmeal = setmealService.findSetmealById(id);
            return new Result(true, QUERY_SETMEAL_SUCCESS, setmeal);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, QUERY_SETMEAL_FAIL);
        }
    }
}

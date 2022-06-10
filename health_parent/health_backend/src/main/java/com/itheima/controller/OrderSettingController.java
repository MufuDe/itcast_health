package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.entity.Result;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import com.itheima.utils.POIUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.itheima.constant.MessageConstant.*;

/**
 * 预约设置管理
 */
@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {

    @Reference
    private OrderSettingService orderSettingService;

    /**
     * 文件上传，实现预约设置数据导入
     */
    @PreAuthorize("hasAuthority('ORDERSETTING')")
    @RequestMapping("/upload")
    public Result upload(@RequestParam("excelFile") MultipartFile excelFile) {
        try {
            //1. 解析文件
            List<String[]> list = POIUtils.readExcel(excelFile);
            //2. 将数据封装成OrderSetting集合
            List<OrderSetting> orderSettingList = new ArrayList<>();
            for (String[] strings : list) {
                String orderDate = strings[0];
                String orderNum = strings[1];
                orderSettingList.add(new OrderSetting(new Date(orderDate), Integer.parseInt(orderNum)));
            }
            //3. 添加预约信息至数据库
            orderSettingService.add(orderSettingList);
            return new Result(true, IMPORT_ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, IMPORT_ORDERSETTING_FAIL);
        }
    }

    /**
     * 根据日期查询预约设置数据
     *
     * @param date 日期
     * @return 查询结果
     */
    @RequestMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String date) { //此处接收到的格式为 yyyy-MM
        try {
            //页面所需数据：{ date: 1, number: 120, reservations: 1 }
            List<Map<String, Integer>> list = orderSettingService.getOrderSettingByMonth(date);
            return new Result(true, GET_ORDERSETTING_SUCCESS, list);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, GET_ORDERSETTING_FAIL);
        }
    }

    /**
     * 根据指定日期修改可预约人数
     *
     * @param orderSetting 预约设置信息
     * @return 结果
     */
    @PreAuthorize("hasAuthority('ORDERSETTING')")
    @RequestMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting) {
        try {
            orderSettingService.editNumberByDate(orderSetting);
            return new Result(true, ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, ORDERSETTING_FAIL);
        }
    }
}

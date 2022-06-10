package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Override
    public void add(List<OrderSetting> orderSettingList) {
        if (orderSettingList != null && orderSettingList.size() > 0) {
            for (OrderSetting orderSetting : orderSettingList) {
                //1. 判断当前日期是否已经设置
                long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                if (count > 0) {
                    //1.1 已存在，执行更新操作
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                } else {
                    //1.2 不存在，执行插入操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }

    @Override
    public List<Map<String, Integer>> getOrderSettingByMonth(String date) { //date 格式 yyyy-MM
        //1. 拼接首尾日期用于查询
        String dateBegin = date + "-1";
        String dateEnd = DateUtils.getLastDayOfMonth(date);
        Map<String, String> map = new HashMap<>();
        map.put("dateBegin", dateBegin);
        map.put("dateEnd", dateEnd);
        List<OrderSetting> orderSettingList = orderSettingDao.getOrderSettingByMonth(map);
        //2. 将查询出的预约数据封装到前端所需返回值内
        List<Map<String, Integer>> orderSettingListForDate = new ArrayList<>();
        for (OrderSetting orderSetting : orderSettingList) {
            //2.1 封装所需的数据格式 { date: 1, number: 120, reservations: 1 }
            Map<String, Integer> orderSettingMap = new HashMap<>();
            orderSettingMap.put("date", orderSetting.getOrderDate().getDate());
            orderSettingMap.put("number", orderSetting.getNumber());
            orderSettingMap.put("reservations", orderSetting.getReservations());
            //2.2 添加到需返回的集合中
            orderSettingListForDate.add(orderSettingMap);
        }
        return orderSettingListForDate;
    }

    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        //1. 判断是否已经有过设置
        long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if (count > 0) {
            //2. 有过设置则进行更新
            orderSettingDao.editNumberByOrderDate(orderSetting);
        } else {
            //3. 未有设置则插入数据
            orderSettingDao.add(orderSetting);
        }
    }
}

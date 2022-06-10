package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderSettingDao;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderService;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.itheima.constant.MessageConstant.*;

@Component
@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Autowired
    private MemberDao memberDao;

    @Override
    public Result order(Map<String, Object> map) throws Exception {
        //1. 检查当前日期是否进行了预约设置
        String orderDate = (String) map.get("orderDate");
        Date date = DateUtils.parseString2Date(orderDate);
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(date);
        if (orderSetting == null) {
            //1.1 所选日期暂未设置，不能预约
            return new Result(false, SELECTED_DATE_CANNOT_ORDER);
        }

        //2. 检查当前日期是否预约满
        int number = orderSetting.getNumber(); //可预约人数
        int reservations = orderSetting.getReservations(); //已预约人数
        if (reservations >= number) {
            //2.1 已预约满，不可预约
            return new Result(false, ORDER_FULL);
        }

        //3. 检查当前用户是否为注册用户
        String telephone = (String) map.get("telephone");
        Member member = memberDao.findByTelephone(telephone);
        if (member != null) {
            //3.1 判断当前用户是否已经有过预约
            Integer memberId = member.getId();
            int setmealId = Integer.parseInt((String) map.get("setmealId"));
            Order order = new Order(memberId, date, null, null, setmealId);
            //3.2 跟据条件查询
            List<Order> list = orderDao.findByCondition(order);
            if (list != null && list.size() > 0) {
                // 已有预约，不能重复预约
                return new Result(false, HAS_ORDERED);
            }
        }

        //4. 可以预约
        orderSetting.setReservations(reservations + 1);
        //4.1 提交预约
        orderSettingDao.editReservationsByOrderDate(orderSetting);

        //5. 注册当前用户为会员
        if (member == null) {
            //5.1 当前用户不是会员，需要添加到会员表
            member = new Member();
            member.setName((String) map.get("name"));
            member.setPhoneNumber(telephone);
            member.setIdCard((String) map.get("idCard"));
            member.setSex((String) map.get("sex"));
            member.setRegTime(new Date());
            memberDao.add(member);
        }

        //6. 保存预约信息到预约表
        Order order = new Order(member.getId(),
                date,
                (String) map.get("orderType"),
                Order.ORDERSTATUS_NO,
                Integer.parseInt((String) map.get("setmealId")));
        orderDao.add(order);

        return new Result(true, ORDER_SUCCESS, order.getId());
    }

    @Override
    public Map<String, Object> findById(Integer id) throws Exception {
        //1. 查询预约信息
        Map<String, Object> orderInfo = orderDao.findById4Detail(id);
        if (orderInfo != null) {
            //2. 调整日期格式
            Date orderDate = (Date) orderInfo.get("orderDate");
            orderInfo.put("orderDate", DateUtils.parseDate2String(orderDate));
        }
        return orderInfo;
    }
}

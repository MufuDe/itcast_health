package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.service.ReportService;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Service(interfaceClass = ReportService.class)
@Transactional
public class ReportServiceImpl implements ReportService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    @Override
    public Map<String, Object> getBusinessReport() throws Exception {
        //1. 获得当前日期
        String today = DateUtils.parseDate2String(DateUtils.getToday());
        //2. 获得本周一的日期
        String thisWeekMonday = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());
        //3. 获得本月第一天的日期
        String firstDay4ThisMonth = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());

        //4. 今日新增会员数
        Integer todayNewMember = memberDao.findMemberCountByDate(today);

        //5. 总会员数
        Integer totalMember = memberDao.findMemberTotalCount();

        //6. 本周新增会员数
        Integer thisWeekNewMember = memberDao.findMemberCountAfterDate(thisWeekMonday);

        //7. 本月新增会员数
        Integer thisMonthNewMember = memberDao.findMemberCountAfterDate(firstDay4ThisMonth);

        //8. 今日预约数
        Integer todayOrderNumber = orderDao.findOrderCountByDate(today);

        //9. 本周预约数
        Integer thisWeekOrderNumber = orderDao.findOrderCountAfterDate(thisWeekMonday);

        //10. 本月预约数
        Integer thisMonthOrderNumber = orderDao.findOrderCountAfterDate(firstDay4ThisMonth);

        //11. 今日到诊数
        Integer todayVisitsNumber = orderDao.findVisitsCountByDate(today);

        //12. 本周到诊数
        Integer thisWeekVisitsNumber = orderDao.findVisitsCountAfterDate(thisWeekMonday);

        //13. 本月到诊数
        Integer thisMonthVisitsNumber = orderDao.findVisitsCountAfterDate(firstDay4ThisMonth);

        //14. 热门套餐（取前4）
        List<Map<String, Object>> hotSetmeal = orderDao.findHotSetmeal();

        Map<String, Object> result = new HashMap<>();
        result.put("reportDate", today);
        result.put("todayNewMember", todayNewMember);
        result.put("totalMember", totalMember);
        result.put("thisWeekNewMember", thisWeekNewMember);
        result.put("thisMonthNewMember", thisMonthNewMember);
        result.put("todayOrderNumber", todayOrderNumber);
        result.put("thisWeekOrderNumber", thisWeekOrderNumber);
        result.put("thisMonthOrderNumber", thisMonthOrderNumber);
        result.put("todayVisitsNumber", todayVisitsNumber);
        result.put("thisWeekVisitsNumber", thisWeekVisitsNumber);
        result.put("thisMonthVisitsNumber", thisMonthVisitsNumber);
        result.put("hotSetmeal", hotSetmeal);

        return result;
    }

    //查询运营数据
    public Map<String, Object> getBusinessReportData() throws Exception {
        /*
            reportData:{
                    reportDate:null,
                    todayNewMember :0,
                    totalMember :0,
                    thisWeekNewMember :0,
                    thisMonthNewMember :0,
                    todayOrderNumber :0,
                    todayVisitsNumber :0,
                    thisWeekOrderNumber :0,
                    thisWeekVisitsNumber :0,
                    thisMonthOrderNumber :0,
                    thisMonthVisitsNumber :0,
                    hotSetmeal :[
                        {name:'阳光爸妈升级肿瘤12项筛查（男女单人）体检套餐',setmeal_count:200,proportion:0.222},
                        {name:'阳光爸妈升级肿瘤12项筛查体检套餐',setmeal_count:200,proportion:0.222}
                    ]
                }
        */
        //报表日期
        String today = DateUtils.parseDate2String(DateUtils.getToday());
        //获得本周一的日期
        String thisWeekMonday = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());
        //获得本月第一天的日期
        String firstDay4ThisMonth = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());

        //本日新增会员数
        Integer todayNewMember = memberDao.findMemberCountByDate(today);
        //总会员数
        Integer totalMember = memberDao.findMemberTotalCount();
        //本周新增会员数
        Integer thisWeekNewMember = memberDao.findMemberCountAfterDate(thisWeekMonday);
        //本月新增会员数
        Integer thisMonthNewMember = memberDao.findMemberCountAfterDate(firstDay4ThisMonth);
        //今日预约数
        Integer todayOrderNumber = orderDao.findOrderCountByDate(today);
        //本周预约数
        Integer thisWeekOrderNumber = orderDao.findOrderCountAfterDate(thisWeekMonday);
        //本月预约数
        Integer thisMonthOrderNumber = orderDao.findOrderCountAfterDate(firstDay4ThisMonth);
        //今日到诊数
        Integer todayVisitsNumber = orderDao.findVisitsCountByDate(today);
        //本周到诊数
        Integer thisWeekVisitsNumber = orderDao.findVisitsCountAfterDate(thisWeekMonday);
        //本月到诊数
        Integer thisMonthVisitsNumber = orderDao.findVisitsCountAfterDate(firstDay4ThisMonth);

        //热门套餐（取前4）
        List<Map<String, Object>> hotSetmeal = orderDao.findHotSetmeal();

        Map<String,Object> data = new HashMap<>();
        data.put("reportDate",today);
        data.put("todayNewMember",todayNewMember);
        data.put("totalMember",totalMember);
        data.put("thisWeekNewMember",thisWeekNewMember);
        data.put("thisMonthNewMember",thisMonthNewMember);
        data.put("todayOrderNumber",todayOrderNumber);
        data.put("thisWeekOrderNumber",thisWeekOrderNumber);
        data.put("thisMonthOrderNumber",thisMonthOrderNumber);
        data.put("todayVisitsNumber",todayVisitsNumber);
        data.put("thisWeekVisitsNumber",thisWeekVisitsNumber);
        data.put("thisMonthVisitsNumber",thisMonthVisitsNumber);
        data.put("hotSetmeal",hotSetmeal);

        return data;
    }
}

package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import com.itheima.utils.DateUtils;
import com.itheima.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;

    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    @Override
    public void add(Member member) {
        //1. 获取用户密码信息
        String password = member.getPassword();
        if (password != null) {
            //1.1 使用md5将明文密码加密
            password = MD5Utils.md5(password);
            member.setPassword(password);
        }
        memberDao.add(member);
    }

    @Override
    public List<Integer> findMemberCountByMonth(List<String> yearMonthList) {
        //1. 通过年月信息获取会员统计数量
        List<Integer> memberCountList = new ArrayList<>();
        for (String yearMonth : yearMonthList) {
            //1.1 转换年月格式，此处传入格式为 yyyy.MM 转换为 yyyy-MM
            yearMonth = yearMonth.replace('.', '-');
            yearMonth = DateUtils.getLastDayOfMonth(yearMonth); //yyyy-MM-dd
            //1.2 查询截止指定月份的会员统计数量
            Integer memberCountTillMonth = memberDao.findMemberCountBeforeDate(yearMonth);
            memberCountList.add(memberCountTillMonth);
        }

        //2. 返回查询结果
        return memberCountList;
    }
}

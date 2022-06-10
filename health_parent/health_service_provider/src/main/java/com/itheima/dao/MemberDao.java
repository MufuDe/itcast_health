package com.itheima.dao;

import com.itheima.pojo.Member;

public interface MemberDao {
    Member findByTelephone(String telephone);

    void add(Member member);

    Integer findMemberCountBeforeDate(String yearMonth);

    Integer findMemberCountByDate(String today);

    Integer findMemberTotalCount();

    Integer findMemberCountAfterDate(String thisWeekMonday);
}

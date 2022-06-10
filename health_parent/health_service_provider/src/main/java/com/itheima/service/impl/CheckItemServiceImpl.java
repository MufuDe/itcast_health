package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 检查项服务
 */
@Component
@Service(interfaceClass = CheckItemService.class) //必须明确当前服务实现的是哪个接口
@Transactional
public class CheckItemServiceImpl implements CheckItemService {

    private Logger logger = LoggerFactory.getLogger(CheckItemServiceImpl.class);

    @Autowired
    private CheckItemDao checkItemDao;

    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString(); //查询条件
        //完成分页查询，基于MyBatis分页查询助手插件完成
        PageHelper.startPage(currentPage, pageSize); //基于ThreadLocal实现拼接Limit条件
        Page<CheckItem> checkItemPage = checkItemDao.selectByCondition(queryString);
        long total = checkItemPage.getTotal();
        List<CheckItem> rows = checkItemPage.getResult();
        return new PageResult(total, rows);
    }

    @Override
    public void deleteById(Integer id) {
        //1. 判断当前检查项是否已经关联检查组
        long count = checkItemDao.findCountByCheckItemId(id);
        if (count > 0) {
            //1.1 当前检查项已经关联检查组
            throw new RuntimeException("当前检查项已经关联检查组!");
        }
        checkItemDao.deleteById(id);
    }

    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    @Override
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }
}

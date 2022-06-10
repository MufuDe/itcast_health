package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    private CheckGroupDao checkGroupDao;

    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //1. 新增检查组，操作t_checkgroup表
        checkGroupDao.add(checkGroup);
        //2. 设置检查项和检查组的多对多关系，操作t_checkgroup_checkitem表
        Integer checkGroupId = checkGroup.getId(); //注意：此处ID值是我们使用selectKey标签获取的，具体可看Dao的实现
        CheckGroupServiceImpl proxy = (CheckGroupServiceImpl) AopContext.currentProxy();
        proxy.buildAssociation(checkitemIds, checkGroupId);
    }

    /**
     * 用于建立检查项检查组关系的方法
     *
     * @param checkitemIds 检查项IDS
     * @param checkGroupId 检查组ID
     */
    public void buildAssociation(Integer[] checkitemIds, Integer checkGroupId) {
        if (checkitemIds != null && checkitemIds.length > 0) {
            for (Integer checkitemId : checkitemIds) {
                Map<String, Integer> idMap = new HashMap<>();
                idMap.put("checkgroupId", checkGroupId);
                idMap.put("checkitemId", checkitemId);
                checkGroupDao.setCheckGroupAndCheckItem(idMap);
            }
        }
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        //1. 设置分页查询的数据
        PageHelper.startPage(currentPage, pageSize);
        //2. 进行分页查询
        Page<CheckGroup> checkGroupPage = checkGroupDao.selectByCondition(queryString);
        //3. 取出结果数据，进行PageResult封装
        long total = checkGroupPage.getTotal();
        List<CheckGroup> rows = checkGroupPage.getResult();
        return new PageResult(total, rows);
    }

    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        //1. 编辑检查组，操作t_checkgroup表
        checkGroupDao.edit(checkGroup);
        //2. 设置检查项和检查组的多对多关系，操作t_checkgroup_checkitem表
        Integer checkGroupId = checkGroup.getId();
        checkGroupDao.deleteAssociation(checkGroupId); //删除已有的关联信息
        CheckGroupServiceImpl proxy = (CheckGroupServiceImpl) AopContext.currentProxy();
        proxy.buildAssociation(checkitemIds, checkGroupId);
    }


    @Override
    public void deleteById(Integer id) {
        //1. 判断当前检查组是否已经关联检查套餐
        long count = checkGroupDao.findCountByCheckItemId(id);
        if (count > 0) {
            //1.1 当前检查组已经关联检查套餐
            throw new RuntimeException("当前检查组已经关联检查套餐!");
        }
        //1. 删除检查项检查组关联信息
        checkGroupDao.deleteAssociation(id);
        //2. 删除检查项信息
        checkGroupDao.deleteById(id);
    }

    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }
}

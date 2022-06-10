package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealService {
    void add(Setmeal setmeal, Integer[] checkgroupIds);

    PageResult pageQuery(QueryPageBean queryPageBean);

    Setmeal findById(Integer id);

    Setmeal findSetmealById(Integer id);

    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    void edit(Setmeal setmeal, Integer[] checkgroupIds);

    void deleteById(Integer id);

    List<Setmeal> findAll();

    List<Map<String, Object>> findSetmealCount();
}

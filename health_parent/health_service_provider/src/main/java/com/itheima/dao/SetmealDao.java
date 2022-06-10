package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealDao {
    public void add(Setmeal setmeal) ;

    void setSetmealAndCheckGroup(Map<String, Integer> map);

    Page<Setmeal> selectByCondition(String queryString);

    Setmeal findById(Integer id);

    Setmeal findSetmealById(Integer id);

    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    void edit(Setmeal setmeal);

    void deleteCheckGroupIdsBySetmealId(Integer setmealId);

    void deleteById(Integer setmealId);

    List<Setmeal> findAll();

    List<Map<String, Object>> findSetmealCount();
}

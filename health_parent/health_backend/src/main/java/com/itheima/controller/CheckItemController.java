package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.itheima.constant.MessageConstant.*;

/**
 * 检查项管理
 */
@RestController
@RequestMapping("/checkitem")
public class CheckItemController {

    @Reference //@Reference查找服务
    private CheckItemService checkItemService;

    /**
     * 新增检查项
     *
     * @return 新增结果
     */
    @PreAuthorize("hasAuthority('CHECKITEM_ADD')")
    @RequestMapping("/add")
    public Result add(@RequestBody CheckItem checkItem) { //@RequestBody解析JSON数据，封装到Java对象
        try {
            checkItemService.add(checkItem);
        } catch (Exception e) {
            e.printStackTrace();
            //服务调用失败
            return new Result(false, ADD_CHECKITEM_FAIL);
        }
        return new Result(true, ADD_CHECKITEM_SUCCESS);
    }

    /**
     * 检查项分页查询
     *
     * @param queryPageBean 分页查询信息（页码、显示条数、查询条件）
     * @return 检查项页面结果
     */
    @PreAuthorize("hasAuthority('CHECKITEM_QUERY')")
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        return checkItemService.pageQuery(queryPageBean);
    }

    /**
     * 跟据ID删除检查项
     *
     * @param id 检查项ID
     * @return 删除结果
     */
    @PreAuthorize("hasAuthority('CHECKITEM_DELETE')")
    @RequestMapping("/delete")
    public Result delete(Integer id) {
        try {
            checkItemService.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, DELETE_CHECKITEM_FAIL);
        }

        return new Result(true, DELETE_CHECKITEM_SUCCESS);
    }

    /**
     * 跟据检查项ID查询
     *
     * @param id 检查项ID
     * @return 检查项结果
     */
    @PreAuthorize("hasAuthority('CHECKITEM_QUERY')")
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        try {
            CheckItem checkItem = checkItemService.findById(id);
            return new Result(true, QUERY_CHECKITEM_SUCCESS, checkItem);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, QUERY_CHECKITEM_FAIL);
        }
    }

    /**
     * 编辑检查项
     *
     * @param checkItem 检查项信息
     * @return 编辑结果
     */
    @PreAuthorize("hasAuthority('CHECKITEM_EDIT')")
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckItem checkItem) {
        try {
            checkItemService.edit(checkItem);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, EDIT_CHECKITEM_FAIL);
        }

        return new Result(true, EDIT_CHECKITEM_SUCCESS);
    }

    /**
     * 查询所有检查项
     *
     * @return 检查项查询结果
     */
    @PreAuthorize("hasAuthority('CHECKITEM_QUERY')")
    @RequestMapping("/findAll")
    public Result findAll() {
        try {
            List<CheckItem> checkItemList = checkItemService.findAll();
            return new Result(true, QUERY_CHECKITEM_SUCCESS, checkItemList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, QUERY_CHECKITEM_FAIL);
        }
    }
}

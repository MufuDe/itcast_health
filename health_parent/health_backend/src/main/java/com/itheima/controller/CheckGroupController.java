package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.itheima.constant.MessageConstant.*;

/**
 * 检查组管理
 */
@RestController
@RequestMapping("/checkgroup")
public class CheckGroupController {

    @Reference
    private CheckGroupService checkGroupService;

    /**
     * 新增检查组
     *
     * @param checkGroup 检查组信息
     * @return 新增结果
     */
    @PreAuthorize("hasAuthority('CHECKGROUP_ADD')")
    @RequestMapping("/add")
    public Result add(@RequestBody CheckGroup checkGroup, Integer[] checkitemIds) {
        try {
            checkGroupService.add(checkGroup, checkitemIds);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, ADD_CHECKGROUP_FAIL);
        }
        return new Result(true, ADD_CHECKGROUP_SUCCESS);
    }

    /**
     * 分页查询
     *
     * @param queryPageBean 分页信息
     * @return 分页数据
     */
    @PreAuthorize("hasAuthority('CHECKGROUP_QUERY')")
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        return checkGroupService.pageQuery(queryPageBean);
    }

    /**
     * 跟据ID查询检查组
     *
     * @param id 检查组ID
     * @return 查询结果
     */
    @PreAuthorize("hasAuthority('CHECKGROUP_QUERY')")
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        try {
            CheckGroup checkGroup = checkGroupService.findById(id);
            return new Result(true, QUERY_CHECKGROUP_SUCCESS, checkGroup);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, QUERY_CHECKGROUP_FAIL);
        }
    }


    /**
     * 跟据检查组ID查询检查项IDS
     *
     * @param id 检查组ID
     * @return 查询结果
     */
    @PreAuthorize("hasAuthority('CHECKGROUP_QUERY')")
    @RequestMapping("/findCheckItemIdsByCheckGroupId")
    public Result findCheckItemIdsByCheckGroupId(Integer id) {
        try {
            List<Integer> checkitemIds = checkGroupService.findCheckItemIdsByCheckGroupId(id);
            return new Result(true, QUERY_CHECKITEM_SUCCESS, checkitemIds);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, QUERY_CHECKITEM_FAIL);
        }
    }

    /**
     * 编辑检查组信息
     *
     * @param checkGroup   检查组对象
     * @param checkitemIds 检查项IDS
     * @return 编辑结果
     */
    @PreAuthorize("hasAuthority('CHECKGROUP_EDIT')")
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckGroup checkGroup, Integer[] checkitemIds) {
        try {
            checkGroupService.edit(checkGroup, checkitemIds);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, EDIT_CHECKGROUP_FAIL);
        }
        return new Result(true, EDIT_CHECKGROUP_SUCCESS);
    }

    /**
     * 跟据检查项ID删除
     *
     * @param id 检查项ID
     * @return 删除结果
     */
    @PreAuthorize("hasAuthority('CHECKGROUP_DELETE')")
    @RequestMapping("/deleteById")
    public Result deleteById(Integer id) {
        try {
            checkGroupService.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, DELETE_CHECKGROUP_FAIL);
        }
        return new Result(true, DELETE_CHECKGROUP_SUCCESS);
    }

    /**
     * 查询所有检查组信息
     * @return 查询结果
     */
    @PreAuthorize("hasAuthority('CHECKGROUP_QUERY')")
    @RequestMapping("/findAll")
    public Result findAll() {
        try {
            List<CheckGroup> checkGroupList = checkGroupService.findAll();
            return new Result(true, QUERY_CHECKGROUP_SUCCESS, checkGroupList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, QUERY_CHECKGROUP_FAIL);
        }
    }
}

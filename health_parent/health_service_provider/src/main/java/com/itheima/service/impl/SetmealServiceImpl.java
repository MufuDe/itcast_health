package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.SetmealDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.itheima.constant.RedisConstant.SETMEAL_PIC_DB_RESOURCES;

@Component
@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealDao setmealDao;

    @Autowired
    private JedisPool jedisPool;

    /**
     * 用于生成静态页面的类
     */
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    /**
     * 从属性文件读取输出目录的路径
     */
    @Value("${out_put_path}")
    private String OUT_PUT_PATH;

    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        //1. 新增套餐
        setmealDao.add(setmeal);
        //2. 绑定套餐检查组关联关系
        Integer setmealId = setmeal.getId();
        SetmealServiceImpl proxy = (SetmealServiceImpl) AopContext.currentProxy();
        if (setmealId != null && checkgroupIds != null && checkgroupIds.length > 0) {
            proxy.setSetmealAndCheckGroup(setmealId, checkgroupIds);
        }
        //3. 将图片名称保存到Redis集合
        String fileName = setmeal.getImg();
        if (fileName != null && fileName.length() > 0) {
            jedisPool.getResource().sadd(SETMEAL_PIC_DB_RESOURCES, fileName);
        }

        //4. 【生成静态页面】新增套餐后需要重新生成静态页面
        proxy.generateMobileStaticHtml();
    }

    /**
     * 关联套餐检查组关系的方法
     *
     * @param setmealId     套餐ID
     * @param checkgroupIds 检查组IDS
     */
    public void setSetmealAndCheckGroup(Integer setmealId, Integer[] checkgroupIds) {
        for (Integer checkgroupId : checkgroupIds) {
            Map<String, Integer> map = new HashMap<>();
            map.put("setmeal_id", setmealId);
            map.put("checkgroup_id", checkgroupId);
            setmealDao.setSetmealAndCheckGroup(map);
        }
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        //1. 设置分页查询信息
        PageHelper.startPage(currentPage, pageSize);
        //2. 进行分页查询
        Page<Setmeal> setmealPage = setmealDao.selectByCondition(queryString);
        //3. 取出结果，封装PageResult
        long total = setmealPage.getTotal();
        List<Setmeal> rows = setmealPage.getResult();
        return new PageResult(total, rows);
    }

    @Override
    public Setmeal findById(Integer id) {
        return setmealDao.findById(id);
    }

    @Override
    public Setmeal findSetmealById(Integer id) {
        return setmealDao.findSetmealById(id);
    }

    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return setmealDao.findCheckItemIdsByCheckGroupId(id);
    }

    @Override
    public void edit(Setmeal setmeal, Integer[] checkgroupIds) {
        //1. 编辑套餐信息
        setmealDao.edit(setmeal);
        //2. 删除原有检查组IDS
        Integer setmealId = setmeal.getId();
        setmealDao.deleteCheckGroupIdsBySetmealId(setmealId);
        //3. 设置检查套餐和检查组关联关系
        SetmealServiceImpl proxy = (SetmealServiceImpl) AopContext.currentProxy();
        if (setmealId != null && checkgroupIds != null && checkgroupIds.length > 0) {
            proxy.setSetmealAndCheckGroup(setmealId, checkgroupIds);
        }

        //4. 【生成静态页面】新增套餐后需要重新生成静态页面
        proxy.generateMobileStaticHtml();
    }

    @Override
    public void deleteById(Integer setmealId) {
        //1. 删除套餐与检查组的关联关系
        setmealDao.deleteCheckGroupIdsBySetmealId(setmealId);
        //2. 删除套餐信息
        setmealDao.deleteById(setmealId);

        SetmealServiceImpl proxy = (SetmealServiceImpl) AopContext.currentProxy();
        //3. 【生成静态页面】新增套餐后需要重新生成静态页面
        proxy.generateMobileStaticHtml();
    }

    @Override
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    @Override
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }

    /**
     * 生成静态页面
     */
    public void generateMobileStaticHtml() {
        SetmealServiceImpl proxy = (SetmealServiceImpl) AopContext.currentProxy();
        //1. 准备模板文件中所需的数据
        List<Setmeal> setmealList = proxy.findAll();
        //2. 生成 [套餐列表] 静态页面
        proxy.generateMobileSetmealListHtml(setmealList);
        //3. 生成 [套餐详情] 静态页面（多个）
        proxy.generateMobileSetmealDetailHtml(setmealList);
    }

    /**
     * 生成[套餐列表]静态页面
     *
     * @param setmealList 套餐列表
     */
    public void generateMobileSetmealListHtml(List<Setmeal> setmealList) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("setmealList", setmealList);
        SetmealServiceImpl proxy = (SetmealServiceImpl) AopContext.currentProxy();
        proxy.generateHtml("mobile_setmeal.ftl", "m_setmeal.html", dataMap);
    }

    /**
     * 生成[套餐详情]静态页面
     *
     * @param setmealList 套餐列表
     */
    public void generateMobileSetmealDetailHtml(List<Setmeal> setmealList) {
        for (Setmeal setmeal : setmealList) {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("setmeal", setmealDao.findSetmealById(setmeal.getId()));
            SetmealServiceImpl proxy = (SetmealServiceImpl) AopContext.currentProxy();
            proxy.generateHtml("mobile_setmeal_detail.ftl",
                    "setmeal_detail_" + setmeal.getId() + ".html",
                    dataMap);
        }
    }

    /**
     * 生成静态页面
     *
     * @param templateName 模板名称
     * @param htmlPageName 静态页面名称
     * @param dataMap 数据映射
     */
    public void generateHtml(String templateName, String htmlPageName, Map<String, Object> dataMap) {
        //1. 获得配置对象，已在配置文件做好配置
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        Writer out = null;
        try {
            //2. 加载模版文件
            Template template = configuration.getTemplate(templateName);
            //3. 生成数据
            File docFile = new File(OUT_PUT_PATH + "\\" + htmlPageName);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
            //4. 输出文件
            template.process(dataMap, out);
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.flush();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        }
    }
}

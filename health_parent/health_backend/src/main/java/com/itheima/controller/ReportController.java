package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.MemberService;
import com.itheima.service.ReportService;
import com.itheima.service.SetmealService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.itheima.constant.MessageConstant.*;

/**
 * 统计数据管理
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    private MemberService memberService;

    @Reference
    private SetmealService setmealService;

    @Reference
    private ReportService reportService;

    /**
     * 获取每月会员数量信息
     *
     * @return 结果
     */
    @RequestMapping("/getMemberReport")
    public Result getMemberReport() {
        //1. 获取日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -12); //获取当前日期前12月的日期

        //2. 转换日期数据
        List<String> yearMonthList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            calendar.add(Calendar.MONTH, 1); //逐一往前推1个月
            yearMonthList.add(
                    new SimpleDateFormat("yyyy.MM")
                            .format(calendar.getTime())
            );
        }

        //3. 封装 reportMap
        //3.1 封装月份信息
        Map<String, Object> memberReportMap = new HashMap<>();
        memberReportMap.put("months", yearMonthList); //日期格式 yyyy.MM
        //3.2 封装人数信息
        List<Integer> memberCountList = null;
        try {
            // 跟据月份查询会员数量
            memberCountList = memberService.findMemberCountByMonth(yearMonthList);
            memberReportMap.put("memberCount", memberCountList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, GET_MEMBER_NUMBER_REPORT_FAIL);
        }

        //4. 返回结果
        return new Result(true, GET_MEMBER_NUMBER_REPORT_SUCCESS, memberReportMap);
    }

    /**
     * 套餐占比统计
     *
     * @return 结果
     */
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport() {
        //1. 获取套餐数据
        List<Map<String, Object>> setmealMapList = null;
        try {
            setmealMapList = setmealService.findSetmealCount();
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, GET_SETMEAL_COUNT_REPORT_FAIL);
        }

        //2. 封装套餐统计报告数据
        //2.1 封装套餐数量
        Map<String, Object> setmealReportMap = new HashMap<>();
        setmealReportMap.put("setmealCount", setmealMapList);
        //2.2 封装对应套餐名
        List<String> setmealNames = new ArrayList<>();
        for (Map<String, Object> setmealMap : setmealMapList) {
            String name = (String) setmealMap.get("name");
            setmealNames.add(name);
        }
        setmealReportMap.put("setmealNames", setmealNames);

        //3. 返回套餐统计数据
        return new Result(true, GET_SETMEAL_COUNT_REPORT_SUCCESS, setmealReportMap);
    }

    /**
     * 获取运营统计数据
     *
     * @return 结果
     */
    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData() {
        try {
            Map<String, Object> result = reportService.getBusinessReport();
            return new Result(true, GET_BUSINESS_REPORT_SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, GET_BUSINESS_REPORT_FAIL);
        }
    }

    /**
     * 导出Excel报表
     *
     * @param request  请求对象
     * @param response 响应对象
     * @return 结果
     */
    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response) {
        try {
            //1. 远程调用报表服务获取报表数据
            Map<String, Object> result = reportService.getBusinessReport();

            //2. 取出返回结果数据，准备将报表数据写入到Excel文件中
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map<String, Object>> hotSetmeal = (List<Map<String, Object>>) result.get("hotSetmeal");

            //3. 获得Excel模板文件绝对路径
            String temlateRealPath = request.getSession().getServletContext().getRealPath("template") +
                    File.separator + "report_template.xlsx";

            //4. 读取模板文件创建Excel表格对象
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(temlateRealPath)));
            XSSFSheet sheet = workbook.getSheetAt(0);

            XSSFRow row = sheet.getRow(2);
            row.getCell(5).setCellValue(reportDate);//日期

            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//新增会员数（本日）
            row.getCell(7).setCellValue(totalMember);//总会员数

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日到诊数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周到诊数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月到诊数

            int rowNum = 12;
            for (Map<String, Object> map : hotSetmeal) {//热门套餐
                String name = (String) map.get("name");
                Long setmeal_count = (Long) map.get("setmeal_count");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                row = sheet.getRow(rowNum++);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }

            //5. 通过输出流进行文件下载
            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");
            workbook.write(out);

            out.flush();
            out.close();
            workbook.close();

            return null;
        } catch (Exception e) {
            // 返回导出文件异常的结果
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    /**
     * 导出运营数据到pdf并提供客户端下载
     *
     * @param request  请求对象
     * @param response 响应对象
     * @return 结果
     */
    @RequestMapping("/exportBusinessReport4PDF")
    public Result exportBusinessReport4PDF(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> result = reportService.getBusinessReportData();
            //取出返回结果数据，准备将报表数据写入到Excel文件中
            List<Map<String, Object>> hotSetmeal = (List<Map<String, Object>>) result.get("hotSetmeal");
            //动态获取pdf模板文件绝对磁盘路径
            String jrxmlPath = request.getSession().getServletContext().getRealPath("template")
                    + File.separator + "health_business3.jrxml";
            String jasperPath = request.getSession().getServletContext().getRealPath("template")
                    + File.separator + "health_business3.jasper";
            //编译模板
            JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);
            //填充数据---使用JavaBean数据源方式填充
            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(jasperPath, result,
                            new JRBeanCollectionDataSource(hotSetmeal));
            //通过输出流进行文件下载 基于浏览器作为客户端下载
            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/pdf");//代表的是Excel文件类型
            response.setHeader("content-Disposition", "attachment;filename=report.pdf");//指定以附件形式下载
            //输出文件
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }
}

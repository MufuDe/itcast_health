# 传智健康 itcast_health

## 一、项目架构

**1. 技术选型**

前端技术：VUE、ElementUI、AJAX、Bootstrap、HTML5
分布式架构：ZooKeeper、Dubbo、SpringMVC
基本框架：Spring、MyBatis、Spring Security
数据库：MySQL、Redis
定时任务：Quartz
报表：POI、JasperReports、FreeMarker、ECharts

**2. 模块介绍**

health_parent：聚合管理；
health_common：通用模块，包含工具类、实体类、返回结果、常量类等；
health_interface：存放服务接口；
health_service_provider：Dubbo服务模块，该模块实际开发中可以再拆分各个单独服务；
health_backend：健康管理后台；
health_mobile：移动端前台。

**3. 辅助软件**

PowerDesigner：数据库模型构建；
TIBCO Jaspersoft Studio：模板文件工具。

**4. 补充说明**

sql文件夹中的sql文件即为本项目所需的基础数据库表文件。

## 二、快速启动

**1. 默认账户**

| 账户 | 用户名 | 密码 |
| --- | --- | --- |
| 1 | admin | admin |
| 2 | xiaoming | 1234 |

**2. 启动ZooKeeper**

win：zkServer.cmd
Linux：bin/zkServer.sh

注意：本项目默认配置localhost。

**3. 启动Redis**

win：redi-server.exe redis.windows.conf

**4. 项目调试启动顺序**

① health_service_provider
② health_backend & health_mobile
③ health_jobs

## 三、其他

**1. 七牛云**

七牛云官方开发文档：[QiniuCloud Java SDK](https://developer.qiniu.com/kodo/1239/java).

**2. Quartz**

Quartz定时任务调度框架：[Quartz](http://www.quartz-scheduler.org/).

Cron表达式在线生成工具：[Online Cron Tools](https://qqe2.com/cron).

**3. aliyun**

阿里云短信验证：[Aliyun](https://www.aliyun.com/).

**4. FreeMarker**

FreeMarker页面静态化技术[FreeMarker](https://freemarker.apache.org/).

**5. ECharts**

ECharts图表框架[ECharts](https://echarts.apache.org/zh/index.html).

**6. JasperReports**

JasperReports文档模板框架[JasperReports](https://community.jaspersoft.com/project/jasperreports-library).

如果使用JasperReports遇到报错: 
> java.io.ioexception: the byte array is not a recognized imageformat.

可能是项目中外联的图片链接失效了，具体见 <u>health_backend/src/main/webapp/template/health_business3.jrxml</u>.

```xml
<title>
    <band height="79" splitType="Stretch">
        <image>
            <reportElement x="0" y="0" width="130" height="60" uuid="cedbff82-d97a-47ad-ba89-27a4b08e0200"/>
            <imageExpression><![CDATA["https://www.itcast.cn/2018czgw/images/logo2.png"]]></imageExpression>
        </image>
    </band>
</title>
```

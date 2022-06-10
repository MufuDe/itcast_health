##Quick Start

####0. The Account Provided

| user | username | password |
| --- | --- | --- |
| 1 | admin | admin |
| 2 | xiaoming | 1234 |

####1. Startup the ZooKeeper Server

If you are using it in windows, just double click
*<u>zkServer.cmd</u>* to startup.

Else in Linux, just command *<u>bin/zkServer.sh</u>* to startup.

####2. Startup the Redis Server

If you are using it in windows, use the command 
*<u>redis-server.exe redis.windows.conf</u>*.

####3. Startup the health project server

First: *<u>health_service_provider</u>* ;

Second: *<u>health_backend</u>* and *<u>health_mobile</u>*;

Others: *<u>health_jobs</u>* ;

##Others

There are something you might need to know.

####1. QiniuCloud

If you want to know more about QiniuCloud, you can click here
[QiniuCloud Java SDK](https://developer.qiniu.com/kodo/1239/java).

####2. Quartz

If you want to learn more about Quartz, you can click here
[Quartz](http://www.quartz-scheduler.org/).

Besides, you may need to use corn in an easy way.
[Online Cron Tools](https://qqe2.com/cron).

####3. aliyun

Validate code service is provided by [Aliyun](https://www.aliyun.com/).

####4. FreeMarker

Template engine is [FreeMarker](https://freemarker.apache.org/).

####5. ECharts

Graphs are provided by [ECharts](https://echarts.apache.org/zh/index.html).

####6. JasperReports

Output reports are provided by [JasperReports](https://community.jaspersoft.com/project/jasperreports-library).

Problem might happen: 
> java.io.ioexception: the byte array is not a recognized imageformat.

What you need to do is to update the link in <u>health_backend/src/main/webapp/template/health_business3.jrxml</u>.

```
<title>
    <band height="79" splitType="Stretch">
        <image>
            <reportElement x="0" y="0" width="130" height="60" uuid="cedbff82-d97a-47ad-ba89-27a4b08e0200"/>
            <imageExpression><![CDATA["https://www.itcast.cn/2018czgw/images/logo2.png"]]></imageExpression>
        </image>
    </band>
</title>
```
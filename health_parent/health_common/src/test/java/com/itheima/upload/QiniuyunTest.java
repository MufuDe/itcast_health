package com.itheima.upload;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.junit.Test;

public class QiniuyunTest {

    @Test
    public void testUpload() {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2()); //华南地区
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = "DcXzC1Db8JQ2D3miSLeHiQyUGn-BjdEj6_Q-Uxpm";
        String secretKey = "7djXClVVO3HK190H0FGJwMjZ7rSWH2Yom6BEkPID";
        String bucket = "healthstr01";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = "D:\\tmp\\0a3b3288-3446-4420-bbff-f263d0c02d8e.jpg";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = "FjYgor3Gle2zsWZBTLWITjl61FEB";
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }

    @Test
    public void delete() {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2());
        //...其他参数参考类注释
        String accessKey = "DcXzC1Db8JQ2D3miSLeHiQyUGn-BjdEj6_Q-Uxpm";
        String secretKey = "7djXClVVO3HK190H0FGJwMjZ7rSWH2Yom6BEkPID";
        String bucket = "healthstr01";
        String key = "FjYgor3Gle2zsWZBTLWITjl61FEB";
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }
}

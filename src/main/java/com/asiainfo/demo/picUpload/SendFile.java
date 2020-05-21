package com.asiainfo.demo.picUpload;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.util.DigestUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description: 模仿表单上传文件  ，将本地文件上传到文件服务器上
 *
 */
public class SendFile {

    public static void main(String[] args) throws ClientProtocolException,
            IOException {


        doPost2020();


    }

    private static void doPost2020() throws IOException {
        String filepath="D:\\11111.rar";

        //创建加密上传的参数
        Date dateNow = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(dateNow);
        StringBuffer key1 = new StringBuffer();

        key1.append("123456").append("PRIVATEM").append(time);
        String myCode = DigestUtils.md5DigestAsHex(key1.toString().getBytes());

        //接收文件的地址
        String urlStr = "http://img.baidu.com/file/receiveFile.jsp";
        //封装需要上传文件的参数
        Map<String, String> textMap = new HashMap<String, String>();
        textMap.put("origin", "PRIVATE");
        textMap.put("time",  time );
        textMap.put("secure",myCode);


        HttpClient httpclient = new DefaultHttpClient();
        //请求处理页面
        HttpPost httppost = new HttpPost( urlStr );
        //创建待处理的文件
        FileBody file = new FileBody(new File(filepath));


        //对请求的表单域进行填充
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("file", file);

        if(textMap!=null ){
            Iterator it = textMap.entrySet().iterator();
            while(it.hasNext() ){
                Map.Entry entry = (Map.Entry)it.next();
                String key =  (String) entry.getKey();
                String value = (String)entry.getValue();
                //创建待处理的表单域内容文本
                StringBody bodyValue = new StringBody(value);
                reqEntity.addPart(key , bodyValue);
            }
        }

        //设置请求
        httppost.setEntity(reqEntity);
        //执行
        HttpResponse response = httpclient.execute(httppost);

        HttpEntity httpEntity = response.getEntity();
        BufferedReader br = new BufferedReader(new InputStreamReader(httpEntity
                .getContent(), "UTF-8"));
        StringBuffer backData = new StringBuffer();
        String line = null;
        while ((line = br.readLine()) != null) {
            backData.append(line);
        }
        System.out.println(backData.toString()   );
    }
}


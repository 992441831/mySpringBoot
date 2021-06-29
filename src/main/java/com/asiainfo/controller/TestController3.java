package com.asiainfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.*;

/**
 * Created by zhangqq20190306 on 2020/3/12.
 */
@Controller
@RequestMapping("/TestController3")
public class TestController3 {


    @RequestMapping(value = "/testController.do", method = RequestMethod.GET)
    @ResponseBody
    public Object testController(HttpServletRequest request, HttpServletResponse response) {

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("我正在执行：+"+Thread.currentThread().getName());
                    String result = execJar("C:\\Users\\zhangqq\\Desktop\\暂存箱\\TestJar.jar");
                    System.out.println("我执行完成：+"+Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread1.start();
        System.out.println("主线程执行完成");

        return "成功";
    }


    public static String  execJar(String dir){
        Process proc = null;
        StringBuilder sb = new StringBuilder();
        try {
            proc = Runtime.getRuntime().exec("java -Dfile.encoding=utf-8 -jar "+dir);
            InputStream inputStream = proc.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String s = "";
            while ((s = reader.readLine()) != null){
//                System.out.println(s);
                sb.append(s+"\r\n");
                System.out.println("我正在被执行："+s);
            }
            reader.close();
            proc.destroy();
        } catch (Exception e) {
            sb.append(e.getMessage());
        }
        return sb.toString();
    }


}

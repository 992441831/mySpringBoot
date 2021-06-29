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
@RequestMapping("/TestController2")
public class TestController2 {

    static ExecutorService executorService= Executors.newSingleThreadExecutor();

    @RequestMapping(value = "/testController.do", method = RequestMethod.GET)
    @ResponseBody
    public Object testController(HttpServletRequest request, HttpServletResponse response) {

        //System.out.println("线程池大小："+Runtime.getRuntime().availableProcessors()*1);

        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() {
                try {
                    //线程休息5s
                    String result = execJar("C:\\Users\\zhangqq\\Desktop\\暂存箱\\TestJar.jar");
                    System.out.println("我执行完成");
                    //System.out.println("执行结果："+result);
                } catch (Exception e) {
                    //future.cancel 会抛出这个
                    System.out.println("任务被中断。");
                }
                return  "OK";
            }
        });
        try {
            /*if(future.isDone()){
                System.out.println("future.isDone()");
            }*/
            String result = future.get(1, TimeUnit.SECONDS);
            System.out.println("result:"+result);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            //future.cancel(true);
            System.out.println("任务超时。");
        }finally {
            System.out.println("清理资源。");
            if(future.cancel(false)){
                return "失败";
            }
            //future.cancel(true);
        }


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

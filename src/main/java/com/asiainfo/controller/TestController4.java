package com.asiainfo.controller;

import com.sun.jmx.snmp.tasks.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by zhangqq20190306 on 2020/3/12.
 */
@Controller
@RequestMapping("/TestController4")
public class TestController4 {

    static ExecutorService executorService= Executors.newFixedThreadPool(1);

    @RequestMapping(value = "/testController.do", method = RequestMethod.GET)
    @ResponseBody
    public Object testController(HttpServletRequest request, HttpServletResponse response) {

        //System.out.println("线程池大小："+Runtime.getRuntime().availableProcessors()*1);

       /* int threadCount = ((ThreadPoolExecutor)executorService).getActiveCount();
        if(threadCount>0){
            System.out.println(threadCount);
            return "任务正在执行，请稍后";
        }*/


       executorService.execute(new Task() {
           @Override
           public void cancel() {
               System.out.println(Thread.currentThread().getName()+" 被取消");
           }

           @Override
           public void run() {
               execJar("C:\\Users\\zhangqq\\Desktop\\暂存箱\\TestJar.jar");
           }
       });

        int activeCount = ((ThreadPoolExecutor)executorService).getActiveCount();
        int waitCount = ((ThreadPoolExecutor)executorService).getQueue().size();
        int corePoolSize = ((ThreadPoolExecutor)executorService).getCorePoolSize();
        int poolSize = ((ThreadPoolExecutor)executorService).getPoolSize();

        Map result = new HashMap();
        result.put("activeCount",activeCount);
        result.put("waitCount",waitCount);
        result.put("corePoolSize",corePoolSize);
        result.put("poolSize",poolSize);
        return "成功"+result.toString();
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

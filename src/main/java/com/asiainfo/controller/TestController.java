package com.asiainfo.controller;

import com.asiainfo.component.TestComponent;
import com.asiainfo.util.PicUtil;
import com.asiainfo.util.TestUtil;
import com.asiainfo.util.TestUtil2;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by zhangqq20190306 on 2020/3/12.
 */
@Controller
@RequestMapping("/TestController")
public class TestController {

    //@Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    static ExecutorService executorService= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);

    @Autowired
    private Environment environment;

    @Autowired
    private TestComponent testComponent;

    @Autowired
    private TestUtil testUtil;

    @RequestMapping(value = "/testController2.do", method = RequestMethod.GET)
    @ResponseBody
    public Object testController2(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("环境："+ Arrays.toString(environment.getActiveProfiles()));
        testComponent.test();
        testUtil.test();
        new TestUtil2().test();
        return "成功";
    }

    @RequestMapping(value = "/testController.do", method = RequestMethod.GET)
    @ResponseBody
    public Object testController(HttpServletRequest request, HttpServletResponse response) {

        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() {
                try {
                    //线程休息5s
                    TimeUnit.SECONDS.sleep(10);
                    System.out.println("我执行完成");
                } catch (InterruptedException e) {
                    //future.cancel 会抛出这个
                    System.out.println("任务被中断。");
                }
                return  "OK";
            }
        });
        try {
            String result = future.get(1, TimeUnit.SECONDS);
            System.out.println("result:"+result);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            //future.cancel(true);
            System.out.println("任务超时。");
        }finally {
            System.out.println("清理资源。");
            future.cancel(false);
            //Thread.interrupted();
        }


        return "成功";
    }


    public  List<AnalyzeResponse.AnalyzeToken> addrAnalyze(String addr_analyze) {
        //先用ES分词器来分词。
        //AnalyzeRequestBuilder hanlpRequest = new AnalyzeRequestBuilder(elasticsearchTemplate.getClient(), AnalyzeAction.INSTANCE,"test",addr_analyze);
        AnalyzeRequestBuilder hanlpRequest = new AnalyzeRequestBuilder(elasticsearchTemplate.getClient(), AnalyzeAction.INSTANCE);
        hanlpRequest.setText(addr_analyze);
        hanlpRequest.setAnalyzer("ik_smart");
        List<AnalyzeResponse.AnalyzeToken> tokens = hanlpRequest.execute().actionGet().getTokens();
        return tokens;
    }


    @RequestMapping(value = "/analyzerWord.do", method = RequestMethod.GET)
    @ResponseBody
    public Object analyzerWord(HttpServletRequest request, HttpServletResponse response) {

        String addr_full = request.getParameter("addr_full");
        Map result = new HashMap();
        List list = addrAnalyze(addr_full);
        result.put("code","0");
        result.put("msg","success");
        result.put("list",list);
        return result;
    }


    @RequestMapping(value = "/createfile.do", method = RequestMethod.POST)
    @ResponseBody
    public Object createfile(HttpServletRequest request, HttpServletResponse response) {

        Map result = new HashMap();
        Map param = request.getParameterMap();
        System.out.println(param.toString());
        result.put("code","0");
        result.put("msg","success");

       return result;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String upLoadImg(MultipartFile file, HttpServletRequest request) throws IOException {
        String path = request.getSession().getServletContext().getRealPath("upload");
        String fileName = file.getOriginalFilename();// 获取到上传文件的名字
        // file.getSize();获取到上传文件的大小
        File dir = new File(path, fileName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // MultipartFile自带的解析方法
        file.transferTo(dir);
        String directory = dir.getCanonicalPath();
        System.out.println("directory:" + directory);
        return "/upload" + "/" + fileName;
    }


    @RequestMapping("/insertProduct.do")
    public ModelAndView insertProduct(HttpServletRequest request,
                                      @RequestParam MultipartFile image1,
                                      @RequestParam MultipartFile[] image2)throws Exception{
        String name = (String) request.getSession().getAttribute("loginName");
        if(true){
            System.out.println("--------------------上传商品后台-------------------------");

            String imgNumber1 = getParameterCheck(request, "imgNumber1");// 接收首页图片数
            String imgNumber2 = getParameterCheck(request, "imgNumber2");// 接收banner图片数
            System.out.println("图片数："+imgNumber1+","+imgNumber2);


            String imageRelativePath1 = new String();
            if (null != imgNumber1) { //判断首页图片是否为空
                if (null == image1.getContentType()
                        || image1.getContentType().split("/") == null) {
                    imageRelativePath1 = String.valueOf(System.currentTimeMillis())
                            + ".jpg";//
                } else {
                    if (image1.getContentType().split("/").length > 1) {
                        imageRelativePath1 = String.valueOf(System
                                .currentTimeMillis())
                                + "."
                                + image1.getContentType().split("/")[1];//
                    } else {
                        imageRelativePath1 = String.valueOf(System
                                .currentTimeMillis()) + ".jpg";//
                    }
                }
                System.out.println("首页图片路径："+imageRelativePath1);

            }


            /**
             * 上传banner图片
             */
            if (true) { //判断首页图片是否为空
                if (null != imgNumber2) {  //判断banber图片是否为空
                    int numb2 = Integer.parseInt(imgNumber2);
                    String[] imageRelativePath2 = new String[numb2];
                    for (int i = 0; i < image2.length; i++) {

                        if (null == image2[i].getContentType()
                                || image2[i].getContentType().split("/") == null) {
                            imageRelativePath2[i] = String.valueOf(System
                                    .currentTimeMillis()) + ".jpg";//
                        } else {
                            if (image2[i].getContentType().split("/").length > 1) {
                                imageRelativePath2[i] = String.valueOf(System
                                        .currentTimeMillis())
                                        + "."
                                        + image2[i].getContentType().split("/")[1];//

                            } else {
                                imageRelativePath2[i] = String.valueOf(System
                                        .currentTimeMillis()) + ".jpg";//
                            }
                        }


                        PicUtil.singleFileUpload(image2[i]);

                        System.out.println("banner图片:"+imageRelativePath2[i]);
                    }
                }
            }
            return new ModelAndView("mng/addProductSuccess");
        }
        return new ModelAndView("mng/mngLogin");
    }

    /**
     * 判断参数是否为空
     * @param request
     * @param key
     * @return
     */
    private String getParameterCheck(HttpServletRequest request, String key) {
        String Value = request.getParameter(key);
        if(null==Value||Value.equals("")){
            Value=null;
        }
        return Value;
    }


}

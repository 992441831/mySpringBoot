package com.asiainfo.demo.picUpload;


import net.sf.json.JSONObject;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HttpClient工具类
 *
 * @return
 * @author SHANHY
 * @create 2015年12月18日
 */
public class HttpClientUtil {

    static final int timeOut = 10 * 1000;

    private static CloseableHttpClient httpClient = null;

    private final static Object syncLock = new Object();

    private static void config(HttpRequestBase httpRequestBase) {
        // 设置Header等
        // httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
        // httpRequestBase
        // .setHeader("Accept",
        // "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        // httpRequestBase.setHeader("Accept-Language",
        // "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
        // httpRequestBase.setHeader("Accept-Charset",
        // "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");

        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeOut)
                .setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * 获取HttpClient对象
     *
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static CloseableHttpClient getHttpClient(String url) {
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        if (httpClient == null) {
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = createHttpClient(200, 40, 100, hostname, port);
                }
            }
        }
        return httpClient;
    }

    /**
     * 创建HttpClient对象
     *
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static CloseableHttpClient createHttpClient(int maxTotal,
                                                       int maxPerRoute, int maxRoute, String hostname, int port) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory> create().register("http", plainsf)
                .register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                                        int executionCount, HttpContext context) {
                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler).build();

        return httpClient;
    }

    private static void setPostParams(HttpPost httpost,
                                      Map<String, Object> params) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * GET请求URL获取内容
     *
     * @param url
     * @return
     * @author SHANHY
     * @throws IOException
     * @create 2015年12月18日
     */
    public static String post(String url, Map<String, Object> params) throws IOException {
        HttpPost httppost = new HttpPost(url);
        config(httppost);
        setPostParams(httppost, params);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(httppost,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            return result;
        } catch (IOException e) {
//          e.printStackTrace();
            throw e;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String mypost(String url, Map<String, Object> params,Map<String,String> headMap) throws IOException {
        HttpPost httppost = new HttpPost(url);
        if (headMap != null && headMap.size()>0){
            for (String key:headMap.keySet()){
                httppost.setHeader(key,headMap.get(key));
            }
        }
        config(httppost);

        setPostParams(httppost, params);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(httppost,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            return result;
        } catch (IOException e) {
//          e.printStackTrace();
            throw e;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String doPost2020(String urlStr,Map textMap,Map<String,String> headMap,String filepath) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        //请求处理页面
        HttpPost httppost = new HttpPost( urlStr );
        //创建待处理的文件
        FileBody file = new FileBody(new File(filepath));


        //对请求的表单域进行填充
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("file", file);

        //StringEntity s = new StringEntity(jsonObject.toString());

        if(textMap!=null ){
            Iterator it = textMap.entrySet().iterator();
            while(it.hasNext() ){
                Map.Entry entry = (Map.Entry)it.next();
                String key =  (String) entry.getKey();
                //String value = (String)entry.getValue();
                //创建待处理的表单域内容文本
                StringBody bodyValue = new StringBody(entry.getValue().toString());
                reqEntity.addPart(key , bodyValue);
            }
        }
        if (headMap != null && headMap.size()>0){
            for (String key:headMap.keySet()){
                httppost.setHeader(key,headMap.get(key));
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
        return backData.toString();
    }






    //json入参 post请求方式
    public static JSONObject doPost(String url, JSONObject json, String xAppId, String xAppKey){

        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-App-Id", xAppId);
        httpPost.addHeader("X-App-Key", xAppKey);
        String charSet = "UTF-8";
        JSONObject response = null;
        try {
            StringEntity entity = new StringEntity(json.toString(), charSet);
//            s.setContentEncoding("GB2312");
//            s.setContentType("application/json");//发送json数据需要设置contentType
            httpPost.setEntity(entity);
            HttpResponse res = httpclient.execute(httpPost);
            if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                String result = EntityUtils.toString(res.getEntity());// 返回json格式：
                response = JSONObject.fromObject(result);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }


    /**
     * GET请求URL获取内容
     *
     * @param url
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static String get(String url) {
        HttpGet httpget = new HttpGet(url);
        config(httpget);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(httpget,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getIn5s(String url) {
        HttpGet httpget = new HttpGet(url);
        //config(httpget);
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(5000)
                .setConnectTimeout(5000)
                .setSocketTimeout(5000).build();
        httpget.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(httpget,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * GET请求URL获取内容
     *
     * @param url
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static String getByParam(String url,Map param,Map<String,String> headMap) {
        StringBuilder urlWithParam=new StringBuilder(url+"?");
        Set<String> keySet = param.keySet();
        int count =0;
        for (String key : keySet) {
            if(count==0){
                urlWithParam.append(key+"="+param.get(key));
            }else{
                urlWithParam.append("&"+key+"="+param.get(key));
            }
            count++;
        }
        HttpGet httpget = new HttpGet(urlWithParam.toString());
        if (headMap != null && headMap.size()>0){
            for (String key:headMap.keySet()){
                httpget.setHeader(key,headMap.get(key));
            }
        }
        config(httpget);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(urlWithParam.toString()).execute(httpget,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            System.out.println("ori result:"+result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * GET请求URL获取内容
     *
     * @param url
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static String deleteByParam(String url,Map param,Map<String,String> headMap) {
        StringBuilder urlWithParam=new StringBuilder(url+"?");
        Set<String> keySet = param.keySet();
        int count =0;
        for (String key : keySet) {
            if(count==0){
                urlWithParam.append(key+"="+param.get(key));
            }else{
                urlWithParam.append("&"+key+"="+param.get(key));
            }
            count++;
        }
        HttpDelete httpDelete = new HttpDelete(urlWithParam.toString());
        if (headMap != null && headMap.size()>0){
            for (String key:headMap.keySet()){
                httpDelete.setHeader(key,headMap.get(key));
            }
        }
        config(httpDelete);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(urlWithParam.toString()).execute(httpDelete,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            System.out.println("ori result:"+result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static final int cache = 10 * 1024;
    /**
     * GET请求URL获取内容
     *
     * @param url
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static String getByParam1(String url,Map param,Map<String,String> headMap,String name) {
        //HttpClient client = HttpClients.createDefault();
        StringBuilder urlWithParam=new StringBuilder(url+"?");
        Set<String> keySet = param.keySet();
        int count =0;
        for (String key : keySet) {
            if(count==0){
                urlWithParam.append(key+"="+param.get(key));
            }else{
                urlWithParam.append("&"+key+"="+param.get(key));
            }
            count++;
        }
        HttpGet httpget = new HttpGet(urlWithParam.toString());
        if (headMap != null && headMap.size()>0){
            for (String key:headMap.keySet()){
                httpget.setHeader(key,headMap.get(key));
            }
        }
        config(httpget);
        CloseableHttpResponse response = null;
        try {
//            response = getHttpClient(urlWithParam.toString()).execute(httpget,
//                    HttpClientContext.create());
            HttpClientBuilder clientBuilder = HttpClients.custom();
            clientBuilder.disableContentCompression();
            HttpClient client = clientBuilder.build();
            HttpResponse response1 = client.execute(httpget);
            HttpEntity entity = response1.getEntity();
            InputStream is = entity.getContent();


            String filepath = "D://"+name;
            File file = new File(filepath);
            file.getParentFile().mkdirs();
            FileOutputStream fileout = new FileOutputStream(file);
            /**
             * 根据实际运行效果 设置缓冲区大小
             */
            byte[] buffer = new byte[cache];
            int ch = 0;
            while ((ch = is.read(buffer)) != -1) {
                fileout.write(buffer, 0, ch);
            }
            is.close();
            fileout.flush();
            fileout.close();


            EntityUtils.consume(entity);
            return "创建成功";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        // URL列表数组
        String[] urisToGet = {
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497" };

        long start = System.currentTimeMillis();
        try {
            int pagecount = urisToGet.length;
            ExecutorService executors = Executors.newFixedThreadPool(pagecount);
            CountDownLatch countDownLatch = new CountDownLatch(pagecount);
            for (int i = 0; i < pagecount; i++) {
                HttpGet httpget = new HttpGet(urisToGet[i]);
                config(httpget);
                // 启动线程抓取
                executors
                        .execute(new GetRunnable(urisToGet[i], countDownLatch));
            }
            countDownLatch.await();
            executors.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("线程" + Thread.currentThread().getName() + ","
                    + System.currentTimeMillis() + ", 所有线程已完成，开始进入下一步！");
        }

        long end = System.currentTimeMillis();
        System.out.println("consume -> " + (end - start));
    }

    static class GetRunnable implements Runnable {
        private CountDownLatch countDownLatch;
        private String url;

        public GetRunnable(String url, CountDownLatch countDownLatch) {
            this.url = url;
            this.countDownLatch = countDownLatch;
        }

       
        public void run() {
            try {
                System.out.println(HttpClientUtil.get(url));
            } finally {
                countDownLatch.countDown();
            }
        }
    }
}

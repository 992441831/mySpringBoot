package com.asiainfo.demo.picUpload;

import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/FileSystemController")
public class fileServer {

    //private final String url = "http://dfs.sh.ctc.com:8997/";
    private final String url = "http://10.145.219.91:8997/";
    private final String username = "te_o_gis";
    private final String password = "7NL";
    
    @RequestMapping(value = "/checkFileExist.do", method = RequestMethod.GET)
    @ResponseBody
    public Map fileExistence(HttpServletResponse response) throws ParseException {
        String timeStamp = getTimeStamp();
        String md5Token = getMd5Token(timeStamp,password);
        Map<String,String> headMap = new HashMap<>();
        headMap.put("webctdfs-username",username);
        headMap.put("webctdfs-token",md5Token);
        headMap.put("webctdfs-timestamp",timeStamp);
        String purdoPath = url+"webctdfs/existence/dfs/te_o_gis/222.txt";
        Map param = new HashMap();
        String strResult1 = HttpClientUtil.getByParam(purdoPath,param,headMap);

        Map resultMap = new HashMap();
        resultMap.put("strResult",strResult1);
        resultMap.put("msg","返回成功！");
        return resultMap;
    }

    @RequestMapping(value = "/listFileInfo.do", method = RequestMethod.GET)
    @ResponseBody
    public Map listFileInfo(HttpServletResponse response) throws ParseException {
        String timeStamp = getTimeStamp();
        String md5Token = getMd5Token(timeStamp,password);
        Map<String,String> headMap = new HashMap<>();
        headMap.put("webctdfs-username",username);
        headMap.put("webctdfs-token",md5Token);
        headMap.put("webctdfs-timestamp",timeStamp);
        String purdoPath = url+"webctdfs/list/dfs/te_o_gis";
        Map param = new HashMap();

        String strResult1 = HttpClientUtil.getByParam(purdoPath,param,headMap);
        strResult1 = strResult1.replace("\\","");
        strResult1 = strResult1.replace("\"[","[");
        strResult1 = strResult1.replace("]\"","]");
        System.out.println("strResult1:"+strResult1);

        JSONObject json2 = JSON.parseObject(strResult1,JSONObject.class);

        Map resultMap = new HashMap();
        resultMap.put("strResult",json2);
        resultMap.put("msg","返回成功！");
        return resultMap;
    }

    @RequestMapping(value = "/createDirectory.do", method = RequestMethod.GET)
    @ResponseBody
    public Map createDirectory(HttpServletResponse response) throws ParseException {
        String timeStamp = getTimeStamp();
        String md5Token = getMd5Token(timeStamp,password);
        Map<String,String> headMap = new HashMap<>();
        headMap.put("webctdfs-username",username);
        headMap.put("webctdfs-token",md5Token);
        headMap.put("webctdfs-timestamp",timeStamp);
        String purdoPathPost = url+"webctdfs/directory/dfs/te_o_gis/address_picture";
        Map param1 = new HashMap();
        String strResult1 = null;
        try {
            strResult1 = HttpClientUtil.mypost(purdoPathPost,param1,headMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map resultMap = new HashMap();
        resultMap.put("strResult",strResult1);
        resultMap.put("msg","返回成功！");
        return resultMap;
    }

    @RequestMapping(value = "/createfile.do", method = RequestMethod.GET)
    @ResponseBody
    public Map createfile(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        String timeStamp = getTimeStamp();
        String md5Token = getMd5Token(timeStamp,password);
        Map<String,String> headMap = new HashMap<>();
        headMap.put("webctdfs-username",username);
        headMap.put("webctdfs-token",md5Token);
        headMap.put("webctdfs-timestamp",timeStamp);
        String purdoPathPost = url+"webctdfs/file/dfs/te_o_gis";
        String strPath= "D:\\Txt\\"+name;
        Map param = new HashMap();
        Map param1 = new HashMap();
        param1.put("fileName","liyi");
        param1.put("overwrite",true);
        JSONObject mapResult = JSONObject.fromObject(param1);
        param.put("params",mapResult);

        String strResult1 = null;
        try {
            strResult1 = HttpClientUtil.doPost2020(purdoPathPost,param,headMap,strPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map resultMap = new HashMap();
        resultMap.put("strResult",strResult1);
        resultMap.put("msg","返回成功！");
        return resultMap;
    }

    @RequestMapping(value = "/getfile.do", method = RequestMethod.GET)
    @ResponseBody
    public Map getfile(HttpServletResponse response) {
        String timeStamp = getTimeStamp();
        String md5Token = getMd5Token(timeStamp,password);
        Map<String,String> headMap = new HashMap<>();
        headMap.put("webctdfs-username",username);
        headMap.put("webctdfs-token",md5Token);
        headMap.put("webctdfs-timestamp",timeStamp);
        String purdoPath = url+"webctdfs/content/dfs/te_o_gis/1111.txt";
        Map param = new HashMap();
        String strResult1 = HttpClientUtil.getByParam(purdoPath,param,headMap);

        Map resultMap = new HashMap();
        resultMap.put("strResult",strResult1);
        resultMap.put("msg","返回成功！");
        return resultMap;
    }


    @RequestMapping(value = "/deletefile.do", method = RequestMethod.GET)
    @ResponseBody
    public Map deletefile(HttpServletRequest Request) {
        String address_id = Request.getParameter("address_id");
        String timeStamp = getTimeStamp();
        String md5Token = getMd5Token(timeStamp,password);
        Map<String,String> headMap = new HashMap<>();
        headMap.put("webctdfs-username",username);
        headMap.put("webctdfs-token",md5Token);
        headMap.put("webctdfs-timestamp",timeStamp);
        String purdoPath = url+"webctdfs/file/dfs/te_o_gis/"+address_id;
        Map param = new HashMap();
        String strResult1 = HttpClientUtil.deleteByParam(purdoPath,param,headMap);

        Map resultMap = new HashMap();
        resultMap.put("strResult",strResult1);
        resultMap.put("msg","返回成功！");
        return resultMap;
    }

    @RequestMapping(value = "/downloadFile.do", method = RequestMethod.GET)
    @ResponseBody
    public Map downloadFile(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        String name = request.getParameter("name");
        String timeStamp = getTimeStamp();
        String md5Token = getMd5Token(timeStamp,password);
        Map<String,String> headMap = new HashMap<>();
        headMap.put("webctdfs-username",username);
        headMap.put("webctdfs-token",md5Token);
        headMap.put("webctdfs-timestamp",timeStamp);
        System.out.println(name);
        String purdoPath = url+"webctdfs/file/dfs/te_o_gis/"+name;
        Map param = new HashMap();

        String strResult1 = HttpClientUtil.getByParam1(purdoPath,param,headMap,name);

        Map resultMap = new HashMap();
        resultMap.put("strResult",strResult1);
        resultMap.put("msg","返回成功！");
        return resultMap;
    }



    public static void createUserCre(String id) {
        String url = "http://admin:11a8ff57440f35baead7a3cc8a21ec2c44@172.16.91.121:8888/jenkins/credentials/store/system/domain/_/createCredentials?";
        HttpPost httpPost = new HttpPost(url);

        CloseableHttpClient client = HttpClients.createDefault();
        String respContent = null;
        JSONObject jsonParam = new JSONObject();

        JSONObject credentialsJsonParam = new JSONObject();

        credentialsJsonParam.put("scope", "GLOBAL");
        //注意，如果ID一样的话，插入失败
        credentialsJsonParam.put("id", id);
        credentialsJsonParam.put("username", "abc520");
        credentialsJsonParam.put("password", "123456");
        credentialsJsonParam.put("description", "hello world jenkins hellow");
        credentialsJsonParam.put("$class", "com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl");

        jsonParam.put("credentials", credentialsJsonParam);
        jsonParam.put("", "0");

        //logger.info("=============:\t" + jsonParam.toString());
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.addTextBody("json", jsonParam.toString(), ContentType.MULTIPART_FORM_DATA);

        HttpEntity multipart = builder.build();

        HttpResponse resp = null;
        try {
            httpPost.setEntity(multipart);
            resp = client.execute(httpPost);

            //注意，返回的结果的状态码是302，非200
            if (resp.getStatusLine().getStatusCode() == 302) {
                HttpEntity he = resp.getEntity();
                //logger.info("----------------123----------666666---");
                respContent = EntityUtils.toString(he, "UTF-8");
            }
        } catch (Exception e) {
            //logger.error(e.getMessage());
        }

//        logger.info("=========================:\t" + respContent);
//        logger.info("=========================:\t" + resp.getStatusLine().getStatusCode());

    }

    /**
     * 测试上传图片
     *
     */
    public static void testUploadImage(){
        String url = "http://xxxtest/Api/testUploadModelBaking";
        String fileName = "e:/username/textures/antimap_0017.png";
        Map<String, String> textMap = new HashMap<String, String>();
        //可以设置多个input的name，value
        textMap.put("name", "testname");
        textMap.put("type", "2");
        //设置file的name，路径
        Map<String, String> fileMap = new HashMap<String, String>();
        fileMap.put("upfile", fileName);
        String contentType = "";//image/png
        String ret = formUpload(url, textMap, fileMap,contentType);
        System.out.println(ret);
        //{"status":"0","message":"add succeed","baking_url":"group1\/M00\/00\/A8\/CgACJ1Zo-LuAN207AAQA3nlGY5k151.png"}
    }

    /**
     * 上传图片
     * @param urlStr
     * @param textMap
     * @param fileMap
     * @param contentType 没有传入文件类型默认采用application/octet-stream
     * contentType非空采用filename匹配默认的图片类型
     * @return 返回response数据
     */
    @SuppressWarnings("rawtypes")
    public static String formUpload(String urlStr, Map<String, String> textMap,
                                    Map<String, String> fileMap,String contentType) {
        String res = "";
        HttpURLConnection conn = null;
        // boundary就是request头和上传文件内容的分隔符
        String BOUNDARY = "---------------------------123821742118716";
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            // conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // text
            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
            }
            // file
            if (fileMap != null) {
                Iterator iter = fileMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    File file = new File(inputValue);
                    String filename = file.getName();

                    //没有传入文件类型，同时根据文件获取不到类型，默认采用application/octet-stream
                    contentType = new MimetypesFileTypeMap().getContentType(file);
                    //contentType非空采用filename匹配默认的图片类型
                    if(!"".equals(contentType)){
                        if (filename.endsWith(".png")) {
                            contentType = "image/png";
                        }else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".jpe")) {
                            contentType = "image/jpeg";
                        }else if (filename.endsWith(".gif")) {
                            contentType = "image/gif";
                        }else if (filename.endsWith(".ico")) {
                            contentType = "image/image/x-icon";
                        }
                    }
                    if (contentType == null || "".equals(contentType)) {
                        contentType = "application/octet-stream";
                    }
                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                    out.write(strBuf.toString().getBytes());
                    DataInputStream in = new DataInputStream(new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                }
            }
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            // 读取返回数据
            StringBuffer strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            System.out.println("发送POST请求出错。" + urlStr);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }


    /**
     * 获取MD5的token
     * @param timeStamp
     * @return
     */
    public String getMd5Token(String timeStamp,String password) {
        String webctdfsToken = password + timeStamp;
        return DigestUtils.md5DigestAsHex(webctdfsToken.getBytes());
    }

    /**
     * 获取小文件服务器时间戳
     * @return
     */
    public  String getTimeStamp() {
        Map param = new HashMap();
        String purdoPath = url+"webctdfs/existence/dfs/te_o_gis/test.log";
        String strResult = HttpClientUtil.getByParam(purdoPath,param,null);
        JSONObject mapResult = JSONObject.fromObject(strResult);
        return date2TimeStamp(mapResult.get("time").toString(), "yyyy-MM-dd HH:mm:ss");
    }


    /**
     * 取得当前时间戳（精确到秒）
     * @return
     */
    public static String timeStamp(){
        long time = System.currentTimeMillis();
        String t = String.valueOf(time/1000);
        return t;
    }

    /**
     * 时间戳转换成日期格式字符串
     * @param seconds 精确到秒的字符串
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }

    /**
     * 日期格式字符串转换成时间戳
     * @param date_str 字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String date2TimeStamp(String date_str,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date_str).getTime()/1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

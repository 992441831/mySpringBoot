package com.asiainfo.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

//图片文件转Base64编码

public class TestBase64 {
    /**
     * <p>将文件转成base64 字符串</p>
     *
     * @param path 文件路径
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new BASE64Encoder().encode(buffer);
    }

    /**
     *      * <p>将base64字符解码保存文件</p>
     *      
     */
    public static void decoderBase64File(String base64Code, String targetPath) throws Exception {
        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    /**
     *      * <p>将base64字符保存文本文件</p>
     *      
     */
    public static void toFile(String base64Code, String targetPath) throws Exception {
        byte[] buffer = base64Code.getBytes();
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }


    static  String  str = "" +
            "iVBORw0KGgoAAAANSUhEUgAAABIAAAASCAYAAABWzo5XAAABs0lEQVQ4T2NkoBJgpJI5DFgNeh0RIfXn79+U/4yM/gwMDJoM////YWBkvPX/37/NbCwss0VXrHiG7gAMg16Ehfn+Z2Wdz+LoJMxqac3AJCXNwPDvL8O/Fy8Yfh8/xvB73553DL9/p0itXr0e2TAUg56FhQUwCgqu5ywoZmCWk8fq679PHjN87+th+P/+XaDUqlUbYIrgBj3y85Ni4eG5zFXXJMQsKws35GtrE5jNXV0HF/v7+DHDt6a6d6z//unCvAk36HloaB2Lu0cjR2QMiks+J8aC+bzzF6OI/1y+lOH3zu31kqtXg22CG/QsLOwMV12TMbOiIlEG/b1/H+Sqs1KrVpmgG/SdZ+ZcDkY2NqIM+v/rF8OX9OQfUqtWcRJl0NfWRrDDkcMIpBGfQWe56pqM0L2GK8Hi9BoosFndPRvZI6NR9GKLNZACnIENSs2/mZguc9U3CTHLIEc/ptdAaelbI47oB9lCdILs72H4//ZtEHLqxplFWB2dhVksrRiYpGXAXv339AnDn+PHGH4Rk0VggQPy5q8/f1IZmZj8GBgYtKDi1/7/+7eJ6ExLbrFCtfIIAPHw1BP0aQZiAAAAAElFTkSuQmCC"
            ;

    public static void main(String[] args) {
        try {
            //图片转base64
            String base64Code = encodeBase64File("D:\\images\\pc\\uploadPc");
            System.out.println(base64Code);
            //toFile(base64Code, "D:\\images\\pc\\three.txt");
            //base64转图片
            decoderBase64File(str, "D:\\images\\pc\\test1.jpg");
            Path path = Paths.get("D://images//pc");
            System.out.println(path.toString());
            System.out.println(path.toString()+"/"+(1+1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}

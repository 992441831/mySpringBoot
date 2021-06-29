package com.asiainfo.util;

import org.springframework.beans.factory.annotation.Value;

public class TestUtil2 {
    public static void main(String[] args) {

    }

    @Value("${config.test}")
    private String test;

    public  void test(){
        System.out.println("1111:"+test);
    }
}

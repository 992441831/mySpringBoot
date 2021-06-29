package com.asiainfo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class TestUtil {

    @Autowired
    private Environment environment;

    public void test(){
        System.out.println(Arrays.toString(environment.getActiveProfiles()));
    }

}

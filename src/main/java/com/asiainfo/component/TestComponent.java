package com.asiainfo.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

@Component
public class TestComponent {

    private Environment environment;

    public TestComponent(Environment environment) {
        this.environment = environment;
    }

    public void test(){
        System.out.println(Arrays.toString(environment.getActiveProfiles()));
    }
}

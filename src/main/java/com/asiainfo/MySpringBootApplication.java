package com.asiainfo;

import com.asiainfo.component.TestComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class MySpringBootApplication {



	public static void main(String[] args) {
		SpringApplication.run(MySpringBootApplication.class, args);

	}

}

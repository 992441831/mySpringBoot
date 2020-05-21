package com.asiainfo;

import com.asiainfo.elasticsearchTest.User;
import javafx.application.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
//@SpringBootTest
public class MySpringBootApplicationTests {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Test
    public void contextLoads() {

        List list = new ArrayList();
        for (int i = 0; i < 5000; i++) {

            User user = new User();
            user.setId(1001L + i);
            user.setLevel(i % 50 + 10);
            user.setAddr_full("张三" + i);
            user.setALIAS("足球、篮球、听音乐");
            user.setROAD_NAME("足球、篮球、听音乐");
            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withObject(user)
                    .build();

            list.add(indexQuery);
        }

        Long start = System.currentTimeMillis();
        this.elasticsearchTemplate.bulkIndex(list);
        System.out.println("用时：" + (System.currentTimeMillis() - start)); //用时： 7836
    }

}

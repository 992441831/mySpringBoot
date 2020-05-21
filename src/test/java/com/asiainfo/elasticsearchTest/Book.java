package com.asiainfo.elasticsearchTest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * Created by zhangqq20190306 on 2020/1/5.
 */


@Data
@AllArgsConstructor
//@NoArgsConstructor
@Document(indexName = "book", type = "_doc")
public class Book {


}

package com.asiainfo.elasticsearchTest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


/**
 * Created by zhangqq20190306 on 2019/11/19.
 */
//@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "es_address_index", type = "ADDRESS", shards = 3, replicas = 0)
public class User {

     @Id
    private Long id;

    @Field(store = true,type = FieldType.Text,name = "ADDR_FULL1")
    private String addr_full;

    @Field(store = true,type = FieldType.Text,name = "level")
    private Integer LEVEL;

    @Field(type = FieldType.Text,analyzer = "hanlp_synonym",searchAnalyzer = "hanlp_synonym",index = true)
    private String ALIAS;

    @Field(type = FieldType.Text,analyzer = "hanlp_synonym",searchAnalyzer = "hanlp_synonym",index = true)
    private String ROAD_NAME;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddr_full() {
        return addr_full;
    }

    public void setAddr_full(String addr_full) {
        this.addr_full = addr_full;
    }

    public Integer getLEVEL() {
        return LEVEL;
    }

    public void setLevel(Integer LEVEL) {
        this.LEVEL = LEVEL;
    }

    public String getALIAS() {
        return ALIAS;
    }

    public void setALIAS(String ALIAS) {
        this.ALIAS = ALIAS;
    }

    public String getROAD_NAME() {
        return ROAD_NAME;
    }

    public void setROAD_NAME(String ROAD_NAME) {
        this.ROAD_NAME = ROAD_NAME;
    }
}

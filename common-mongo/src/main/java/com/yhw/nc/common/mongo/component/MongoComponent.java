package com.yhw.nc.common.mongo.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yhw
 */
@Component
public class MongoComponent {

    @Resource
    MongoTemplate mongoTemplate;

    public void createCollection(String collectionName){
        if(!mongoTemplate.collectionExists(collectionName)){
            mongoTemplate.createCollection(collectionName);
        }
    }

    public void insert(Map<String,Object> map, String collectionName){
        Document document = new Document(map);
        getCollection(collectionName).insertOne(document);
    }

    public void insertMany(List<Map<String,Object>> list, String collectionName){
        List<Document> docList = list.stream().map(map->{
            return new Document(map);
        }).collect(Collectors.toList());
        getCollection(collectionName).insertMany(docList);
    }

    public MongoCollection<Document> getCollection(String collectionName){
        return mongoTemplate.getCollection(collectionName);
    }

    /**
     * CriteriaDefinition criteria = Criteria.where("column").is("parameter");//.and("createdAt").gt(DateUtil.parse("2019-10-05"))
     * Query query = Query.query(criteria);
     * @param query
     * @param collectionName
     * @return
     */
    public List<JSONObject> find(Query query, String collectionName){
//        mongoTemplate.remove(new Query(),"");//删除
        return mongoTemplate.find(query, JSONObject.class,collectionName);
    }

    /**
     * 分页查询
     * @param query
     * @param request
     * @param entityClass
     * @param collectionName
     * @param <T>
     * @return
     */
    public <T>  IPage<T>  getPage(Query query, PageRequest request,Class<T> entityClass, String collectionName){
        int count = (int) mongoTemplate.count(query, entityClass, collectionName);

        query.with(request);
        List<T> list = mongoTemplate.find(query, entityClass, collectionName);
        IPage<T> result = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(request.getPageNumber(), request.getPageSize(),count);
        if(CollUtil.isNotEmpty(list)){
            result.setRecords(list);
            return result;
        }
        return result;
    }
}
